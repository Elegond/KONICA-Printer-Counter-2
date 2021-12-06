package work.saretzki;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import work.saretzki.util.*;
import work.saretzki.net.lib.*;
import work.saretzki.gui.ServerGUI;
import work.saretzki.net.ServerNet;

public class Server extends ServerNet {
	public static Server s;
	public static List<Printer> printer = new ArrayList<Printer>();
	public static Thread pThread;
	private static final Runnable rP = () -> {printerThread();};

	private Server(int port) {
		super(port);
	}

	public static boolean printerRun = true;

	public static void l() {

		/* Test Printer */
		// addPrinter("192.168.1.", "552", new int[] { 171, 188 }, new String[] {
		// "test1", "test2" });

		// Server starten
		s = new Server(4444);

		// GUI starten
		if (!Main.ng) {
			Thread t = new Thread(()->{ServerGUI.l();});
			t.start();
		}

		/* Start Printer Schleife */

		
		pThread = new Thread(rP);
		pThread.start();

	}

	public static void addPrinter(String ip, String name, int[] f, String[] nameData) {
		/* Test Printer */
		Printer test = new Printer(ip, name, f, nameData);

		try {
			FileStorage f2Storage = new FileStorage(new File("./cfg/printer"));

			f2Storage.store(test.getName(),
					new savePrinter(test.getIP(), test.getName(), test.getFields(), test.getNameData()));
			System.out.println("Printer " + test.getName() + " saved");

		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		printerRun = false;

		/* Test Printer */
		while (pThread.isAlive()) {

		}
		pThread = new Thread(rP);
		pThread.start();

	}

	public static void delPrinter(String name) {
		try {
			FileStorage f2Storage = new FileStorage(new File("./cfg/printer"));
			f2Storage.remove(name);
			List<savePrinter> sPrinter = new ArrayList<savePrinter>();
			for (Printer sda : printer) {
				sPrinter.add(new savePrinter(sda.getIP(), sda.getName(), sda.getFields(), sda.getNameData()));

			}

			s.broadcastMessage(new Datapackage("RELOAD", sPrinter));
			System.out.println("Printer " + name + " removed");
		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		printerRun = false;
		while (pThread.isAlive()) {
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
			}
		}
		pThread = new Thread(rP);
		pThread.start();
	}

	private static void printerThread() {

		// Config auslesen
		File dir = new File("./cfg");
		if (!dir.exists())
			dir.mkdirs();
		printer.clear();
		try {
			FileStorage fStorage = new FileStorage(new File("./cfg/printer"));
			List<Object> pri = fStorage.getAllAsArrayList();
			for (Object Pr : pri) {
				if (Pr.getClass().getName().equals(savePrinter.class.getName())) {
					savePrinter save = (savePrinter) Pr;
					printer.add(new Printer(save.ip, save.name, save.f, save.nameData));
					System.out.println("Printer added");
				}
			}
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		List<savePrinter> sPrinter = new ArrayList<savePrinter>();
		for (Printer sda : printer) {
			sPrinter.add(new savePrinter(sda.getIP(), sda.getName(), sda.getFields(), sda.getNameData()));

		}

		s.broadcastMessage(new Datapackage("RELOAD", sPrinter));
		// Druckerabfragen starten

		while (printerRun) {

			for (Printer p : printer) {
				p.c();
				for (PrinterData pD : p.getPrinterDataList()) {
					System.out.println(pD.getName() + ": " + pD.getDif(p.getImports()));
					// s.broadcastMessage(new Datapackage("PRINTERDATA", pD));

				}

				System.out.println(p.getName());
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		printerRun = true;
		
		// example
		// Printer p1 = new Printer("192.168.1.189");
		// PrinterData pD1 = new PrinterData(PrinterData.getField(176813,
		// p1.getImports()), p1.getImports());
	}

	@Override
	public void preStart() {
		registerMethod("GET", new Exec() {

			@Override
			public void run(Datapackage pack, Socket socket) {

				if (pack.get(1).equals("Printer")) {
					List<savePrinter> sPrinter = new ArrayList<savePrinter>();
					for (Printer sda : printer) {
						sPrinter.add(new savePrinter(sda.getIP(), sda.getName(), sda.getFields(), sda.getNameData()));

					}
					System.out.println("Sending...");
					sendMessage(new Datapackage("REPLY", sPrinter), socket);
				} else if (pack.get(1).equals("PrinterData")) {
					List<sendDif> sendD = new ArrayList<sendDif>();
					for (Printer pr : printer) {
						for (PrinterData pData : pr.getPrinterDataList()) {
							System.out.println("SEND: " + pData.getName() + ": " + pData.getDif());
							sendD.add(new sendDif(pData.getName(), pData.getDif()));
						}
					}
					sendMessage(new Datapackage("REPLY", sendD), socket);
				}

			}
		});
		registerMethod("ADDPRINTER", new Exec() {
			@Override
			public void run(Datapackage pack, Socket socket) {
				if (pack.get(1).getClass().getName().equals(savePrinter.class.getName())) {
					savePrinter sp = (savePrinter) pack.get(1);
					addPrinter(sp.ip, sp.name, sp.f, sp.nameData);
					sendMessage(new Datapackage("REPLY", "OK"), socket);
				}
			}
		});
		registerMethod("DELPRINTER", new Exec() {
			@Override
			public void run(Datapackage pack, Socket socket) {
				delPrinter(pack.get(1).toString());
				sendMessage(new Datapackage("REPLY", "OK"), socket);
			}
		});

	}

	@Override
	public void onClientRegistered() {

	}

	@Override
	public void onClientRegistered(Datapackage msg, Socket socket) {

	}

	@Override
	public void onSocketRemoved(Socket socket) {

	}
}

class savePrinter implements Serializable {
	public String ip, name;
	public int[] f;
	public String[] nameData;

	savePrinter(String ip, String name, int[] f, String[] nameData) {
		this.ip = ip;
		this.name = name;
		this.f = f;
		this.nameData = nameData;
	}
}

class sendDif implements Serializable {
	public String ip, name;
	public int dif;

	sendDif(String name, int dif) {
		this.name = name;
		this.dif = dif;
	}
}