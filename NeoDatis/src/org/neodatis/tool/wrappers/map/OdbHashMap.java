package org.neodatis.tool.wrappers.map;

import java.util.HashMap;
import java.util.Map;

/**
 * @sharpen.ignore
 * @author olivier
 *
 * @param <K>
 * @param <V>
 */
public class OdbHashMap<K,V> extends HashMap<K, V> {

	public OdbHashMap() {
		super();
	}

	
	public OdbHashMap(int initialCapacity) {
		super(initialCapacity);
	}


	public OdbHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}


	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
	}
	/**
	 * The remove2 method is just to create another method to be able to map the c# equivalent as the Remove in C# does not return the object
	 * @param key
	 * @return
	 */
	public V remove2(K key){
		return remove(key);
	}

}
