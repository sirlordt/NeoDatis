package org.neodatis.odb.impl.core.query.list.objects;

import java.util.ArrayList;
import java.util.Iterator;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * A simple list to hold query result. It is used when no index and no order by is used and inMemory = true
 * @author osmadja
 *
 */
public class SimpleList<E> extends ArrayList<E> implements Objects<E> {

	private int currentPosition;
	
	
	public SimpleList() {
		super();
	}

	public SimpleList(int initialCapacity) {
		super(initialCapacity);
	}
	
	
	public boolean addWithKey(OdbComparable key, E o) {
		add( o);
		return true;
	}

	public boolean addWithKey(int key, E o) {
		add( o);
		return true;
	}

	public E getFirst() {
		return get(0);
	}

	public boolean hasNext() {
		return currentPosition < size();
	}

	/** The orderByType in not supported by this kind of list
	 * 
	 */
	public Iterator<E> iterator(OrderByConstants orderByType) {
		return iterator();
	}

	public E next() {
		return get(currentPosition++);
	}

	public void reset() {
		currentPosition = 0;

	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.Objects#removeByKey(org.neodatis.tool.wrappers.OdbComparable, java.lang.Object)
	 */
	public boolean removeByKey(OdbComparable key, Object value) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED.addParameter("removeByKey on Simplelist, use remove instead"));
	}

}
