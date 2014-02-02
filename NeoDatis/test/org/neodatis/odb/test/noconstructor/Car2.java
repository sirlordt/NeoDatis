package org.neodatis.odb.test.noconstructor;

public class Car2 {

	private String model;

	private Car2() {
		model = "";
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public static Car2 getInstance() {
		return new Car2();
	}
}
