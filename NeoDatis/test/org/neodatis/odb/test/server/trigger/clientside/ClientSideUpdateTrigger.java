/**
 * 
 */
package org.neodatis.odb.test.server.trigger.clientside;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.trigger.UpdateTrigger;

/**
 * @author olivier
 *
 */
public class ClientSideUpdateTrigger extends UpdateTrigger {
	private int nbUpdatesAfter;
	private int nbUpdatesBefore;

	public int getNbUpdatesAfter() {
		return nbUpdatesAfter;
	}

	public int getNbUpdatesBefore() {
		return nbUpdatesBefore;
	}


	public void afterUpdate(ObjectRepresentation oldObjectRepresentation, Object newObject, OID oid) {
		System.out.println("After Update for oid "+oid);
		nbUpdatesAfter++;

	}

	public boolean beforeUpdate(ObjectRepresentation oldObjectRepresentation, Object newObject, OID oid) {
		System.out.println("Before Update for oid "+oid);
		nbUpdatesBefore++;
		return true;
	}

}
