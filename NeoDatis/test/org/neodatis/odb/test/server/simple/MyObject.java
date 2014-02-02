package org.neodatis.odb.test.server.simple;

import java.io.Serializable;

public class MyObject implements Serializable {
	private String name;
	private String street;

	public MyObject(String name, String street) {
		super();
		this.name = name;
		this.street = street;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

}
