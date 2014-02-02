/**
 * 
 */
package org.neodatis.odb.test.fromusers.OlegNekludov;

/**
 * @author olivier
 * 
 */
public class Item {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Item(String name) {
		super();
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}
