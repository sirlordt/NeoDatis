package org.neodatis.odb.impl.core.layers.layer2.meta.history;

import org.neodatis.odb.OID;

public class InsertHistoryInfo implements IHistoryInfo{
	private String type;
	private long position;
	private OID oid;
	private OID next;
	private OID prev;
	public InsertHistoryInfo(String type, OID oid, long position, OID prev, OID next) {
		super();
		this.type = type;
		this.position = position;
		this.oid = oid;
		this.next = next;
		this.prev = prev;
	}
	public OID getNext() {
		return next;
	}
	public long getPosition() {
		return position;
	}
	public OID getPrev() {
		return prev;
	}
	public String getType() {
		return type;
	}
	
	public OID getOid() {
		return oid;
	}
	public String toString() {
		return type + " - oid="+oid+ " - pos="+position+" - prev="+prev+" - next="+next;
	}	
}
