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

import java.util.Observable;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.odb.core.transaction.ITransaction;
import org.neodatis.tool.wrappers.OdbRandom;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * The session object used when ODB is used in local/embedded mode
 * 
 * @author olivier s
 * 
 */
public class LocalSession extends Session {
	private ITransaction transaction;

	private IFileSystemInterface fsiToApplyTransaction;

	private IStorageEngine storageEngine;

	public LocalSession(IStorageEngine engine, String sessionId) {
		super(sessionId, engine.getBaseIdentification().getIdentification());
		this.storageEngine = engine;
	}

	public LocalSession(IStorageEngine engine) {
		this(engine, "local " + OdbTime.getCurrentTimeInMs() + OdbRandom.getRandomInteger());
	}

	public void setFileSystemInterfaceToApplyTransaction(IFileSystemInterface fsi) {
		fsiToApplyTransaction = fsi;
		if (transaction != null) {
			transaction.setFsiToApplyWriteActions(fsiToApplyTransaction);
		}
	}

	public ITransaction getTransaction() {
		if (transaction == null) {
			transaction = OdbConfiguration.getCoreProvider().getTransaction(this, fsiToApplyTransaction);
		}
		return transaction;
	}

	public boolean transactionIsPending() {
		if (transaction == null) {
			return false;
		}
		return transaction.getNumberOfWriteActions() != 0;
	}

	private void resetTranstion() {
		if (transaction != null) {
			transaction.clear();
			transaction = null;
		}
	}

	public void commit() {
		if (transaction != null) {
			transaction.commit();
			transaction.reset();
			setHasBeenCommitted(true);
		}
	}

	public void rollback() {
		if (transaction != null) {
			transaction.rollback();
			resetTranstion();
		}
		super.rollback();
	}

	public IStorageEngine getStorageEngine() {
		return storageEngine;
	}

	public void clear() {
		super.clear();
		if (transaction != null) {
			transaction.clear();
		}
		storageEngine = null;
	}

	public ICache buildCache() {
		return CacheFactory.getLocalCache(this, "permanent");
	}

	public ITmpCache buildTmpCache() {
		return CacheFactory.getLocalTmpCache(this, "tmp");
	}

	public void update(Observable o, Object arg) {
		// used to receive notifications
		
	}

}
