package work.saretzki;

import java.io.Serializable;
import java.util.logging.Level;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PrinterData implements Serializable {
	private long time = System.currentTimeMillis();
	private int dif = 0;
	private int lS = 0;
	private int first = 0;
	private int last = 0;
	private int F;
	private Elements e;
	private String name;

	public PrinterData(String name, int F, Elements e) throws NumberFormatException {
		this.F = F;
		this.e = e;
		this.name = name;

		first = Integer.parseInt(e.get(F).ownText());

	}

	

	public int getLast(Elements e) {
		this.e = e;
		last = Integer.parseInt(e.get(F).ownText());

		return last;
	}

	public int getDif(Elements e) {
		this.e = e;
		last = Integer.parseInt(e.get(F).ownText());
		dif = last - first;

		if (lS == last) {

			if (time < System.currentTimeMillis()) {
				// first = last;
			}
		} else {
			lS = last;
			time = System.currentTimeMillis() + (30 * 60 * 1000);
		}

		return dif;
	}

	public int getDif() {
		return dif;
	}

	public String getName() {
		return name;
	}
}
