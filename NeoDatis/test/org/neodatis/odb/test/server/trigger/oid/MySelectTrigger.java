/**
 * 
 */
package org.neodatis.odb.test.server.trigger.oid;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerSelectTrigger;

/**
 * @author olivier
 *
 */
public class MySelectTrigger extends ServerSelectTrigger {

	

	public void afterSelect(ObjectRepresentation objectRepresentation, OID oid) {
		objectRepresentation.setValueOf("id", oid.oidToString());	
	}

}
