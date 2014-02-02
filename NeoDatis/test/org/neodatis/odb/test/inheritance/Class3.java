package org.neodatis.odb.test.inheritance;

public class Class3 {
	private int nb;
	private Class1 class1;

	public Class3(int nb, Class1 class1) {
		this.nb = nb;
		this.class1 = class1;
	}

	public Class1 getClass1() {
		return class1;
	}

	public void setClass1(Class1 class1) {
		this.class1 = class1;
	}

	public int getNb() {
		return nb;
	}

	public void setNb(int nb) {
		this.nb = nb;
	}

}
