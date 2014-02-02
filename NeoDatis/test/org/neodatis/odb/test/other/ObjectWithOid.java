package org.neodatis.odb.test.other;

public class ObjectWithOid {
	private String oid;
	private String name;

	public ObjectWithOid(String oid, String name) {
		super();
		this.oid = oid;
		this.name = name;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
