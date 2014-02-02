package org.neodatis.odb.impl.core.query.list.objects;

import java.util.Iterator;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.tool.wrappers.OdbComparable;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * A simple list to hold query result. It is used when no index and no order by is
 * used and inMemory = false
 * 
 * This collection does not store the objects, it only holds the OIDs of the objects. When user ask an object
 * the object is lazy loaded by the getObjectFromId method
 * 
 * @author osmadja
 * 
 */
public class LazySimpleListFromOid <T> extends OdbArrayList<T> implements Objects<T> {

	/** a cursor when getting objects*/
	private int currentPosition;
	
	/** The odb engine to lazily get objects*/
	private IStorageEngine engine;
	
	/** indicate if objects must be returned as instance (true) or as non native objects (false)*/
	private boolean returnInstance;

	public LazySimpleListFromOid(int size, IStorageEngine engine, boolean returnObjects) {
		super(size);
		this.engine = engine;
		this.returnInstance = returnObjects;
	}

	public boolean addWithKey(OdbComparable key, T object) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED);
	}

	public boolean addWithKey(int key, T object) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED);
	}

	public T getFirst() {
		try {
			return get(0);
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_GETTING_OBJECT_FROM_LIST_AT_INDEX.addParameter(0), e);
		}
	}

	@Override
	public T get(int index) {
		OID oid = (OID) super.get(index);
		try {
			if(returnInstance){
				return (T) engine.getObjectFromOid(oid);
			}

			return (T) engine.getObjectReader().getObjectFromOid(oid, false, false);
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_GETTING_OBJECT_FROM_LIST_AT_INDEX.addParameter(index));
		}
	}

	public boolean hasNext() {
		return currentPosition < size();
	}

	public Iterator<T> iterator(OrderByConstants orderByType) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED);
	}

	public T next() {
		try {
			return get(currentPosition++);
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_GETTING_OBJECT_FROM_LIST_AT_INDEX.addParameter(0), e);
		}
	}

	public void reset() {
		currentPosition = 0;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.Objects#removeByKey(org.neodatis.tool.wrappers.OdbComparable, java.lang.Object)
	 */
	public boolean removeByKey(OdbComparable key, Object value) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED.addParameter("removeByKey on LazySimplelist, use remove instead"));
	}

}
