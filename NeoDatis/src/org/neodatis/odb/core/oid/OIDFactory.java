package org.neodatis.odb.core.oid;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OIDTypes;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.impl.core.oid.ExternalClassOID;
import org.neodatis.odb.impl.core.oid.ExternalObjectOID;
import org.neodatis.odb.impl.core.oid.OdbClassOID;
import org.neodatis.odb.impl.core.oid.OdbObjectOID;
import org.neodatis.tool.wrappers.OdbString;

public class OIDFactory {
	
	public static OID buildObjectOID(long oid){
		return OdbConfiguration.getCoreProvider().getObjectOID(oid, 0); 
	}

	public static OID buildClassOID(long oid) {
		return OdbConfiguration.getCoreProvider().getClassOID(oid); 
	}
	
	public static OID oidFromString(String oidString){
		String [] tokens = OdbString.split(oidString,":");
		
		if(tokens[0].equals(OIDTypes.TYPE_NAME_OBJECT_OID)){
			return OdbObjectOID.oidFromString(oidString);
		}
		if(tokens[0].equals(OIDTypes.TYPE_NAME_CLASS_OID)){
			return OdbClassOID.oidFromString(oidString);
		}
		if(tokens[0].equals(OIDTypes.TYPE_NAME_EXTERNAL_OBJECT_OID)){
			return ExternalObjectOID.oidFromString(oidString);
		}
		if(tokens[0].equals(OIDTypes.TYPE_NAME_EXTERNAL_CLASS_OID)){
			return ExternalClassOID.oidFromString(oidString);
		}
		throw new ODBRuntimeException(NeoDatisError.INVALID_OID_REPRESENTATION.addParameter(oidString));
	}
}
