package work.saretzki;

import java.io.File;
import java.io.IOException;
import java.util.List;
import work.saretzki.net.lib.*;
import work.saretzki.util.FileStorage;
import work.saretzki.gui.ClientGUI;
import work.saretzki.net.ClientNet;

public class Client extends ClientNet {
	public static Client c;
	private static String server;

	public static void l() {
		File dir = new File("./cfg");
		if (!dir.exists())
			dir.mkdirs();
		try {
			FileStorage fStorage = new FileStorage(new File("./cfg/client"));
			server = new String(fStorage.get("Server").toString());
			System.out.println(server);

		} catch (IllegalArgumentException e1) {
		} catch (IOException e1) {
		} catch (NullPointerException e) {
		}
		if (server != null) {
			connect();
		}
		Runnable r = () -> {
			new ClientGUI().l();
		};
		Thread t = new Thread(r);
		t.start();

	}

	public static void connect() {
		c = new Client(server, 4444);
		c.setMuted(false);
		c.start();
		/*
		 * try { Thread.sleep(5000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } Datapackage d =
		 * c.sendMessage("GET", "Printer"); if (d != null) { List<savePrinter> sPrinter
		 * = (List<savePrinter>) d.get(1); for (savePrinter sda : sPrinter) {
		 * System.out.println(sda.ip + " " + sda.name); } System.out.println("test"); }
		 * else {
		 * 
		 * System.out.println("error"); } // c.sendMessage("DELPRINTER", "190");
		 * 
		 * try { Thread.sleep(5000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } int[] f = { 293 }; String[]
		 * nd = { "testC" }; d = c.sendMessage(new Datapackage("ADDPRINTER", new
		 * savePrinter("192.168.1.190", "Farb", f, nd)));
		 * 
		 * if (d != null) {
		 * 
		 * System.out.println(d.get(1)); } else {
		 * 
		 * System.out.println("error"); } try { Thread.sleep(5000); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } d = c.sendMessage("GET", "Printer"); if (d != null) {
		 * List<savePrinter> sPrinter = (List<savePrinter>) d.get(1); for (savePrinter
		 * sda : sPrinter) { System.out.println(sda.ip + " " + sda.name); }
		 * System.out.println("test"); } else {
		 * 
		 * System.out.println("error"); }
		 */

	}

	public Client(String address, int port) {
		super(address, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionProblem() {

	}

	@Override
	public void onConnectionGood() {

	}

	@Override
	public void onReconnect() {

	}
}
