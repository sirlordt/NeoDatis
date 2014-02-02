/**
 * 
 */
package org.neodatis.odb.impl.main;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBExt;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.tool.mutex.Mutex;
import org.neodatis.tool.mutex.MutexFactory;

/**
 * Used to synchronized access to the db in multi thread.
 * 
 * @author olivier
 * 
 */
public class ThreadSafeLocalODB implements ODB {
	protected ODB localOdb;
	/**
	 * time out to acquire the mutex
	 * 
	 */
	protected static final String THREAD_MUTEX_PREFIX = "thread-safe-mutex-";
	protected Mutex mutex;
	private Map<String, Boolean> holdsTheMutex;

	/**
	 * neodatiesee : when true, the mutex is only released on commit or close,
	 * which would prevent another thread acessing the db before commit. if
	 * false, the mutex is released after the getObjects, store and delete
	 * 
	 */
	protected boolean multiThreadExclusive;

	public ThreadSafeLocalODB(ODB odb) {
		this.localOdb = odb;
		mutex = MutexFactory.get(THREAD_MUTEX_PREFIX + odb.getName());
		holdsTheMutex = new HashMap<String, Boolean>();
		multiThreadExclusive = OdbConfiguration.multiThreadExclusive();
	}

	public ODB getNonThreadSafeOdb() {
		return localOdb;
	}

	protected boolean holdsTheMutex(String threadName) {
		synchronized (holdsTheMutex) {
			Boolean b = holdsTheMutex.get(threadName);
			if (b == null) {
				return false;
			}
			return b.booleanValue();
		}
	}

	protected void acquireMutex() {
		String threadName = Thread.currentThread().getName();
		if (holdsTheMutex(threadName)) {
			return;
		}
		Boolean b = null;
		try {
			b = mutex.attempt(OdbConfiguration.getTimeoutToAcquireMutexInMultiThread());
			holdsTheMutex.put(threadName, b);
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_ACQUIRING_MUTEX.addParameter(THREAD_MUTEX_PREFIX + localOdb.getName()),
					e);
		}
		if (!b.booleanValue()) {
			Exception e = new Exception("Timeout acquiring mutex ");
			e.printStackTrace();
			throw new ODBRuntimeException(NeoDatisError.TIMEOUT_WHILE_ACQUIRING_MUTEX
					.addParameter(THREAD_MUTEX_PREFIX + localOdb.getName()), e);
		}
	}

	protected void releaseMutex() {
		String threadName = Thread.currentThread().getName();
		if (holdsTheMutex(threadName)) {
			mutex.release(localOdb.getName());
			holdsTheMutex.put(threadName, Boolean.FALSE);
		}
	}

	public void addDeleteTrigger(Class clazz, DeleteTrigger trigger) {
		acquireMutex();
		localOdb.addDeleteTrigger(clazz, trigger);
	}

	public void addInsertTrigger(Class clazz, InsertTrigger trigger) {
		acquireMutex();
		localOdb.addInsertTrigger(clazz, trigger);
	}

	public void addSelectTrigger(Class clazz, SelectTrigger trigger) {
		acquireMutex();
		localOdb.addSelectTrigger(clazz, trigger);
	}

	public void addUpdateTrigger(Class clazz, UpdateTrigger trigger) {
		acquireMutex();
		localOdb.addUpdateTrigger(clazz, trigger);
	}

	public void close() {
		acquireMutex();
		localOdb.close();
		releaseMutex();
	}

	public void commit() {
		acquireMutex();
		localOdb.commit();
		releaseMutex();
	}

	public BigInteger count(CriteriaQuery query) {
		try {
			acquireMutex();
			return localOdb.count(query);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public CriteriaQuery criteriaQuery(Class clazz, ICriterion criterio) {
		try {
			acquireMutex();
			return localOdb.criteriaQuery(clazz, criterio);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public CriteriaQuery criteriaQuery(Class clazz) {
		try {
			acquireMutex();
			return localOdb.criteriaQuery(clazz);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public void defragmentTo(String newFileName) {
		acquireMutex();
		localOdb.defragmentTo(newFileName);
	}

	public OID delete(Object object) {
		try {
			acquireMutex();
			return localOdb.delete(object);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public OID deleteCascade(Object object) {
		try {
			acquireMutex();
			return localOdb.deleteCascade(object);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public void deleteObjectWithId(OID oid) {
		try {
			acquireMutex();
			localOdb.deleteObjectWithId(oid);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public void disconnect(Object object) {
		acquireMutex();
		localOdb.disconnect(object);
	}

	public ODBExt ext() {
		acquireMutex();
		return localOdb.ext();
	}

	public ClassRepresentation getClassRepresentation(Class clazz) {
		acquireMutex();
		return localOdb.getClassRepresentation(clazz);
	}

	public ClassRepresentation getClassRepresentation(String fullClassName) {
		acquireMutex();
		return localOdb.getClassRepresentation(fullClassName);
	}

	public ClassRepresentation getClassRepresentation(String fullClassName, boolean laodClass) {
		acquireMutex();
		return localOdb.getClassRepresentation(fullClassName, laodClass);
	}

	public String getName() {
		return localOdb.getName();
	}

	public Object getObjectFromId(OID id) {
		acquireMutex();
		return localOdb.getObjectFromId(id);
	}

	public OID getObjectId(Object object) {
		try {
			acquireMutex();
			return localOdb.getObjectId(object);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public <T> Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex) {
		try {
			acquireMutex();
			return localOdb.getObjects(clazz, inMemory, startIndex, endIndex);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public <T> Objects<T> getObjects(Class clazz, boolean inMemory) {
		try {
			acquireMutex();
			return localOdb.getObjects(clazz, inMemory);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public <T> Objects<T> getObjects(Class clazz) {
		try {
			acquireMutex();
			return localOdb.getObjects(clazz);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public <T> Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {
		try {
			acquireMutex();
			return localOdb.getObjects(query, inMemory, startIndex, endIndex);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}
	}

	public <T> Objects<T> getObjects(IQuery query, boolean inMemory) {
		try {
			acquireMutex();
			return localOdb.getObjects(query, inMemory);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public <T> Objects<T> getObjects(IQuery query) {
		try {
			acquireMutex();
			return localOdb.getObjects(query);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public IRefactorManager getRefactorManager() {
		acquireMutex();
		return localOdb.getRefactorManager();
	}

	public Values getValues(IValuesQuery query) {
		try {
			acquireMutex();
			return localOdb.getValues(query);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

	public boolean isClosed() {
		return localOdb.isClosed();
	}

	public void reconnect(Object object) {
		acquireMutex();
		localOdb.reconnect(object);
	}

	public void rollback() {
		acquireMutex();
		localOdb.rollback();
		releaseMutex();
	}

	public OID store(Object object) {
		try {
			acquireMutex();
			return localOdb.store(object);
		} finally {
			if (!multiThreadExclusive) {
				releaseMutex();
			}
		}

	}

}
