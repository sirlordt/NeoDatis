package org.neodatis.odb.impl.core.transaction;

import org.neodatis.odb.OID;

public class IdInfo {
	public OID oid;

	public long position;

	public byte status;

	public IdInfo(OID oid, long position, byte status) {
		super();
		this.oid = oid;
		this.position = position;
		this.status = status;
	}
}
