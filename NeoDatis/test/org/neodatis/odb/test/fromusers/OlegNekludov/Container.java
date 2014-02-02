/**
 * 
 */
package org.neodatis.odb.test.fromusers.OlegNekludov;

import java.util.ArrayList;
import java.util.List;

/**
 * @author olivier
 * 
 */
public class Container {
	List<Item> items = new ArrayList<Item>();

	public Container() {
		items = new ArrayList<Item>();
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public void setItem(int index, Item item) {
		items.set(index, item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return items.toString();
	}
}
