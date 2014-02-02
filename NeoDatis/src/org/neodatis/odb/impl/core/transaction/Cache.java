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

import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.layers.layer3.oid.IDStatus;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * A cache of object.
 * 
 * <pre>
 *   Cache objects by object, by position, by oids,...
 * </pre>
 * 
 * @author olivier s
 * 
 */
public class Cache implements ICache {

	protected static int nbObjects = 0;
	protected static int nbOids = 0;
	protected static int nbOih = 0;
	protected static int nbTransactionOids = 0;
	protected static int nbObjectPositionByIds = 0;
	protected static int nbCallsToGetObjectInfoHeaderFromOid = 0;
	protected static int nbCallsToGetObjectInfoHeaderFromObject = 0;
	protected static int nbCallsToGetObjectWithOid = 0;

	/**
	 * object cache - used to know if object exist in the cache TODO use
	 * hashcode instead?
	 */
	protected Map<Object,OID> objects;

	/** Entry to get an object from its oid */
	protected Map<OID,Object> oids;

	/** To resolve cyclic reference, keep track of objects being inserted */
	protected Map<Object,ObjectInsertingInfo> insertingObjects;

	/** To resolve cyclic reference, keep track of objects being read */
	protected Map<OID,Object[]> readingObjectInfo;

	/**
	 * <pre>
	 *    To resolve the update of an id object position:
	 *    When an object is full updated(the current object is being deleted and a new one os being created), 
	 *    the id remain the same but its position change.
	 *    But the update is done in transaction, so it is not flushed until the commit happens
	 *    So after the update when i need the position to make the old object a pointer, i have no way to get 
	 *    the right position. To resolve this, i keep a cache of ids where i keep the non commited value
	 * </pre>
	 * 
	 */
	protected Map<OID,IdInfo> objectPositionsByIds;

	/**
	 * To keep track of the oid that have been created or modified in the
	 * current transaction
	 */
	protected Map<OID,OID> unconnectedZoneOids;

	protected ISession session;
	protected String name;

	/**
	 * Entry to get object info pointers (position,next object pos, previous
	 * object pos and class info pos) from the id
	 */
	protected Map<OID,ObjectInfoHeader> objectInfoPointersCacheFromOid;
	
	protected boolean useCache;

	public Cache(ISession session, String name) {
		init(session, name);
	}

	protected void init(ISession session, String name) {
		this.name = name;
		this.session = session;
		objects = new OdbHashMap<Object, OID>();
		oids = new OdbHashMap<OID, Object>();
		unconnectedZoneOids = new OdbHashMap<OID, OID>();
		objectInfoPointersCacheFromOid = new OdbHashMap<OID, ObjectInfoHeader>();
		insertingObjects = new OdbHashMap<Object, ObjectInsertingInfo>();
		readingObjectInfo = new OdbHashMap<OID, Object[]>();
		objectPositionsByIds = new OdbHashMap<OID, IdInfo>();
		useCache = OdbConfiguration.useCache();
	}

	/*
	 * 
	 * 
	 * Adds an object to the cache Add the object, the oid and the object infos
	 * Keys to get objects are the oid.
	 */
	public void addObject(OID oid, Object object, ObjectInfoHeader objectInfoHeader) {
		if(!useCache){
			return;
		}
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
		oids.put(oid, object);
		try {
			objects.put(object, oid);
		} catch (NullPointerException e) {
			// FIXME URL in HashMap What should we do?
			// In some case, the object can throw exception when added to the
			// cache
			// because Map.put, end up calling the equals method that can throw
			// exception
			// This is the case of URL that has a transient attribute handler
			// that is used in the URL.equals method
		}

		objectInfoPointersCacheFromOid.put(oid, objectInfoHeader);
		// For monitoring purpose
		nbObjects = objects.size();
		nbOids = oids.size();
		nbOih = objectInfoPointersCacheFromOid.size();
	}

	/**
	 * Only adds the Object info - used for non committed objects
	 * 
	 */
	public void addObjectInfo(ObjectInfoHeader objectInfoHeader) {
		if(!useCache){
			return;
		}
		if (objectInfoHeader.getOid() == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		if (objectInfoHeader.getClassInfoId() == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_OBJECT_INFO_HEADER_WITHOUT_CLASS_ID.addParameter(objectInfoHeader.getOid()));
		}

		// TODO : Should remove first inserted object and not clear all cache
		if (objectInfoPointersCacheFromOid.size() > OdbConfiguration.getMaxNumberOfObjectInCache()) {
			manageFullCache();
		}

		objectInfoPointersCacheFromOid.put(objectInfoHeader.getOid(), objectInfoHeader);
		// For monitoring purpose
		nbObjects = objects.size();
		nbOids = oids.size();
		nbOih = objectInfoPointersCacheFromOid.size();

	}

