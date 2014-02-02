package org.neodatis.odb.test.trigger;

public class ID {

	private String idName;
	private long id;

	public ID(String name, long id) {
		this.idName = name;
		this.id = id;
	}

	public long getNext() {
		id++;
		return id;
	}

}
