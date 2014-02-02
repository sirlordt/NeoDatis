package org.neodatis.odb.test.crossSessionCache.vo1;

import java.io.Serializable;

public class MyClass implements Serializable, IMyClass {
	private String name;
	private int value;

	public MyClass(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
