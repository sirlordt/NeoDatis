package org.neodatis.odb.impl.core.query.list.objects;

import java.util.Iterator;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.lookup.LookupFactory;
import org.neodatis.odb.impl.core.lookup.Lookups;
import org.neodatis.tool.wrappers.OdbComparable;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * A simple list to hold query result. It is used when no index and no order by 
 * 
 * This collection does not store the objects, it only holds the Abstract Object Info (AOI) of the objects. When user ask an object
 * the object is lazy loaded by the buildInstance method
 * 
 * @author osmadja
 * 
 */
public class LazySimpleListOfAOI<T> extends OdbArrayList<T> implements Objects<T>{

	/** a cursor when getting objects*/
	private int currentPosition;
	
	/** The odb engine to lazily get objects*/
	private transient IInstanceBuilder instanceBuilder;
	/** this session id is used to store the odb session id. When in true client server mode, when the lazy list is sent 
	 * back to the client, the instance builder (declared as transient) will be null on the client side. 
	 * Then the client will use the Lookup class with the base id to obtain the client instance builder 
	 */
	private String sessionId;
	
	/** indicate if objects must be returned as instance (true) or as non native objects (false)*/
	private boolean returnInstance;

	public LazySimpleListOfAOI() {
		super(10);
	}
	
	/**
	 * 
	 * @param size
	 * @param builder
	 * @param returnInstance
	 */
	public LazySimpleListOfAOI(int size, IInstanceBuilder builder, boolean returnInstance) {
		super(10);
		// If in client server mode, the instance builder will be set on the client.
		if(builder.isLocal()){
			this.instanceBuilder = builder;
		}
		this.sessionId = builder. getSessionId();
		this.returnInstance = returnInstance;
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

	public T get(int index) {
		Object o = super.get(index);
		AbstractObjectInfo aoi = (AbstractObjectInfo) o;
		try {
			if(aoi.isNull()){
				return null;
			}
			if(returnInstance){
				if(aoi.isNative()){
					return (T) aoi.getObject();
				}
				if(instanceBuilder==null){
					// Lookup the instance builder
					instanceBuilder = (IInstanceBuilder) LookupFactory.get(sessionId).get(Lookups.INSTANCE_BUILDER);
					if(instanceBuilder==null){
						throw new ODBRuntimeException(NeoDatisError.LOOKUP_KEY_NOT_FOUND.addParameter(Lookups.INSTANCE_BUILDER));
					}
					
				}
				return (T) instanceBuilder.buildOneInstance((NonNativeObjectInfo)aoi);
			}
			// No need to return Instance return the layer 2 representation
			o = aoi;
			return (T) o;
		} catch (Throwable e) {
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_GETTING_OBJECT_FROM_LIST_AT_INDEX.addParameter(index),e);
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

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("list with ").append(size()).append(" elements");
		return buffer.toString();
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.Objects#removeByKey(org.neodatis.tool.wrappers.OdbComparable, java.lang.Object)
	 */
	public boolean removeByKey(OdbComparable key, Object value) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED.addParameter("removeByKey on LazySimplelist, use remove instead"));
	}
}
