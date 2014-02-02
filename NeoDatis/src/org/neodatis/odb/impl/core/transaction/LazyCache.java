/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.impl.core.transaction;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * @sharpen.ignore
 * A cache of object.
 * 
 * <pre>
 *  Cache objects by object, by position, by oids,...
 *  
 *  This implementation does not work!!!!!
 * </pre>
 * 
 * @author olivier s
 * 
 */
public class LazyCache extends Cache {
	private ReferenceQueue queue;
	private OdbThread queueThread;
	protected LazyCache(ISession session) {
		super(session,"lazy");
		queue = new ReferenceQueue();
		//queueThread = new OdbThread(new ReferenceQueueThread(this,1000));
		//queueThread.start();
	}

	protected void init(ISession session, String name) {
		this.name = name;
		this.session = session;
		objects = new OdbHashMap();
		oids = new WeakHashMap();
		unconnectedZoneOids = new WeakHashMap();
		objectInfoPointersCacheFromOid = new WeakHashMap();
		insertingObjects = new WeakHashMap();
		readingObjectInfo = new WeakHashMap();
		objectPositionsByIds = new WeakHashMap();
	}

	public void addObject(OID oid, Object object, ObjectInfoHeader objectInfoHeader) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}
		if (checkHeaderPosition() && objectInfoHeader.getPosition() == -1) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NEGATIVE_POSITION.addParameter("Adding OIH with position = -1"));
		}
		// TODO : Should remove first inserted object and not clear all cache
		if (objects.size() > OdbConfiguration.getMaxNumberOfObjectInCache()) {
			// clear();
			manageFullCache();
		}
		//oids.put(new WeakReference(oid), new WeakReference(object));
		objects.put(new SoftReference(object,queue), oid);
		//objectInfoPointersCacheFromOid.put(new WeakReference(oid), new WeakReference(objectInfoHeader));
	}



    public void addObjectInfo(ObjectInfoHeader objectInfoHeader) {
    	if(objectInfoHeader.getOid()==null){
    		throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
    	}
    	
    	if(objectInfoHeader.getClassInfoId()==null){
    		throw new ODBRuntimeException(NeoDatisError.CACHE_OBJECT_INFO_HEADER_WITHOUT_CLASS_ID.addParameter(objectInfoHeader.getOid()));
    	}

        // TODO : Should remove first inserted object and not clear all cache
        if (objectInfoPointersCacheFromOid.size() > OdbConfiguration.getMaxNumberOfObjectInCache()) {
            manageFullCache();
        }

       	//objectInfoPointersCacheFromOid.put(new WeakReference(objectInfoHeader.getOid()), new WeakReference(objectInfoHeader));
        
        // For monitoring purpose
        nbObjects = objects.size();
        nbOids = oids.size();
        nbOih = objectInfoPointersCacheFromOid.size();

    }

	public ObjectInfoHeader getObjectInfoHeaderFromOid(OID oid, boolean throwExceptionIfNotFound) {
		ObjectInfoHeader oih = null;
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}
		Object o = objectInfoPointersCacheFromOid.get(new WeakReference(oid));
		WeakReference wr = (WeakReference) o;

		if (wr == null && throwExceptionIfNotFound) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE.addParameter(oid));
		}

		oih = (ObjectInfoHeader) wr.get();
		return oih;
	}

    protected void manageFullCache() {
        if (OdbConfiguration.automaticallyIncreaseCacheSize()) {
            OdbConfiguration.setMaxNumberOfObjectInCache((long) (OdbConfiguration.getMaxNumberOfObjectInCache() * 1.2));
        } else {
        	throw new ODBRuntimeException(NeoDatisError.CACHE_IS_FULL.addParameter(objectInfoPointersCacheFromOid.size()).addParameter(OdbConfiguration.getMaxNumberOfObjectInCache()));
        }

    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getObjectPosition(java.lang.Object)
	 */
	public OID getOid(Object object, boolean throwExceptionIfNotFound) {

		WeakReference wr = (WeakReference) objects.get(object);

		if (wr != null) {
			OID oid = (OID) wr.get();
			if (oid != null) {
				return oid;
			}
		}
		if (throwExceptionIfNotFound) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE);
		}
		return StorageEngineConstant.NULL_OBJECT_ID;
	}

	public Object getObjectWithOid(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID.addParameter(oid));
		}

		Object o = oids.get(new WeakReference(oid));
		if (o != null) {
			o = ((WeakReference) o).get();
		}
		return o;
	}

	public ObjectInfoHeader getObjectInfoHeaderFromObject(Object object, boolean throwExceptionIfNotFound) {
		WeakReference wkOid = (WeakReference) objects.get(object);
		if (wkOid == null) {
			if (throwExceptionIfNotFound) {
				throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE.addParameter(object.toString()));
			}
			return null;
		}

		OID oid = (OID) wkOid.get();
		WeakReference wkOIH = null;//(WeakReference) objectInfoPointersCacheFromOid.get(new WeakReference(oid));
		if (wkOIH == null) {
			if (throwExceptionIfNotFound) {
				throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE.addParameter(object.toString()));
			}
			return null;
		}
		ObjectInfoHeader oih = (ObjectInfoHeader) wkOIH.get();
		if (oih == null && throwExceptionIfNotFound) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE.addParameter(object.toString()));
		}
		return oih;
	}
	
    protected boolean checkHeaderPosition(){
    	return false;
    }

	public ReferenceQueue getQueue() {
		return queue;
	}

}
