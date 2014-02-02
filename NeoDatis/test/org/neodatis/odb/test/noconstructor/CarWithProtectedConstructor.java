package org.neodatis.odb.test.noconstructor;

public class CarWithProtectedConstructor {
	private String name;

	protected CarWithProtectedConstructor() {

	}

	public CarWithProtectedConstructor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
