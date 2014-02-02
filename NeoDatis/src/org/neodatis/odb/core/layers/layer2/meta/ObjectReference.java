
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
package org.neodatis.odb.core.layers.layer2.meta;

import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;

/**
 * Meta representation of an object reference. 
 * @author osmadja
 *
 */
public class ObjectReference extends AbstractObjectInfo {
    private OID id;
    private NonNativeObjectInfo nnoi;
    public ObjectReference(OID id) {
        super(ODBType.NON_NATIVE_ID);
        this.id = id;
    }
    public ObjectReference(NonNativeObjectInfo nnoi) {
        super(ODBType.NON_NATIVE_ID);
        this.id = null;
        this.nnoi = nnoi;
    }
    /**
     * @return Returns the id.
     */
    public OID getOid() {
    	if(nnoi!=null){
    		return nnoi.getOid();
    	}
        return id;
    }
    public boolean isObjectReference(){
        return true;
    }
    public String toString() {
    	return "ObjectReference to oid "+getOid();
    }

    public boolean isNull(){
    	return false;
    }
	public Object getObject() {
		throw new ODBRuntimeException(NeoDatisError.METHOD_SHOULD_NOT_BE_CALLED.addParameter("getObject").addParameter(this.getClass().getName()));
	}
	public void setObject(Object object) {
		throw new ODBRuntimeException(NeoDatisError.METHOD_SHOULD_NOT_BE_CALLED.addParameter("setObject").addParameter(this.getClass().getName()));
	}
	public NonNativeObjectInfo getNnoi() {
		return nnoi;
	}
	public AbstractObjectInfo createCopy(Map<OID,AbstractObjectInfo> cache, boolean onlyData) {
		return new ObjectReference((NonNativeObjectInfo) nnoi.createCopy(cache, onlyData));
	}
    
}
