package org.neodatis.tool.wrappers.list;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @sharpen.ignore
 * @author olivier
 *
 * @param <E>
 */
public class OdbArrayList<E> extends ArrayList<E> implements IOdbList<E> {

	public OdbArrayList() {
		super();
	}

	public OdbArrayList(int size) {
		super(size);
	}

	public boolean addAll(Collection<? extends E> c) {
		return super.addAll(c);
	}
	
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return super.removeAll(c);
	}

	public E get(int index) {
		return super.get(index);
	}

	public E set(int index, E o){
		return super.set(index, o);
	}
}
