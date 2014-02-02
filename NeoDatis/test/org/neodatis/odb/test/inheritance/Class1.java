package org.neodatis.odb.test.inheritance;

public class Class1 implements IInterface {

	private String name;

	public Class1(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
