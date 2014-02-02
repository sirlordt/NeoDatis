package org.neodatis.odb.test.oid;

public class A {
	private String name;
	private B b;

	public A(String name, B b) {
		super();
		this.name = name;
		this.b = b;
	}

	public String getName() {
		return name;
	}

	public B getB() {
		return b;
	}

}
