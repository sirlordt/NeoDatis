package org.neodatis.odb.test.server.trigger.oid;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.trigger.OIDTrigger;

public class MyOidTrigger2 extends OIDTrigger {

	public void setOid(ObjectRepresentation o, OID oid) {
		o.setValueOf("b", buildNnoi(oid.oidToString()));
	}

	/**
	 * @param oidToString
	 * @return
	 */
	private Object buildNnoi(String oidToString) {
		return null;
	}


}
