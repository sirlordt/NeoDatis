package org.neodatis.odb.core.layers.layer3;

import org.neodatis.odb.OID;

public interface IIdManager {

	/** Gets an id for an object (instance)
	 * 
	 * @param objectPosition the object position (instance) 
	 * @return The id
	 * 
	 */
	public abstract OID getNextObjectId(long objectPosition) ;

	/** Gets an id for a class
	 * 
	 * @param objectPosition the object position (class) 
	 * @return The id
	 *
	 */
	public abstract OID getNextClassId(long objectPosition);

	public abstract void updateObjectPositionForOid(OID oid, long objectPosition, boolean writeInTransaction);

	public abstract void updateClassPositionForId(OID classId, long objectPosition, boolean writeInTransaction);

	public abstract void updateIdStatus(OID id, byte newStatus);

	public abstract void reserveIds(long nbIds);

	public abstract long getObjectPositionWithOid(OID oid, boolean useCache);

	public void clear();

	/** To check if the id block must shift: that a new id block must be created
	 * 
	 * @return a boolean value to check if block of id is full
	 */
	public boolean mustShift();

	public OID consultNextOid();

}