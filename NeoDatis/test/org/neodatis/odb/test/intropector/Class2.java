package org.neodatis.odb.test.intropector;

public class Class2 {
	public Class2(String name22, String name3) {
		this.name2 = name22;
		class3 = new Class3(name3);
	}

	private String name2;
	private Class3 class3;

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public Class3 getClass3() {
		return class3;
	}

	public void setClass3(Class3 class3) {
		this.class3 = class3;
	}

}
