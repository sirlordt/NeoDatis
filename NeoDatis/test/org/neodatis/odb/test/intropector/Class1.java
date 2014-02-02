package org.neodatis.odb.test.intropector;

public class Class1 {
	private String name1;
	private Class2 class2;

	public Class1(String name1, String name2, String name3) {
		this.name1 = name1;
		class2 = new Class2(name2, name3);
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public Class2 getClass2() {
		return class2;
	}

	public void setClass2(Class2 class2) {
		this.class2 = class2;
	}

}
