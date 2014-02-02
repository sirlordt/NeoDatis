package org.neodatis.odb.test.server.trigger.oid;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.trigger.OIDTrigger;

public class MyOidTrigger extends OIDTrigger {

	public void setOid(ObjectRepresentation o, OID oid) {
		o.setValueOf("id", oid.oidToString());
	}


}
