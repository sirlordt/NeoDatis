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
package org.neodatis.odb.impl.core.oid;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OIDTypes;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.tool.wrappers.OdbString;

public class OdbObjectOID implements OID {
	protected long oid;
	
	public OdbObjectOID(long oid){
		this.oid = oid;
	}
	

	public String toString() {
		return String.valueOf(oid);
	}
	
	public String oidToString() {
		StringBuffer buffer = new StringBuffer(OIDTypes.TYPE_NAME_OBJECT_OID).append(":").append(String.valueOf(oid));
		return buffer.toString();
	}

	public static OdbObjectOID oidFromString(String oidString){
		String [] tokens = OdbString.split(oidString,":");
		
		if(tokens.length!=2 ||!(tokens[0].equals(OIDTypes.TYPE_NAME_OBJECT_OID))){
			throw new ODBRuntimeException(NeoDatisError.INVALID_OID_REPRESENTATION.addParameter(oidString));
		}
		long oid = Long.parseLong(tokens[1]);
		return new OdbObjectOID(oid);
	}
	
	public long getObjectId() {
		return oid;
	}
	public int compareTo(Object object) {
		if(object==null || !(object instanceof OdbObjectOID) ){
			return -1000;
		}
		OID otherOid = (OID) object;
		return (int) (oid - otherOid.getObjectId());
	}
	public boolean equals(Object object){
		boolean b = this==object || this.compareTo(object) == 0;
		return b;
	}
	
	public int hashCode() {
		//Copy of the Long hashcode algorithm  
		return (int)(oid ^ (oid >>> 32));
	}

	public long getClassId() {
		return 0;
	}

	public int getType() {
		return OIDTypes.TYPE_OBJECT_OID;
	}
	
}
