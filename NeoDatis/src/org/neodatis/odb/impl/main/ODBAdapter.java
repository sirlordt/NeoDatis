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
package org.neodatis.odb.impl.main;

import java.math.BigInteger;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.DatabaseStartupManager;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBExt;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.server.trigger.ServerDeleteTrigger;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;
import org.neodatis.odb.core.server.trigger.ServerSelectTrigger;
import org.neodatis.odb.core.server.trigger.ServerUpdateTrigger;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.impl.core.transaction.CrossSessionCache;
import org.neodatis.tool.DLogger;

/**
 * A basic adapter for ODB interface
 * 
 * @author osmadja
 * 
 */
public abstract class ODBAdapter implements ODB {

	protected IStorageEngine storageEngine;

	protected IClassIntrospector classIntrospector;
	private ODBExt ext;

	public ODBAdapter(IStorageEngine storageEngine) {
		super();
		this.storageEngine = storageEngine;
		this.classIntrospector = OdbConfiguration.getCoreProvider().getClassIntrospector();

		DatabaseStartupManager manager = OdbConfiguration.getDatabaseStartupManager();
		if (manager != null) {
			manager.start(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#commit()
	 */
	public void commit() {
		storageEngine.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#rollback()
	 */
	public void rollback() {
		storageEngine.rollback();
	}

	/*
	 * @depracated
	 */
	public void commitAndClose() {
		storageEngine.commit();
		storageEngine.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#store(java.lang.Object)
	 */
	public OID store(Object object) {
		return storageEngine.store(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjects(java.lang.Class)
	 */
	public <T> Objects<T> getObjects(Class clazz) {
		return storageEngine.getObjects(new CriteriaQuery(clazz), true, -1, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjects(java.lang.Class, boolean)
	 */
	public <T> Objects<T> getObjects(Class clazz, boolean inMemory) {
		return storageEngine.getObjects(clazz, inMemory, -1, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjects(java.lang.Class, boolean, int,
	 * int)
	 */
	public <T> Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex) {
		return storageEngine.getObjects(clazz, inMemory, startIndex, endIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#close()
	 */
	public void close() {
		storageEngine.commit();
		storageEngine.close();
	}

	public OID delete(Object object) {
		return storageEngine.delete(object, false);
	}

	public OID deleteCascade(Object object) {
		return storageEngine.delete(object, true);
	}

	/**
	 * Delete an object from the database with the id
	 * 
	 * @param oid
	 *            The object id to be deleted @
	 */
	public void deleteObjectWithId(OID oid) {
		storageEngine.deleteObjectWithOid(oid, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery)
	 */
	public <T> Objects<T> getObjects(IQuery query) {
		return storageEngine.getObjects(query, true, -1, -1);
	}

	public Values getValues(IValuesQuery query) {
		return storageEngine.getValues(query, -1, -1);
	}

	public BigInteger count(CriteriaQuery query) {
		IValuesQuery q = new ValuesCriteriaQuery(query).count("count");
		q.setPolymorphic(query.isPolymorphic());
		Values values = storageEngine.getValues(q, -1, -1);
		BigInteger count = (BigInteger) values.nextValues().getByIndex(0);
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery,
	 * boolean)
	 */
	public <T> Objects<T> getObjects(IQuery query, boolean inMemory) {
		return storageEngine.getObjects(query, inMemory, -1, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery,
	 * boolean, int, int)
	 */
	public <T> Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {
		return storageEngine.getObjects(query, inMemory, startIndex, endIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getSession()
	 */
	public ISession getSession() {
		return storageEngine.getSession(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjectId(java.lang.Object)
	 */
	public OID getObjectId(Object object) {
		return storageEngine.getObjectId(object, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjectFromId(long)
	 */
	public Object getObjectFromId(OID id) {
		return storageEngine.getObjectFromOid(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#defragmentTo(java.lang.String)
	 */
	public void defragmentTo(String newFileName) {
		storageEngine.defragmentTo(newFileName);
	}

	public ClassRepresentation getClassRepresentation(Class clazz) {
		return getClassRepresentation(clazz.getName());
	}

	public ClassRepresentation getClassRepresentation(String fullClassName) {
		return getClassRepresentation(fullClassName, true);
	}

	public ClassRepresentation getClassRepresentation(String fullClassName, boolean loadClass) {
		ClassInfo classInfo = null;
		if (loadClass) {
			classInfo = storageEngine.getSession(true).getMetaModel().getClassInfo(fullClassName, false);
			if (classInfo == null) {
				ClassInfoList ciList = classIntrospector.introspect(fullClassName, true);
				storageEngine.addClasses(ciList);
				classInfo = ciList.getMainClassInfo();
			}
		} else {
			classInfo = new ClassInfo(fullClassName);
		}
		return new DefaultClassRepresentation(storageEngine, classInfo);
	}

	/** or shutdown hook */
	public void run() {
		if (!storageEngine.isClosed()) {
			DLogger.debug("ODB has not been closed and VM is exiting : force ODB close");
			storageEngine.close();
		}
	}

	public void addUpdateTrigger(Class clazz, UpdateTrigger trigger) {
		if (trigger instanceof ServerUpdateTrigger) {
			throw new ODBRuntimeException(NeoDatisError.CAN_NOT_ASSOCIATE_SERVER_TRIGGER_TO_LOCAL_OR_CLIENT_ODB.addParameter(trigger.getClass().getName()));
		}
		storageEngine.addUpdateTriggerFor(clazz.getName(), trigger);
	}

	public void addInsertTrigger(Class clazz, InsertTrigger trigger) {
		if (trigger instanceof ServerInsertTrigger) {
			throw new ODBRuntimeException(NeoDatisError.CAN_NOT_ASSOCIATE_SERVER_TRIGGER_TO_LOCAL_OR_CLIENT_ODB.addParameter(trigger.getClass().getName()));
		}
		storageEngine.addInsertTriggerFor(clazz.getName(), trigger);
	}

	public void addDeleteTrigger(Class clazz, DeleteTrigger trigger) {
		if (trigger instanceof ServerDeleteTrigger) {
			throw new ODBRuntimeException(NeoDatisError.CAN_NOT_ASSOCIATE_SERVER_TRIGGER_TO_LOCAL_OR_CLIENT_ODB.addParameter(trigger.getClass().getName()));
		}
		storageEngine.addDeleteTriggerFor(clazz.getName(), trigger);
	}

	public void addSelectTrigger(Class clazz, SelectTrigger trigger) {
		if (trigger instanceof ServerSelectTrigger) {
			throw new ODBRuntimeException(NeoDatisError.CAN_NOT_ASSOCIATE_SERVER_TRIGGER_TO_LOCAL_OR_CLIENT_ODB.addParameter(trigger.getClass().getName()));
		}
		storageEngine.addSelectTriggerFor(clazz.getName(), trigger);
	}

	public IRefactorManager getRefactorManager() {
		return storageEngine.getRefactorManager();
	}

	public ODBExt ext() {
		if (isClosed()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(getName()));
		}
		if (ext == null) {
			ext = new ODBExtImpl(storageEngine);
		}
		return ext;
	}

	public void disconnect(Object object) {
		storageEngine.disconnect(object);
	}

	public void reconnect(Object object) {
		storageEngine.reconnect(object);

	}

	public boolean isClosed() {
		return storageEngine.isClosed();
	}

	public CriteriaQuery criteriaQuery(Class clazz, ICriterion criterion) {
		return storageEngine.criteriaQuery(clazz, criterion);
	}

	public CriteriaQuery criteriaQuery(Class clazz) {
		return storageEngine.criteriaQuery(clazz);
	}

	public String getName() {
		return storageEngine.getBaseIdentification().getIdentification();
	}
}
