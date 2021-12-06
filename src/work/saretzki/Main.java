package work.saretzki;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import work.saretzki.util.WOL;

public class Main {

	public static final Logger logger = Logger.getLogger("log");
	private static File cfgDatei = new File(".//config.cfg");
	private static File logDatei;
	public static boolean ServerMode = false, debug = false, ng = false, log = true, ClientMode = true;

	public static void main(String[] args) throws IOException {
		System.out.println("Printer Counter Server/Client v0.3.4");
		logger.setLevel(Level.SEVERE);
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("--client") || args[i].equalsIgnoreCase("-c")) {
				ClientMode = true;
			} else if (args[i].equalsIgnoreCase("--server") || args[i].equalsIgnoreCase("-s")) {
				ServerMode = true;
				ClientMode = false;
			} else if (args[i].equalsIgnoreCase("--debug") || args[i].equalsIgnoreCase("-d")) {
				debug = true;
				logger.setLevel(Level.FINEST);
			} else if (args[i].equalsIgnoreCase("--nogui") || args[i].equalsIgnoreCase("-ng")) {
				ng = true;
			} else if (args[i].equalsIgnoreCase("--console") || args[i].equalsIgnoreCase("-con")) {
				log = false;
			} else if (args[i].equalsIgnoreCase("-t")) {
				try {
					int t = Integer.parseInt(args[i + 1]);
					i++;
					try {
						Thread.sleep(t * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (NumberFormatException e) {
					System.err.println(args[i + 1] + " ist keine Zahl");
				}

			} else {
				System.out.println(args[i]
						+ " Unknown use --client|-c, --server|-s, --debug|-d, --nogui|-ng, --console|-con, -t [Sekunden]");
			}
		} // Ende der Startparameter Schleife
		if (log)
			Log();
		
		if (ServerMode) {
			Thread t = new Thread(()-> {
					Server.l();
			});
			t.start();

		}

		if (ClientMode) {
			Thread t = new Thread(() -> {
				Client.l();
			});
			t.start();

			new WOL("192.168.1.255", "ff:ff:ff:ff:ff:ff");
		}

	}

	private static void Log() {

		if (cfgDatei.getParent().equals("."))
			logDatei = new File(".//debug.log");
		else
			logDatei = new File(cfgDatei.getParent() + ".//debug.log");
		try {
			System.setOut(new PrintStream(new FileOutputStream(logDatei, true)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void shutdown() throws RuntimeException, IOException {
		String shutdownCommand;
		String operatingSystem = System.getProperty("os.name");
		System.out.println(operatingSystem);
		if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
			shutdownCommand = "shutdown -h now";
		} else if (operatingSystem.startsWith("Win")) {
			shutdownCommand = "shutdown.exe -s -t 0";
		} else {
			throw new RuntimeException("Unsupported operating system.");
		}

		Runtime.getRuntime().exec(shutdownCommand);
		System.exit(0);
	}
}
