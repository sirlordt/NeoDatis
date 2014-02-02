/**
 * 
 */
package org.neodatis.odb.test.cyclic;

/**
 * @author olivier
 * 
 */
public class ClassA {
	String name;
	ClassB classb;

	public ClassA() {
		super();
	}

	public ClassA(ClassB classb, String name) {
		super();
		this.classb = classb;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClassB getClassb() {
		return classb;
	}

	public void setClassb(ClassB classb) {
		this.classb = classb;
	}

}
