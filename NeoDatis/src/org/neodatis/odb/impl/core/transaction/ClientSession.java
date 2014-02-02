
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

import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.odb.core.transaction.ITransaction;

/**
 * The client session when ODB is used in client server mode
 * @author olivier s
 *
 */
public class ClientSession extends Session{
	protected IStorageEngine engine;
	
	public ClientSession(IStorageEngine engine){
		super("client", engine.getBaseIdentification().getIdentification());
		this.engine = engine;

	}
	public ICache getCache(){
		return cache;
	}
	public void commit() {
	}
    public void rollback() {
    	super.rollback();
    }
    public void close(){
    	clear();
    }
	public void clearCache() {
		cache.clear(false);		
	}
	public boolean isRollbacked() {
		return rollbacked;
	}
    public void clear(){
    	super.clear();
    }
	public IStorageEngine getStorageEngine() {
		return engine;
	}
	public boolean transactionIsPending() {
		// TODO do this right
		return false;
	}
	public ITransaction getTransaction() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setFileSystemInterfaceToApplyTransaction(IFileSystemInterface fsi) {
		// TODO Auto-generated method stub
		
	}
	public ICache buildCache() {
		return CacheFactory.getLocalCache(this,"permanent");
	}
	public ITmpCache buildTmpCache() {
		return CacheFactory.getLocalTmpCache(this,"tmp");
	}
	public void update(Observable o, Object arg) {
		
	}
    
}
