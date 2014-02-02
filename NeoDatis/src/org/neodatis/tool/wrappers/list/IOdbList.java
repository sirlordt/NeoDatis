package org.neodatis.tool.wrappers.list;

import java.util.Collection;
import java.util.List;



/**
 * @sharpen.ignore
 * @author olivier
 *
 * @param <E>
 */
public interface IOdbList<E> extends List<E>{
	boolean addAll(Collection<? extends E> c);
	boolean removeAll(Collection<?> c);
	boolean add(E o);
	E get(int index);
	E set(int index, E o);
	boolean isEmpty();
}
