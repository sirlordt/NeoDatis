/**
 * 
 */
package org.neodatis.odb.test.nativeobject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author olivier
 * 
 */
public class MyMap<K, V> extends HashMap<K, V> {

	public MyMap() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		// TODO Auto-generated constructor stub
	}

	public MyMap(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public MyMap(Map<? extends K, ? extends V> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

}
