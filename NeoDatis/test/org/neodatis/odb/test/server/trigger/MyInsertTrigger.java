package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;

public class MyInsertTrigger extends ServerInsertTrigger {
	private int nbInsertsAfter;
	private int nbInsertsBefore;

	public void afterInsert(final ObjectRepresentation objectRepresentation, final OID oid) {
		System.out.println("New object has been inserted with oid " + oid);
		nbInsertsAfter++;
	}

	public boolean beforeInsert(final ObjectRepresentation objectRepresentation) {
		System.out.println("New object is going to be inserted " + objectRepresentation);
		nbInsertsBefore++;
		return false;
	}

	public int getNbInsertsAfter() {
		return nbInsertsAfter;
	}

	public int getNbInsertsBefore() {
		return nbInsertsBefore;
	}
}
