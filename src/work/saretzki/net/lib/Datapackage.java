package work.saretzki.net.lib;

import java.io.Serializable;
import java.util.ArrayList;

public class Datapackage implements Serializable {

	private static final long serialVersionUID = 5816841623141578069L;

	private ArrayList<Object> memory;

	public Datapackage(String id, Object... o) {
		memory = new ArrayList<Object>();
		memory.add(id);
		for (Object current : o) {
			memory.add(current);
		}
	}


	public String id() {
		if (!(memory.get(0) instanceof String)) {
			throw new IllegalArgumentException("Identifier of Datapackage is not a String");
		}
		return (String) memory.get(0);
	}


	public Object get(int i) {
		return memory.get(i);
	}

	
	public ArrayList<Object> open() {
		return memory;
	}

	@Override
	public String toString() {
		return memory.toString();
	}

}
