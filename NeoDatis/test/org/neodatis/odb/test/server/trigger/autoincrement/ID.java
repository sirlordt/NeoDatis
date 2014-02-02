package org.neodatis.odb.test.server.trigger.autoincrement;

public class ID {
	/** the name of the ID (could be className_fieldName) */
	private String idName;
	/** The current value of the id */
	private long id;

	public ID(String name, long id) {
		this.idName = name;
		this.id = id;
	}

	public long getNext() {
		id++;
		return id;
	}

	public long getValue() {
		return id;
	}

}
