package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;
import org.neodatis.odb.impl.core.oid.ExternalObjectOID;

public class ReplicationInsertTrigger extends ServerInsertTrigger {
	private int nbInsertsAfter;
	private int nbInsertsBefore;

	public ReplicationInsertTrigger() {
	}

	public void afterInsert(final ObjectRepresentation objectRepresentation, final OID oid) {
		System.out.println("New object has been inserted with oid " + oid);
		System.out.println("Internal OID = " + oid);
		System.out.println("External OID = " + new ExternalObjectOID(oid, getOdb().ext().getDatabaseId()));
		nbInsertsAfter++;
	}

	public boolean beforeInsert(final ObjectRepresentation objectRepresentation) {
		return false;
	}

	public int getNbInsertsAfter() {
		return nbInsertsAfter;
	}

	public int getNbInsertsBefore() {
		return nbInsertsBefore;
	}
}
