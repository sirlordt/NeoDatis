package org.neodatis.odb.test.refactoring.manual;

import java.util.Date;

public class Item {
	private String name;
	//public Date date;
	//public String s1;
	public String s2;
	

	public Item(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
