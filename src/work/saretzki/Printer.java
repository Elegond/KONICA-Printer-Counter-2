package work.saretzki;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Printer implements Serializable {
	private String ip, name;
	private Elements imports;
	private int[] f;
	public List<PrinterData> PData = new ArrayList<PrinterData>();
	transient private Document doc;
	private Map<String, String> loginCookies;
	private String[] nameData;
	public Printer(String ip, String name, int[] f,String[] nameData) {
		this.ip = ip;
		this.name = name;
		this.f=f;
		this.nameData=nameData;
		System.out.println("\nConnecting to " + ip + "...");
		Response res;
		try {
			res = Jsoup.connect("http://" + ip + "/wcd/ulogin.cgi")
					.data("func", "PSL_LP0_TOP", "AuthType", "None", "TrackType", "None", "PswcForm", "HtmlFlash",
							"Mode", "Public", "ViewMode", "Html", "BrowseMode", "Low", "Lang", "De")
					.method(Method.POST).execute();
			loginCookies = res.cookies();
			System.out.println(loginCookies.toString());
			doc = Jsoup.connect("http://" + ip + "/wcd/system_counter.xml").cookies(loginCookies).get();

			imports = doc.getAllElements();

			if (Main.logger.isLoggable(Level.ALL)) {
				System.out.println("\nElemente von " + ip + ": " + imports.size());
				int i = 0;
				for (Element link : imports) {
					System.out.println(i + "  " + link.ownText());
					i++;

				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		}
		for (int df=0;f.length>df;df++) {
			PData.add(new PrinterData(nameData[df],f[df], getImports()));
		}

	}

	public void c() {
		System.out.println("\nConnecting to " + ip + "...");
		Response res;
		try {
			if(loginCookies.isEmpty()) {
				res = Jsoup.connect("http://" + ip + "/wcd/ulogin.cgi")
						.data("func", "PSL_LP0_TOP", "AuthType", "None", "TrackType", "None", "PswcForm", "HtmlFlash",
								"Mode", "Public", "ViewMode", "Html", "BrowseMode", "Low", "Lang", "De")
						.method(Method.POST).execute();
				 loginCookies = res.cookies();
				System.out.println(loginCookies.toString());
			}
			
			 doc = Jsoup.connect("http://" + ip + "/wcd/system_counter.xml").cookies(loginCookies).get();

			imports = doc.getAllElements();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}
	public static int getNewField(int z, String ip) {

		Response res;
		try {
			res = Jsoup.connect("http://" + ip + "/wcd/ulogin.cgi")
					.data("func", "PSL_LP0_TOP", "AuthType", "None", "TrackType", "None", "PswcForm", "HtmlFlash",
							"Mode", "Public", "ViewMode", "Html", "BrowseMode", "Low", "Lang", "De")
					.method(Method.POST).execute();
			Map<String, String> loginCookies = res.cookies();
			System.out.println(loginCookies.toString());
			Document doc = Jsoup.connect("http://" + ip + "/wcd/system_counter.xml").cookies(loginCookies).get();

			Elements imports = doc.getAllElements();
			int v = 0;
			for (Element link : imports) {

				if (link.ownText().startsWith(Integer.toString(z))) {
					System.out.println(v);
					System.out.println(v + "  " + link.ownText());
					return v;
				}
				v++;
			}
			

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Server IO Error");
			return 0;
		}
		
		System.err.println("Not Found");
		return 0;
	}

	public List<PrinterData> getPrinterDataList() {
		return PData;
	}

	public Elements getImports() {
		return imports;
	}

	public String getName() {
		return name;
	}

	public String getIP() {
		return ip;
	}public int[] getFields() {
		return f;
	}
	public String[] getNameData() {
		return nameData;
	}

}
