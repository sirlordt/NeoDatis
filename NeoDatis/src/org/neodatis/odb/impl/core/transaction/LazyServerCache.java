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

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/** @sharpen.ignore
 * A specific cache for the server in Client/Server mode
 * @author osmadja
 *
 */
public class LazyServerCache extends LazyCache {
	/** Object id of NonNativeObjectInfo*/
	protected Map<OID,NonNativeObjectInfo> oidsOfNNoi;
	public LazyServerCache(ISession session) {
		super(session);
		oidsOfNNoi = new OdbHashMap<OID, NonNativeObjectInfo>();
	}
	public void addOid(OID oid, NonNativeObjectInfo nnoi){
		oidsOfNNoi.put(oid, nnoi);
	}
	public NonNativeObjectInfo getNnoi(OID oid){
		return (NonNativeObjectInfo) oidsOfNNoi.get(oid);
	}

    public void startInsertingObject(Object object, OID oid, NonNativeObjectInfo nnoi) {
    	super.startInsertingObjectWithOid(object, oid, nnoi);
    	addOid(oid, nnoi);
    }
    
    public void clear(boolean setToNull) {
    	super.clear(setToNull);
    	oidsOfNNoi.clear();
    	if(setToNull){
    		oidsOfNNoi=null;
    	}
    }
}
