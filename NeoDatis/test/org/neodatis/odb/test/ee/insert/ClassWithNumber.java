/**
 * 
 */
package org.neodatis.odb.test.ee.insert;

/**
 * @author olivier
 *
 */
public class ClassWithNumber {
	protected Number n;
	protected String name;
	public ClassWithNumber(Number n, String name) {
		super();
		this.n = n;
		this.name = name;
	}
	public ClassWithNumber() {
		super();
	}
	public Number getN() {
		return n;
	}
	public void setN(Number n) {
		this.n = n;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
