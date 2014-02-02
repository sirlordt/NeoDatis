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

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.odb.core.transaction.ITransaction;

/**
 * An ODB Session. Keeps track of all the session operations. Caches objects and
 * manage the transaction.
 * 
 * The meta model of the database is stored in the session.
 * 
 * @author osmadja
 * 
 */
public abstract class Session implements Comparable, ISession {
	protected ICache cache;
	/** A temporary cache used for object info read */
	protected ITmpCache tmpCache;
	protected boolean rollbacked;
	public String id;
	protected String baseIdentification;
	protected MetaModel metaModel;
	/**
	 * To indicate that session has already been committed once
	 */
	protected boolean hasBeenCommitted;

	public Session(String id, String baseIdentification) {
		cache = buildCache();
		tmpCache = buildTmpCache();
		this.id = id;
		this.baseIdentification = baseIdentification;
	}

	public abstract ICache buildCache();

	public abstract ITmpCache buildTmpCache();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#getCache()
	 */
	public ICache getCache() {
		return cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#getTmpCache()
	 */
	public ITmpCache getTmpCache() {
		return tmpCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#rollback()
	 */
	public void rollback() {
		clearCache();
		rollbacked = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#close()
	 */
	public void close() {
		clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#clearCache()
	 */
	public void clearCache() {
		cache.clear(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#isRollbacked()
	 */
	public boolean isRollbacked() {
		return rollbacked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#clear()
	 */
	public void clear() {
		cache.clear(true);
		if (metaModel != null) {
			metaModel.clear();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String sessionId) {
		this.id = sessionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#getStorageEngine()
	 */
	public abstract IStorageEngine getStorageEngine();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#transactionIsPending()
	 */
	public abstract boolean transactionIsPending();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#commit()
	 */
	public abstract void commit();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#getTransaction()
	 */
	public abstract ITransaction getTransaction();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#setFileSystemInterfaceToApplyTransaction(org.neodatis.odb.core.impl.layers.layer3.engine.FileSystemInterface)
	 */
	public abstract void setFileSystemInterfaceToApplyTransaction(IFileSystemInterface fsi);

	public String toString() {
		ITransaction transaction = null;
		transaction = getTransaction();

		if (transaction == null) {
			return "name=" + baseIdentification+  " sid=" + id + " - no transaction";
		}
		int n = transaction.getNumberOfWriteActions();
		return "name="+ baseIdentification + " - sid=" + id + " - Nb Actions = " + n;
	}

	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Session)) {
			return false;
		}
		ISession session = (ISession) obj;
		return getId().equals(session.getId());
	}

	public int compareTo(Object o) {
		if (o == null || !(o instanceof Session)) {
			return -100;
		}
		ISession session = (ISession) o;
		return getId().compareTo(session.getId());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#getBaseIdentification()
	 */
	public String getBaseIdentification() {
		return baseIdentification;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#getMetaModel()
	 */
	public MetaModel getMetaModel() {
		if (metaModel == null) {
			// MetaModel can be null (this happens at the end of the
			// Transaction.commitMetaModel() method)when the user commited the
			// database
			// And continue using it. In this case, after the commit, the
			// metamodel is set to null
			// and lazy-reloaded when the user use the odb again.
			metaModel = new SessionMetaModel();
			try {
				getStorageEngine().getObjectReader().readMetaModel(metaModel, true);
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("Session.getMetaModel"), e);

			}
		}
		return metaModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.session.ISession#setMetaModel(org.neodatis.odb.core.impl.layers.layer2.meta.MetaModel)
	 */
	public void setMetaModel(MetaModel metaModel2) {
		this.metaModel = metaModel2;
	}

	public void setBaseIdentification(String baseIdentification) {
		this.baseIdentification = baseIdentification;
	}

	public void removeObjectFromCache(Object object) {
		cache.removeObject(object);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.neodatis.odb.core.transaction.ISession#addObjectToCache(org.neodatis.odb.OID, java.lang.Object, org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader)
	 */
	public void addObjectToCache(OID oid, Object object, ObjectInfoHeader oih) {
		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OBJECT.addParameter(object));
		}
		
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID.addParameter(oid));
		}
		
		if (oih == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OBJECT.addParameter(oih));
		}
		
		cache.addObject(oid, object, oih);		
	}

	public boolean hasBeenCommitted() {
		return hasBeenCommitted;
	}

	public void setHasBeenCommitted(boolean hasBeenCommitted) {
		this.hasBeenCommitted = hasBeenCommitted;
	}

}
