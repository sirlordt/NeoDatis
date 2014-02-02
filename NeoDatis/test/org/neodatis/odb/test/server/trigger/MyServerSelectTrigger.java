package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerSelectTrigger;

public class MyServerSelectTrigger extends ServerSelectTrigger {
	public int nbCalls;

	@Override
	public void afterSelect(ObjectRepresentation objectRepresentation, OID oid) {
		System.out.println("Select trigger called on " + oid);
		nbCalls++;

	}

}
