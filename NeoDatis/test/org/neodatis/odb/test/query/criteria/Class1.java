package org.neodatis.odb.test.query.criteria;

public class Class1 {
	private String name;

	public Class1(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
	
	public boolean equals(Object obj) {
		if(obj==null || !( obj instanceof Class1)){
			return false;
		}
		Class1 c1 = (Class1) obj;
		boolean b = name.equals(c1.name);
		return b;
	}

}
