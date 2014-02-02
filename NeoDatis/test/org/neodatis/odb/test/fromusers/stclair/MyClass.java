/**
 * 
 */
package org.neodatis.odb.test.fromusers.stclair;

import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author olivier
 * 
 */
public class MyClass {
	private String name;
	private Map map;

	public MyClass(String name) {
		super();
		this.name = name;
		this.map = new MultiKeyMap();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + ":" + map;
	}

}
