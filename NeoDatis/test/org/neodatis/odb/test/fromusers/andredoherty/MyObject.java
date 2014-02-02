/**
 * 
 */
package org.neodatis.odb.test.fromusers.andredoherty;

import java.util.TreeMap;

/**
 * @author olivier
 * 
 */
public class MyObject {
	private String name;

	enum Action {
		ADD, CHANGE, DELETE, CLONED
	};

	private TreeMap<Integer, Action> nodeHistory;

	public MyObject() {
		nodeHistory = new TreeMap<Integer, Action>();
		nodeHistory.put(new Integer(1), Action.ADD);
		nodeHistory.put(new Integer(2), Action.CHANGE);
		nodeHistory.put(new Integer(3), Action.DELETE);
		nodeHistory.put(new Integer(4), Action.CLONED);
	}

	Action getAction(Integer i) {
		return nodeHistory.get(i);
	}

}
