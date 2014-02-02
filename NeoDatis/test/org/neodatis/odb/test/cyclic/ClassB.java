/**
 * 
 */
package org.neodatis.odb.test.cyclic;

/**
 * @author olivier
 * 
 */
public class ClassB {
	String name;
	ClassA classA;

	public ClassB() {
		super();
	}

	public ClassB(ClassA classA, String name) {
		super();
		this.classA = classA;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassA getClassA() {
		return classA;
	}

	public void setClassA(ClassA classA) {
		this.classA = classA;
	}

}
