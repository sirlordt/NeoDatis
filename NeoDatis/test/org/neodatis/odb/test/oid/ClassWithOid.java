package org.neodatis.odb.test.oid;

import org.neodatis.odb.OID;

public class ClassWithOid {
	private String name;
	private OID oid;

	public ClassWithOid(String name, OID oid) {
		super();
		this.name = name;
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OID getOid() {
		return oid;
	}

	public void setOid(OID oid) {
		this.oid = oid;
	}

}
