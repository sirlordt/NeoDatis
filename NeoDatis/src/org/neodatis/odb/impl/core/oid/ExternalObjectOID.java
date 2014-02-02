package org.neodatis.odb.impl.core.oid;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.ExternalOID;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OIDTypes;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.tool.wrappers.OdbString;

public class ExternalObjectOID extends OdbObjectOID implements ExternalOID{
	private DatabaseId databaseId;
	public ExternalObjectOID(OID oid, DatabaseId databaseId) {
		super(oid.getObjectId());
		this.databaseId = databaseId;
	}
	public DatabaseId getDatabaseId() {
		return databaseId;
	}
	
	public String oidToString() {
		StringBuffer buffer = new StringBuffer(OIDTypes.TYPE_NAME_EXTERNAL_OBJECT_OID).append(":");
		buffer.append(databaseId.toString()).append(":").append(oid);
		return buffer.toString();
	}

	public static ExternalObjectOID oidFromString(String oidString){
		String [] tokens = OdbString.split(oidString,":");
		if(tokens.length!=3 ||!(tokens[0].equals(OIDTypes.TYPE_NAME_EXTERNAL_OBJECT_OID))){
			throw new ODBRuntimeException(NeoDatisError.INVALID_OID_REPRESENTATION.addParameter(oidString));
		}
		long oid = Long.parseLong(tokens[2]);
		String databaseid = tokens[1];
		return new ExternalObjectOID(new OdbClassOID(oid), DatabaseIdImpl.fromString(databaseid));
	}
	public int getType() {
		return OIDTypes.TYPE_EXTERNAL_OBJECT_OID;
	}
	
}
