package org.neodatis.odb.impl.core.transaction;

import org.neodatis.odb.OID;

public class ObjectInsertingInfo {
	 public OID oid;

	    public int level;

	    public ObjectInsertingInfo(OID oid, int level) {
	        this.oid = oid;
	        this.level = level;
	    }
}
