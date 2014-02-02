package org.neodatis.odb.impl.core.oid;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OIDTypes;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.tool.wrappers.OdbString;

public class OdbClassOID implements OID {
	protected long oid;

	public OdbClassOID(long oid) {
		this.oid = oid;
	}

	public String toString() {
		return String.valueOf(oid);
	}
	
	public String oidToString() {
		StringBuffer buffer = new StringBuffer(OIDTypes.TYPE_NAME_CLASS_OID).append(":").append(String.valueOf(oid));
		return buffer.toString();
	}

	public static OdbClassOID oidFromString(String oidString){
		String [] tokens = OdbString.split(oidString,":");
		if(tokens.length!=2 ||!(tokens[0].equals(OIDTypes.TYPE_NAME_CLASS_OID))){
			throw new ODBRuntimeException(NeoDatisError.INVALID_OID_REPRESENTATION.addParameter(oidString));
		}
		long oid = Long.parseLong(tokens[1]);
		return new OdbClassOID(oid);
	}
	public long getObjectId() {
		return oid;
	}

	public int compareTo(Object object) {
		if (object == null || !(object instanceof OdbClassOID)) {
			return -1000;
		}
		OID otherOid = (OID) object;
		return (int) (oid - otherOid.getObjectId());
	}

	public boolean equals(Object object) {
		boolean b = this == object || this.compareTo(object) == 0;
		return b;
	}

	public int hashCode() {
		// Copy of the Long hashcode algorithm
		return (int) (oid ^ (oid >>> 32));
	}

	public long getClassId() {
		return 0;
	}

	public int getType() {
		return ODBType.CLASS_OID_ID;
	}

}
