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
package org.neodatis.odb.impl.core.layers.layer3.engine;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.CheckMetaModelResult;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.query.IQuery;
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
import org.neodatis.odb.impl.core.transaction.CacheFactory;

/**
 * An Adapter for IStorageEngine interface.
 * 
 * @author osmadja
 * 
 */
public abstract class StorageEngineAdapter implements IStorageEngine {
	
	private DatabaseId databaseId;
	/** To keep track of current transaction Id
	 * 
	 */
	protected TransactionId currentTransactionId;
	
	/** To manage triggers */
	protected ITriggerManager triggerManager;
	
	protected boolean isClosed;


	public ClassInfo addClass(ClassInfo newClassInfo, boolean addDependentClasses)  {

		return null;
	}

    /*
     * Adds a list of class to the metamodel, if it already exists simply returns the original one 
     */
    public ClassInfoList addClasses(ClassInfoList classInfoList)  {

    	Iterator iterator = classInfoList.getClassInfos().iterator();
    	
    	while(iterator.hasNext()){
    		addClass((ClassInfo) iterator.next(),false);
    	}
    	return classInfoList;
    }

	public abstract void addDeleteTrigger(Class clazz, DeleteTrigger trigger);
	public abstract void addInsertTrigger(Class clazz, InsertTrigger trigger);	
	public abstract void addOidTrigger(Class clazz, OIDTrigger trigger);
	public abstract void addSelectTrigger(Class clazz, SelectTrigger trigger);
	public abstract void addUpdateTrigger(Class clazz, UpdateTrigger trigger);

	public void close()  {

	}

	public void commit()  {

	}

	public long count(String fullClassName) {

		return 0;
	}

	public long count(Class clazz) throws Exception {

		return 0;
	}

	public void defragmentTo(String newFileName) {

	}

	public OID delete(Object object, boolean cascade)  {
		return null;

	}

	public OID internalDelete(ObjectInfoHeader header)  {
		return null;
	}

	public void deleteObjectWithOid(long oid) throws Exception {

	}

	public List<FullIDInfo> getAllObjectIdInfos(String objectType, boolean displayObjects) {
		return null;
	}

	public List<Long> getAllObjectIds()  {
		return null;
	}

	public OID getCurrentIdBlockMaxOid() {
		return StorageEngineConstant.NULL_OBJECT_ID;
	}

	public int getCurrentIdBlockNumber() {
		return 0;
	}

	public long getCurrentIdBlockPosition() {
		return 0;
	}

	public IFileSystemInterface getFsi() {
		return null;
	}

	public OID getMaxOid() {

		return StorageEngineConstant.NULL_OBJECT_ID;
	}

	public MetaModel get2MetaModel() {

		return null;
	}

	public Object getObjectFromId(OID id) throws Exception {

		return null;
	}

	public OID getObjectId(Object object, boolean throwExceptionIfDoesNotExist) {

		return null;
	}

	public <T>Objects<T> getObjectInfos(String fullClassName, IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnOjects)
			throws Exception {

		return null;
	}

	public <T>Objects<T> getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnOjects)  {

		return null;
	}

	public IObjectReader getObjectReader() {

		return null;
	}

	public IObjectWriter getObjectWriter() {

		return null;
	}

	public <T>Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {

		return null;
	}

	public <T>Objects<T> getObjects(String fullClassName, IQuery query, boolean inMemory, int startIndex, int endIndex) throws Exception {

		return null;
	}

	public ISession getSession(boolean throwExceptionIfDoesNotExist) {

		return null;
	}

	public int getVersion() {

		return 0;
	}

	public boolean isClosed() {

		return isClosed;
	}

	public long mainStoreObject(Object object) throws Exception {

		return 0;
	}

	public ClassInfo persistClass(ClassInfo newClassInfo, int lastClassInfoIndex, boolean addClass, boolean addDependentClasses)
			 {

		return null;
	}

	public void rollback()  {

	}

	public void setCurrentIdBlockInfos(long currentBlockPosition, int currentBlockNumber, OID maxId) {

	}

	public void setDatabaseId(long[] databaseId) {

	}

	public void setLastODBCloseStatus(boolean lastCloseStatus) {

	}

	public void setMetaModel(MetaModel metaModel)  {

	}

	public void setNbClasses(long nbClasses) {

	}

	public void setVersion(int version) {

	}
	
	public TransactionId getCurrentTransactionId() {
		return currentTransactionId;
	}
	public void setCurrentTransactionId(TransactionId transactionId) {
		currentTransactionId = transactionId;
	}

	public void setDatabaseId(DatabaseId databaseId) {
		this.databaseId = databaseId;
		
	}
	public DatabaseId getDatabaseId() {
		return databaseId;
	}

	public void disconnect(Object object) {
		if(OdbConfiguration.reconnectObjectsToSession()){
			CacheFactory.getCrossSessionCache(this.getBaseIdentification().getIdentification()).removeObject(object);
		}
	}

	public void reconnect(Object object) {
		// nothing to do
	}
	
	public ITriggerManager getTriggerManager() {
		return triggerManager;
	}

	public void addDeleteTriggerFor(String className, DeleteTrigger trigger) {
		triggerManager.addDeleteTriggerFor(className, trigger);
	}

	public void addInsertTriggerFor(String className, InsertTrigger trigger) {
		triggerManager.addInsertTriggerFor(className, trigger);
	}
	public void addOidTriggerFor(String className, OIDTrigger trigger) {
		triggerManager.addOidTriggerFor(className, trigger);
	}

	public void addSelectTriggerFor(String className, SelectTrigger trigger) {
		triggerManager.addSelectTriggerFor(className, trigger);	
	}

	public void addUpdateTriggerFor(String className, UpdateTrigger trigger) {
		triggerManager.addUpdateTriggerFor(className, trigger);
	}

	
	public abstract <T>Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex,
			int endIndex);

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.layers.layer3.IStorageEngine#checkMetaModelCompatibility(java.util.Map)
	 */
	public CheckMetaModelResult checkMetaModelCompatibility(Map<String, ClassInfo> currentCIs) {
		return null;
	}
	public CriteriaQuery criteriaQuery(Class clazz, ICriterion criterion) {
		CriteriaQuery q = new CriteriaQuery(clazz,criterion);
		q.setStorageEngine(this);
		if(criterion!=null){
			criterion.ready();
		}
		return q;
	}
	public CriteriaQuery criteriaQuery(Class clazz) {
		CriteriaQuery q = new CriteriaQuery(clazz);
		q.setStorageEngine(this);
		return q;
	}

}
