package org.neodatis.odb.impl.core.layers.layer3.engine;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;

public class PendingReading {
	private int id;
	private ClassInfo ci;
	private OID attributeOID;
	public PendingReading(int id, ClassInfo ci, OID attributeOID) {
		super();
		this.id = id;
		this.ci = ci;
		this.attributeOID = attributeOID;
	}
	
	public int getId() {
		return id;
	}

	public ClassInfo getCi() {
		return ci;
	}
	public OID getAttributeOID() {
		return attributeOID;
	}
	

}
