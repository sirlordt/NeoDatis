package org.neodatis.odb.core.transaction;

import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;

public interface ICache {

	public abstract void addObject(OID oid, Object object, ObjectInfoHeader objectInfoHeader);

	public abstract void startInsertingObjectWithOid(Object object, OID oid, NonNativeObjectInfo nnoi);

	public abstract void updateIdOfInsertingObject(Object object, OID oid);

	public abstract void endInsertingObject(Object object);

	public abstract void addObjectInfo(ObjectInfoHeader objectInfoHeader);

	public abstract void removeObjectWithOid(OID oid);
	public abstract void removeObject(Object object);
	public abstract boolean existObject(Object object);

	public abstract Object getObjectWithOid(OID oid);

	public abstract ObjectInfoHeader getObjectInfoHeaderFromObject(Object object, boolean throwExceptionIfNotFound);

	public abstract ObjectInfoHeader getObjectInfoHeaderFromOid(OID oid, boolean throwExceptionIfNotFound);

	public abstract OID getOid(Object object, boolean throwExceptionIfNotFound);

	/** To resolve uncommitted updates where the oid change and is not committed yet*/
	public abstract void savePositionOfObjectWithOid(OID oid, long objectPosition);

	public abstract void markIdAsDeleted(OID oid);

	public abstract boolean isDeleted(OID oid);

	public abstract long getObjectPositionByOid(OID oid);

	public abstract void clearOnCommit();

	public abstract void clear(boolean setToNull);

	public abstract void clearInsertingObjects();

	public abstract String toString();

	public abstract String toCompleteString();

	public abstract int getNumberOfObjects();

	public abstract int getNumberOfObjectHeader();

	public abstract OID idOfInsertingObject(Object object);

	public abstract int insertingLevelOf(Object object);

	public abstract boolean isReadingObjectInfoWithOid(OID oid);

	public abstract NonNativeObjectInfo getReadingObjectInfoFromOid(OID oid);

	/**
	 * To resolve cyclic reference, keep track of objects being read The read
	 * count is used to store how many times the object has been recursively
	 * read
	 * 
	 * @param oid
	 *            The Object OID
	 * @param objectInfo
	 *            The object info (not fully set) that is being read
	 */
	public abstract void startReadingObjectInfoWithOid(OID oid, NonNativeObjectInfo objectInfo);

	public abstract void endReadingObjectInfo(OID oid);

	public Map<OID,Object> getOids();

	public Map<OID,ObjectInfoHeader> getObjectInfoPointersCacheFromOid();

	public Map<Object,OID> getObjects();
	
	public boolean objectWithIdIsInCommitedZone(OID oid);
	public void addOIDToUnconnectedZone(OID oid);

}