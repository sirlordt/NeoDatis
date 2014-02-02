/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.ike;

/**
 * @author olivier
 *
 */
public class Bird {
	protected String name;
	protected int size;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Bird(String name, int size) {
		super();
		this.name = name;
		this.size = size;
	}
	
	
}
