/**
 * 
 */
package org.neodatis.odb.test.vo.interfaces;

import java.io.Serializable;

/**
 * @author olivier
 * 
 */
public class MyObject implements Serializable {
	public MyObject(String name) {
		super();
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
