package org.neodatis.odb.test.noconstructor;

public class Car {

	private String message;

	public Car(String greeting) {
		message = greeting;
		System.out.println("This will never print.");
	}

	public void getModel() {
		System.out.println(message + ", Ranger");
	}

}
