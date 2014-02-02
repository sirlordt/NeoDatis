package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerDeleteTrigger;

public class MyDeleteTrigger extends ServerDeleteTrigger {
	private int nbDeletesAfter;
	private int nbDeletesBefore;

	public int getNbDeletesAfter() {
		return nbDeletesAfter;
	}

	public int getNbDeletesBefore() {
		return nbDeletesBefore;
	}

	public void afterUpdate(ObjectRepresentation oldObject, ObjectRepresentation newObject, OID oid) {
		System.out.println("New object with oid " + oid + " has been updated");
		nbDeletesAfter++;

	}

	public boolean beforeUpdate(ObjectRepresentation oldObjectRepresentation, ObjectRepresentation newObjectRepresentation, OID oid) {
		System.out.println("New object is going to be updated " + newObjectRepresentation);
		nbDeletesBefore++;
		return false;
	}

	public void afterDelete(ObjectRepresentation objectRepresentation, OID oid) {
		System.out.println("object with oid " + oid + " has been deleted");
		nbDeletesAfter++;

	}

	public boolean beforeDelete(ObjectRepresentation objectRepresentation, OID oid) {
		System.out.println("object is going to be deleted " + oid);
		nbDeletesBefore++;
		return false;
	}

}
