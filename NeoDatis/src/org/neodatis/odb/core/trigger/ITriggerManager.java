package org.neodatis.odb.core.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public interface ITriggerManager {
	public abstract boolean manageInsertTriggerBefore(String className, Object object);

	public abstract void manageInsertTriggerAfter(String className, final Object object, OID oid);

	public abstract boolean manageUpdateTriggerBefore(String className, NonNativeObjectInfo oldObjectRepresentation, Object newObject, final OID oid);

	public abstract void manageUpdateTriggerAfter(String className, final NonNativeObjectInfo oldObjectRepresentation, Object newObject, final OID oid);

	public abstract boolean manageDeleteTriggerBefore(String className, final Object object, final OID oid);

	public abstract void manageDeleteTriggerAfter(String className, final Object object, final OID oid);

	public abstract void manageSelectTriggerAfter(String className, final Object object, final OID oid);

	public void addUpdateTriggerFor(String className, UpdateTrigger trigger);

	public void addInsertTriggerFor(String className, InsertTrigger trigger);
	
	public void addOidTriggerFor(String className, OIDTrigger trigger);

	public void addDeleteTriggerFor(String className, DeleteTrigger trigger);

	public void addSelectTriggerFor(String className, SelectTrigger trigger);

	/**
	 * used to transform object before real trigger call. This is used for
	 * example, in server side trigger where the object is encapsulated in an
	 * ObjectRepresentation instance. It is only for internal use
	 */
	public Object transform(Object object);

	public boolean hasDeleteTriggersFor(String classsName);

	public boolean hasInsertTriggersFor(String className);
	
	public boolean hasOidTriggersFor(String className);

	public boolean hasSelectTriggersFor(String className);

	public boolean hasUpdateTriggersFor(String className);

	/**
	 * @param objectInfo
	 * @param oid
	 */
	public abstract boolean manageOidTrigger(NonNativeObjectInfo objectInfo, OID oid);

}