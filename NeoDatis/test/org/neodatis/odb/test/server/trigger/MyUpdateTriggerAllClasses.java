package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerUpdateTrigger;

public class MyUpdateTriggerAllClasses extends ServerUpdateTrigger {
	private int nbUpdatesAfter;
	private int nbUpdatesBefore;

	public int getNbUpdatesAfter() {
		return nbUpdatesAfter;
	}

	public int getNbUpdatesBefore() {
		return nbUpdatesBefore;
	}

	public void afterUpdate(ObjectRepresentation oldObjectRepresentation, ObjectRepresentation newObjectRepresentation, OID oid) {
		System.out.println("allclasses : New object with oid " + oid + "has been updated");
		nbUpdatesAfter++;

	}

	public boolean beforeUpdate(ObjectRepresentation oldObjectRepresentation, ObjectRepresentation newObjectRepresentation, OID oid) {
		System.out.println("allclasses : New object is going to be updated " + newObjectRepresentation);
		nbUpdatesBefore++;
		return false;
	}

}
