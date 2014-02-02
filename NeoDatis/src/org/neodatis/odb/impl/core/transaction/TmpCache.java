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

import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * A temporary cache of objects.
 * 
 * 
 * @author olivier s
 * 
 */
public class TmpCache implements ITmpCache {

	/** To resolve cyclic reference, keep track of objects being read */
	protected Map<OID,Object[]> readingObjectInfo;
	protected ISession session;
	protected String name;

	public TmpCache(ISession session, String name) {
		init(session, name);
	}

	protected void init(ISession session, String name) {
		this.name = name;
		this.session = session;
		readingObjectInfo = new OdbHashMap<OID, Object[]>();
	}

    public boolean isReadingObjectInfoWithOid(OID oid) {
    	if(oid==null){
    		return false;
    	}
        return readingObjectInfo.containsKey(oid);
    }

	public NonNativeObjectInfo getReadingObjectInfoFromOid(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		Object[] values = (Object[]) readingObjectInfo.get(oid);
		if(values==null){
			return null;
		}
		return (NonNativeObjectInfo) values[1];
	}


	public void startReadingObjectInfoWithOid(OID oid, NonNativeObjectInfo objectInfo) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CACHE_NULL_OID);
		}

		Object[] objects = (Object[]) readingObjectInfo.get(oid);
		// TODO : use a value object instead of an array!
		if (objects == null) {
			// The key is the oid, the value is an array of 2 objects :
			// 1-the read count, 2-The object info
			// Here we are saying that the object with oid 'oid' is
			// being read for the first time
			Object[] values = { new Short((short) 1), objectInfo };
			readingObjectInfo.put(oid, values);
		} else {
			// Here the object is already being read. It is necessary to
			// increase the read count
			short currentReadCount = ((Short) objects[0]).shortValue();
			objects[0] = new Short((short) (currentReadCount + 1));
			// Object is in memory, do not need to re-put in map. The key has
			// not changed
			// readingObjectInfo.put(oid, objects);
		}
	}

	public void clearObjectInfos(){
		readingObjectInfo.clear();
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.transaction.ITmpCache#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return readingObjectInfo.size();
	}
}
