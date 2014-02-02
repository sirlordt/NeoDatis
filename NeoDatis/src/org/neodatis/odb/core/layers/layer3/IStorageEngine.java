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
package org.neodatis.odb.core.layers.layer3;

import java.util.List;
import java.util.Map;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.engine.CheckMetaModelResult;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.OIDTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;
import org.neodatis.odb.impl.core.layers.layer3.oid.FullIDInfo;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * The interface of all that a StorageEngine (Main concept in ODB) must do.
 * 
 * @author osmadja
 * 
 */
public interface IStorageEngine {

	OID store(OID oid, Object object);

	/**
	 * Store an object in an database.
	 * 
	 * To detect if object must be updated or insert, we use the cache. To
	 * update an object, it must be first selected from the database. When an
	 * object is to be stored, if it exist in the cache, then it will be
	 * updated, else it will be inserted as a new object. If the object is null,
	 * the cache will be used to check if the meta representation is in the
	 * cache
	 * 
	 */
	OID store(Object object);

	void deleteObjectWithOid(OID oid, boolean cascade);

	/**
	 * Actually deletes an object database
	 * @param object
	 * @param cascade
	 */
	public OID delete(Object object, boolean cascade);


	void close();

	long count(CriteriaQuery query);

	Values getValues(IValuesQuery query, int startIndex, int endIndex);

	<T>Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex);

	<T>Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex);

	/**
	 * Return Meta representation of objects
	 * 
	 * @param query
	 *            The query to select objects
	 * @param inMemory
	 *            To indicate if object must be all loaded in memory
	 * @param startIndex
	 *            First object index
	 * @param endIndex
	 *            Last object index
	 * @param returnOjects
	 *            To indicate if object instances must be created
	 * @return The list of objects @
	 * 
	 */
	<T>Objects<T> getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnOjects);

	IObjectReader getObjectReader();

	IObjectWriter getObjectWriter();

	ITriggerManager getTriggerManager();

	ISession getSession(boolean throwExceptionIfDoesNotExist);

	ISession buildDefaultSession();

	void commit();

	void rollback();

	OID getObjectId(Object object, boolean throwExceptionIfDoesNotExist);

	Object getObjectFromOid(OID oid);

	NonNativeObjectInfo getMetaObjectFromOid(OID oid);

	ObjectInfoHeader getObjectInfoHeaderFromOid(OID oid, boolean useCache);

	void defragmentTo(String newFileName);

	List<Long> getAllObjectIds();

	List<FullIDInfo> getAllObjectIdInfos(String objectType, boolean displayObjects);

	/**
	 * @return Returns the currentIdBlockNumber.
	 */
	int getCurrentIdBlockNumber();

	/**
	 * @return Returns the currentIdBlockPosition.
	 */
	long getCurrentIdBlockPosition();

	/**
	 * @return Returns the currentIdBlockMaxId.
	 */
	OID getCurrentIdBlockMaxOid();

	OID getMaxOid();

	boolean isClosed();

	int getVersion();

	void addUpdateTriggerFor(String className, UpdateTrigger trigger);

	void addInsertTriggerFor(String className, InsertTrigger trigger);
	
	void addOidTriggerFor(String className, OIDTrigger trigger);

	void addDeleteTriggerFor(String className, DeleteTrigger trigger);

	void addSelectTriggerFor(String className, SelectTrigger trigger);

	void setVersion(int version);

	void setDatabaseId(DatabaseId databaseId);

	void setNbClasses(long nbClasses);

	void setLastODBCloseStatus(boolean lastCloseStatus);

	void setCurrentIdBlockInfos(long currentBlockPosition, int currentBlockNumber, OID maxId);

	void setMetaModel(MetaModel metaModel);

	IBaseIdentification getBaseIdentification();

	/**
	 * Write an object already transformed into meta representation!
	 * 
	 * @param oid
	 * @param nnoi
	 * @param position
	 * @param updatePointers
	 * @return te object position(or id (if <0, it is id)) @
	 */
	public OID writeObjectInfo(OID oid, NonNativeObjectInfo nnoi, long position, boolean updatePointers);

	/**
	 * Updates an object already transformed into meta representation!
	 * 
	 * 
	 * @param nnoi
	 *            The Object Meta representation
	 * @param forceUpdate
	 * @return The OID of the update object @
	 */
	public OID updateObject(NonNativeObjectInfo nnoi, boolean forceUpdate);

	void addSession(ISession session, boolean readMetamodel);

	/**
	 * 
	 * @param className
	 *            The class name on which the index must be created
	 * @param name
	 *            The name of the index
	 * @param indexFields
	 *            The list of fields of the index
	 * @param verbose
	 *            A boolean value to indicate of ODB must describe what it is
	 * doing @ @
	 */
	void addIndexOn(String className,  String name, String[] indexFields, boolean verbose, boolean acceptMultipleValuesForSameKey) ;

	void addCommitListener(ICommitListener commitListener);

	IOdbList<ICommitListener> getCommitListeners();

	/** Returns the object used to refactor the database */
	IRefactorManager getRefactorManager();

	void resetCommitListeners();

	/**
	 * Used to know if the storage engine is executed in local mode (embedded
	 * mode) or client server mode
	 */
	public boolean isLocal();

	/*
	 * Adds a list of class to the metamodel, if it already exists simply
	 * returns the original one
	 */
	public abstract ClassInfoList addClasses(ClassInfoList classInfoList);

	DatabaseId getDatabaseId();

	TransactionId getCurrentTransactionId();

	void setCurrentTransactionId(TransactionId transactionId);

	/** Used to reconnect an object to the current session */
	void reconnect(Object object);

	/**
	 * Used to disconnect the object from the current session. The object is
	 * removed from the cache
	 */
	void disconnect(Object object);

	/**
	 * 
	 * @param className
	 * @param indexName
	 * @param verbose
	 */
	void rebuildIndex(String className, String indexName, boolean verbose);
	
	/**
	 * 
	 * @param className
	 * @param indexName
	 * @param verbose
	 */
	public void deleteIndex(String className, String indexName, boolean verbose);

	/**
	 * Receive the current class info (loaded from current java classes present on classpath
	 * and check against the persisted meta model
	 * @param currentCIs
	 */
	public CheckMetaModelResult checkMetaModelCompatibility(Map<String, ClassInfo> currentCIs);
	
	public IObjectIntrospector buildObjectIntrospector();

	public IObjectWriter buildObjectWriter();

	public IObjectReader buildObjectReader();

	public ITriggerManager buildTriggerManager();
	
	public IObjectIntrospector getObjectIntrospector();

	/**
	 * @param clazz
	 * @param criterion
	 * @return
	 */
	CriteriaQuery criteriaQuery(Class clazz, ICriterion criterion);

	/**
	 * @param clazz
	 * @return
	 */
	CriteriaQuery criteriaQuery(Class clazz);

	
	
}