	protected void manageFullCache() {
		if (OdbConfiguration.automaticallyIncreaseCacheSize()) {
			OdbConfiguration.setMaxNumberOfObjectInCache((long) (OdbConfiguration.getMaxNumberOfObjectInCache() * 1.2));
		} else {
			//throw new ODBRuntimeException(Error.CACHE_IS_FULL.addParameter(objectInfoPointersCacheFromOid.size()).addParameter(OdbConfiguration.getMaxNumberOfObjectInCache()));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#startInsertingObject(java.lang.Object,
	 *      long, org.neodatis.odb.core.meta.NonNativeObjectInfo)
	 */
	public void startInsertingObjectWithOid(Object object, OID oid, NonNativeObjectInfo nnoi) {

		// In this case oid can be -1,because object is beeing inserted and do
		// not have yet a defined oid.
		if (object == null) {
			return;
		}
		ObjectInsertingInfo oii = (ObjectInsertingInfo) insertingObjects.get(object);
		if (oii == null) {
			insertingObjects.put(object, new ObjectInsertingInfo(oid, 1));
		} else {
			oii.level++;
			// No need to update the map, it is a reference.
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#updateIdOfInsertingObject(java.lang.Object,
	 *      long)
	 */
	public void updateIdOfInsertingObject(Object object, OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		ObjectInsertingInfo oii = (ObjectInsertingInfo) insertingObjects.get(object);
		if (oii != null) {
			oii.oid = oid;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#endInsertingObject(java.lang.Object)
	 */
	public void endInsertingObject(Object object) {
		ObjectInsertingInfo oii = (ObjectInsertingInfo) insertingObjects.get(object);
		if (oii.level == 1) {
			insertingObjects.remove(object);
			oii = null;
		} else {
			oii.level--;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#removeObject(long,
	 *      java.lang.Object)
	 */
	public void removeObjectWithOid(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		Object object = oids.get(oid);

		oids.remove(oid);
		try {
			objects.remove(object);
		} catch (NullPointerException e) {
			// FIXME URL in HashMap What should we do?
		}

		objectInfoPointersCacheFromOid.remove(oid);
		unconnectedZoneOids.remove(oid);

		// For monitoring purpose
		nbObjects = objects.size();
		nbOids = oids.size();
		nbOih = objectInfoPointersCacheFromOid.size();

	}
	
	public void removeObject(Object object) {
		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OBJECT.addParameter(" while removing object from the cache"));
		}
		OID oid = (OID) objects.get(object);

		oids.remove(oid);
		try {
			objects.remove(object);
		} catch (NullPointerException e) {
			// FIXME URL in HashMap What should we do?
		}

		objectInfoPointersCacheFromOid.remove(oid);
		unconnectedZoneOids.remove(oid);

		// For monitoring purpose
		nbObjects = objects.size();
		nbOids = oids.size();
		nbOih = objectInfoPointersCacheFromOid.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#existObject(java.lang.Object)
	 */
	public boolean existObject(Object object) {
		return objects.containsKey(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getObjectWithPosition(long)
	 */
	public Object getObjectWithOid(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID.addParameter(oid));
		}

		Object o = oids.get(oid);
		nbCallsToGetObjectWithOid++;
		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getObjectInfoHeaderFromObject(java.lang.Object,
	 *      boolean)
	 */
	public ObjectInfoHeader getObjectInfoHeaderFromObject(Object object, boolean throwExceptionIfNotFound) {
		OID oid = (OID) objects.get(object);
		ObjectInfoHeader oih = (ObjectInfoHeader) objectInfoPointersCacheFromOid.get(oid);
		if (oih == null && throwExceptionIfNotFound) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE.addParameter(object.toString()));
		}
		nbCallsToGetObjectInfoHeaderFromObject++;
		return oih;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getObjectInfoHeaderFromPosition(long,
	 *      boolean)
	 */
	public ObjectInfoHeader getObjectInfoHeaderFromOid(OID oid, boolean throwExceptionIfNotFound) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		ObjectInfoHeader oih = (ObjectInfoHeader) objectInfoPointersCacheFromOid.get(oid);
		if (oih == null && throwExceptionIfNotFound) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE.addParameter(oid));
		}
		nbCallsToGetObjectInfoHeaderFromOid++;
		return oih;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getObjectPosition(java.lang.Object)
	 */
	public OID getOid(Object object, boolean throwExceptionIfNotFound) {
		OID oid = (OID) objects.get(object);
		if (oid != null) {
			return oid;
		}
		if (throwExceptionIfNotFound) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE);
		}
		return StorageEngineConstant.NULL_OBJECT_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#saveObjectPosition(long, long)
	 */
	public void savePositionOfObjectWithOid(OID oid, long objectPosition) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		IdInfo idInfo = new IdInfo(oid, objectPosition, IDStatus.ACTIVE);
		objectPositionsByIds.put(oid, idInfo);

		// For monitoring purpose
		nbObjects = objects.size();
		nbOids = oids.size();
		nbOih = objectInfoPointersCacheFromOid.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#markIdAsDeleted(long)
	 */
	public void markIdAsDeleted(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}
		IdInfo idInfo = objectPositionsByIds.get(oid);
		if (idInfo != null) {
			idInfo.status = IDStatus.DELETED;
		} else {
			idInfo = new IdInfo(oid, -1, IDStatus.DELETED);
			objectPositionsByIds.put(oid, idInfo);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#isDeleted(long)
	 */
	public boolean isDeleted(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		IdInfo idInfo = (IdInfo) objectPositionsByIds.get(oid);
		if (idInfo != null) {
			return idInfo.status == IDStatus.DELETED;
		}
		return false;
	}

	/**
	 * Returns the position or -1 if it is not is the cache or
	 * StorageEngineConstant.NULL_OBJECT_ID_ID if it has been marked as deleted
	 */
	public long getObjectPositionByOid(OID oid) {
		if (oid == null) {
			return StorageEngineConstant.NULL_OBJECT_ID_ID;
		}

		IdInfo idInfo = (IdInfo) objectPositionsByIds.get(oid);
		if (idInfo != null) {
			if (!IDStatus.isActive(idInfo.status)) {
				return StorageEngineConstant.DELETED_OBJECT_POSITION;
			}
			return idInfo.position;
		}
		// object is not in the cache
		return StorageEngineConstant.OBJECT_IS_NOT_IN_CACHE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#clearOnCommit()
	 */
	public void clearOnCommit() {
		objectPositionsByIds.clear();
		unconnectedZoneOids.clear();
		objectInfoPointersCacheFromOid.clear();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#clear(boolean)
	 */
	public void clear(boolean setToNull) {
		if(objects!=null){
			objects.clear();
			oids.clear();
			objectInfoPointersCacheFromOid.clear();
			insertingObjects.clear();
			objectPositionsByIds.clear();
			readingObjectInfo.clear();
			unconnectedZoneOids.clear();
		}

		if (setToNull) {
			objects = null;
			oids = null;
			objectInfoPointersCacheFromOid = null;
			insertingObjects = null;
			objectPositionsByIds = null;
			readingObjectInfo = null;
			unconnectedZoneOids = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#clearInsertingObjects()
	 */
	public void clearInsertingObjects() {
		insertingObjects.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("C=");
		buffer.append(objects.size()).append(" objects ");
		buffer.append(oids.size()).append(" oids ");
		buffer.append(objectInfoPointersCacheFromOid.size()).append(" pointers");
		buffer.append(objectPositionsByIds.size()).append(" pos by oid");
		// buffer.append(insertingObjects.size()).append(" inserting
		// objects\n");
		// buffer.append(readingObjectInfo.size()).append(" reading objects");
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#toCompleteString()
	 */
	public String toCompleteString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(objects.size()).append(" Objects=").append(objects).append("\n");
		buffer.append(oids.size()).append(" Objects from pos").append(oids).append("\n");
		buffer.append(objectInfoPointersCacheFromOid.size()).append(" Pointers=").append(objectInfoPointersCacheFromOid);
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getNumberOfObjects()
	 */
	public int getNumberOfObjects() {
		return objects.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getNumberOfObjectHeader()
	 */
	public int getNumberOfObjectHeader() {
		return objectInfoPointersCacheFromOid.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#idOfInsertingObject(java.lang.Object)
	 */
	public OID idOfInsertingObject(Object object) {
		if (object == null) {
			return StorageEngineConstant.NULL_OBJECT_ID;
		}
		ObjectInsertingInfo oii = (ObjectInsertingInfo) insertingObjects.get(object);
		if (oii != null) {
			return oii.oid;
		}
		return StorageEngineConstant.NULL_OBJECT_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#insertingLevelOf(java.lang.Object)
	 */
	public int insertingLevelOf(Object object) {
		ObjectInsertingInfo oii = (ObjectInsertingInfo) insertingObjects.get(object);
		if (oii == null) {
			return 0;
		}

		return oii.level;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#isReadingObjectInfo(long)
	 */
	public boolean isReadingObjectInfoWithOid(OID oid) {
		if (oid == null) {
			return false;// throw new
			// ODBRuntimeException(Error.CACHE_NULL_OID);
		}

		return readingObjectInfo.get(oid) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#getReadingObjectInfo(long)
	 */
	public NonNativeObjectInfo getReadingObjectInfoFromOid(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		Object[] values = (Object[]) readingObjectInfo.get(oid);
		if(values==null){
			return null;
		}
		return (NonNativeObjectInfo) values[1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#startReadingObjectInfo(long,
	 *      org.neodatis.odb.core.meta.NonNativeObjectInfo)
	 */
	public void startReadingObjectInfoWithOid(OID oid, NonNativeObjectInfo objectInfo) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		Object[] objects = (Object[]) readingObjectInfo.get(oid);

		if (objects == null) {
			// The key is the oid, the value is an array of 2 objects :
			// 1-the read count, 2-The object info
			// Here we are saying that the object with oid 'oid' is
			// being read for the first time
			Object[] values = { new Short((short) 1), objectInfo };
			readingObjectInfo.put(oid, values);
		} else {
			// Here the object is already being read. It is necessary to
			// increase the read count
			short currentReadCount = ((Short) objects[0]).shortValue();
			objects[0] = new Short((short) (currentReadCount + 1));
			// Object is in memory, do not need to re-put in map. The key has
			// not changed
			// readingObjectInfo.put(oid, objects);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.session.ICache#endReadingObjectInfo(org.neodatis.odb.core.meta.NonNativeObjectInfo)
	 */
	public void endReadingObjectInfo(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID.addParameter(oid));
		}

		Object[] values = (Object[]) readingObjectInfo.get(oid);

		if (values == null || values[0] == null) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_INFO_NOT_IN_TEMP_CACHE.addParameter(oid).addParameter("?"));
		}
		short readCount = ((Short) values[0]).shortValue();

		if (readCount == 1) {
			readingObjectInfo.remove(oid);
		} else {
			values[0] = new Short((short) (readCount - 1));
			// Object is in memory, do not need to re-put in map. The key has
			// not changed
			// readingObjectInfo.put(oid, values);
		}

	}

	public Map<OID,Object> getOids() {
		return oids;
	}

	public void setOids(Map<OID,Object> oids) {
		this.oids = oids;
	}

	public Map<OID,ObjectInfoHeader> getObjectInfoPointersCacheFromOid() {
		return objectInfoPointersCacheFromOid;
	}

	public void setObjectInfoPointersCacheFromOid(Map<OID,ObjectInfoHeader> objectInfoPointersCacheFromOid) {
		this.objectInfoPointersCacheFromOid = objectInfoPointersCacheFromOid;
	}

	public Map<Object,OID> getObjects() {
		return objects;
	}

	public void setObjects(Map<Object,OID> objects) {
		this.objects = objects;
	}

	public boolean objectWithIdIsInCommitedZone(OID oid) {
		return !unconnectedZoneOids.containsKey(oid);
	}

	public void addOIDToUnconnectedZone(OID oid) {
		if(!useCache){
			return;
		}
		unconnectedZoneOids.put(oid, oid);
	}

	public static String usage() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("NbObj=").append(nbObjects);
		buffer.append(" - NbOIDs=").append(nbOids);
		buffer.append(" - NbObjPos=").append(nbObjectPositionByIds);
		buffer.append(" - NbOIHs=").append(nbOih);
		buffer.append(" - NbTransOIDs=").append(nbTransactionOids);
		buffer.append(" - Calls2getObjectWitOid=").append(nbCallsToGetObjectWithOid);
		buffer.append(" - Calls2getObjectInfoHeaderFromOid=").append(nbCallsToGetObjectInfoHeaderFromOid);
		buffer.append(" - Calls2getObjectInfoHeaderFromObject=").append(nbCallsToGetObjectInfoHeaderFromObject);

		return buffer.toString();
	}

	protected boolean checkHeaderPosition() {
		return false;
	}

}
