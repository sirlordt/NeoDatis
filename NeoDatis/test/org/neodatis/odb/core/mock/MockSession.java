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
package org.neodatis.odb.core.mock;

import java.util.Observable;

import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.odb.core.transaction.ITransaction;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.odb.impl.core.transaction.Session;

/**
 * A fake session used for testsmo
 * 
 * @author olivier s
 * 
 */
public class MockSession extends Session {
	public MockSession(String baseIdentification) {
		super("mock", baseIdentification);
		this.metaModel = new SessionMetaModel();
	}

	public ICache buildCache() {
		return CacheFactory.getLocalCache(this, "mock");
	}

	public void commit() {
	}

	public IStorageEngine getStorageEngine() {
		try {
			return new MockStorageEngine();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return null;
	}

	public ITransaction getTransaction() {
		return null;
	}

	public void setFileSystemInterfaceToApplyTransaction(IFileSystemInterface fsi) {
	}

	public boolean transactionIsPending() {
		return false;
	}

	public MetaModel getMetaModel() {
		return metaModel;
	}

	public ITmpCache buildTmpCache() {
		return CacheFactory.getLocalTmpCache(this, "mock-tmp");
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
