package org.neodatis.odb.test.crossSessionCache.vo2;

import java.io.Serializable;

public class MyClass implements Serializable {
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
