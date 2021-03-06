package work.saretzki.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.HashMap;


import work.saretzki.net.lib.*;

public abstract class ServerNet {

	private HashMap<String, Exec> idMethods = new HashMap<String, Exec>();

	private ServerSocket server;
	private int port;
	private ArrayList<Socket> clients;

	private Thread listeningThread;
	private Thread pingThread;

	private boolean autoRegisterEveryClient;
	
	private boolean muted;
	
	/**
	 * Executed the preStart()-Method,<br>
	 * creates a Server on <i>port</i><br>
	 * and starts the listening loop on its own thread.<br>
	 * <br>
	 * The servers stores every client connecting in a list<br>
	 * to make them reachable using <i>broadcastMessage()</i> method.
	 * The connecting to server will be kept alive by<br>
	 * sending a little datapackage every 30 seconds.
	 * The connecting will be encrypted using SSL (beta stage!)
	 */
	public ServerNet(int port) {
		this(port, true, true);
	}

	/**
	 * Executed the preStart()-Method,<br>
	 * creates a Server on <i>port</i><br>
	 * and starts the listening loop on its own thread.
	 * 
	 * @param port
	 *            the server shall work on
	 * @param autoRegisterEveryClient
	 *            whether every clients connecting<br>
	 *            shall be registered or not
	 * @param keepConnectionAlive
	 *            whether the server shall try everything to keep the<br>
	 *            connection alive by sending a little datapackage every 30
	 *            seconds
	 */
	public ServerNet(int port, boolean autoRegisterEveryClient, boolean keepConnectionAlive) {
		this.clients = new ArrayList<Socket>();
		this.port = port;
		this.autoRegisterEveryClient = autoRegisterEveryClient;
		this.muted = false;

	
		if (autoRegisterEveryClient) {
			registerLoginMethod();
		}
		preStart();

		start();

		if (keepConnectionAlive) {
			startPingThread();
		}
	}
	
	/**
	 * Sets whether the server shall print information or not. Exception and
	 * errors are always printed.
	 * 
	 * @param muted
	 *            <b>true</b> for no debug output at all, <b>false</b> for all
	 *            output. Default is false (and all output printed).
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	/**
	 * Executed while constructing the Server instance,<br>
	 * just before listening to data from the network starts.
	 */
	public abstract void preStart();

	/**
	 * Overwrite this method to react on a client registered (logged in)<br>
	 * to the server. That happens always, when a Datapackage<br>
	 * with identifier <i>_INTERNAL_LOGIN_</i> is received from a client.
	 */
	public abstract void onClientRegistered();

	/**
	 * Overwrite this method to react on a client registered (logged in)<br>
	 * to the server. That happens always, when a Datapackage<br>
	 * with identifier <i>_INTERNAL_LOGIN_</i> is received from a client.
	 */
	public abstract void onClientRegistered(Datapackage msg, Socket socket);

	/**
	 * Called whenever a bad or erroneous socket is removed<br>
	 * from the ArrayList of registered sockets.
	 * 
	 * @param socket
	 *            The socket that has been removed from the list
	 */
	public abstract void onSocketRemoved(Socket socket);

	/**
	 * Starts a thread pinging every client every 30 seconds to keep the
	 * connection alive.
	 */
	private void startPingThread() {
		pingThread = new Thread(new Runnable() {
			@Override
			public void run() {

				while (server != null) {
					try {
						Thread.sleep(30 * 1000);
					} catch (InterruptedException e) {
					}
					broadcastMessage(new Datapackage("_INTERNAL_PING_", "OK"));
				}

			}
		});
		pingThread.start();
	}

