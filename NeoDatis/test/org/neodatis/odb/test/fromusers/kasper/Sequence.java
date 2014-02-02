package org.neodatis.odb.test.fromusers.kasper;

public class Sequence {
	/** the name of the class */
	private String className;
	/** The current value of the id */
	private long id;

	public Sequence(String name, long id) {
		this.className = name;
		this.id = id;
	}

	public long increment() {
		id++;
		return id;
	}

	public long getId() {
		return id;
	}
   
}
