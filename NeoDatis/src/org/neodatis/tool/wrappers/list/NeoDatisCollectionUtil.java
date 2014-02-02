package org.neodatis.tool.wrappers.list;

import java.util.Collection;
import java.util.List;

/**
 * A class to provide some simple utilities about collection
 * @author olivier
 * @sharpen.ignore
 *
 */
public class NeoDatisCollectionUtil {
	
	public static Collection<Object> concat(Collection<Object> c1, Collection<Object> c2){
		c1.addAll(c2);
		return c1;
	}
	
	public static List sublist(List l1, int from, int to){
		return l1.subList(from, to);
	}
	
	public static List<Object> sublistGeneric(List<Object> l1, int from, int to){
		return l1.subList(from, to);
	}

}