	/**
	 * Starts a thread listening for incoming connections and Datapackages by
	 * registered and non-registered clients
	 */
	private void startListening() {
		if (listeningThread == null && server != null) {
			listeningThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (server != null) {

						try {
							if (!muted)
								System.out.println("[Server] Waiting for connection" +  "...");
							final Socket tempSocket = server.accept();

							ObjectInputStream ois = new ObjectInputStream(tempSocket.getInputStream());
							Object raw = ois.readObject();

							if (raw instanceof Datapackage) {
								final Datapackage msg = (Datapackage) raw;
								if (!muted)
									System.out.println("[Server] Message received: " + msg);

								INNER_LOOP:
								for (final String current : idMethods.keySet()) {
									if (msg.id().equalsIgnoreCase(current)) {
										if (!muted)
											System.out.println(
													"[Server] Executing method for identifier '" + msg.id() + "'");
										new Thread(new Runnable() {
											public void run() {
												idMethods.get(current).run(msg, tempSocket);
											}
										}).start();
										break INNER_LOOP;
									}
								}

							}

						} catch(StreamCorruptedException e) {
							System.err.println("Verbindungsversuch aus unzul?ssiger Anwendung erkannt und geblockt.");
						} catch (EOFException e) {
							e.printStackTrace();
						} catch (IllegalBlockingModeException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}

					}
				}

			});

			listeningThread.start();
		}
	}

	/**
	 * Sends a Datapackage to a Socket
	 * 
	 * @param message
	 *            The Datapackage to be delivered
	 * @param socket
	 *            The Socket the Datapackage shall be delivered to
	 */
	public void sendMessage(Datapackage message, Socket socket) {
		try {
			// Nachricht senden
			if (!socket.isConnected()) {
				throw new Exception("Socket not connected.");
			}
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
		} catch (Exception e) {
			System.err.println("[SendMessage] Fehler: " + e.getMessage());

			// Bei Fehler: Socket aus Liste loeschen
			if (toBeDeleted != null) {
				toBeDeleted.add(socket);
			} else {
				clients.remove(socket);
				onSocketRemoved(socket);
			}
		}
	}

	private ArrayList<Socket> toBeDeleted;

	/**
	 * Broadcasts a Datapackage to every single logged-in socket,<br>
	 * one after another on the calling thread.<br>
	 * Every erroneous (unreachable etc.) socket is being removed in the end
	 * 
	 * @param message
	 *            The Datapackage to be broadcasted
	 * @return The number of reachable the Datapackage has been delivered to
	 */
	public int broadcastMessage(Datapackage message) {
		toBeDeleted = new ArrayList<Socket>();

		// Nachricht an alle Sockets senden
		for (Socket current : clients) {
			sendMessage(message, current);
		}

		// Alle Sockets, die fehlerhaft waren, im Anschluss loeschen
		for (Socket current : toBeDeleted) {
			clients.remove(current);
			onSocketRemoved(current);
		}

		toBeDeleted = null;

		return clients.size();
	}

	/**
	 * Registers an Executable to be executed by the server<br>
	 * on an incoming Datapackage has <i>identifier</i> as its identifier.
	 * 
	 * @param identifier
	 *            The identifier the Executable is triggered by
	 * @param executable
	 *            The Executable to be executed on arriving identifier
	 */
	public void registerMethod(String identifier, Exec executable) {
		if (identifier.equalsIgnoreCase("_INTERNAL_LOGIN_") && autoRegisterEveryClient) {
			throw new IllegalArgumentException("Identifier may not be '_INTERNAL_LOGIN_'. "
					+ "Since v1.0.1 the server automatically registers new clients. "
					+ "To react on new client registed, use the onClientRegisters() Listener by overwriting it.");
		} else {
			idMethods.put(identifier, executable);
		}
	}

	private void registerLoginMethod() {
		idMethods.put("_INTERNAL_LOGIN_", new Exec() {
			@Override
			public void run(Datapackage msg, Socket socket) {
				registerClient(socket);
				onClientRegistered(msg, socket);
				onClientRegistered();
			}
		});
	}

	/**
	 * Registers a new client. From now on this Socket will receive broadcast
	 * messages.
	 * 
	 * @param newClientSocket
	 *            The Socket to be registerd
	 */
	public void registerClient(Socket newClientSocket) {
		clients.add(newClientSocket);
	}

	private void start() {
		server = null;
		try {
				
			server = new ServerSocket(port);
			
		} catch (IOException e) {
			System.err.println("Error opening ServerSocket");
			e.printStackTrace();
		}
		startListening();
	}

	/**
	 * Interrupts the listening thread and closes the server
	 */
	public void stop() {
		if (listeningThread.isAlive()) {
			listeningThread.interrupt();
		}

		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return The number of connected clients
	 */
	public int getClientCount() {
		return clients.size();
	}
}


