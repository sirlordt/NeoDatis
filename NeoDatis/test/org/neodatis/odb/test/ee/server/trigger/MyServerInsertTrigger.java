/**
 * 
 */
package org.neodatis.odb.test.ee.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;

/**
 * @author olivier
 *
 */
public class MyServerInsertTrigger extends ServerInsertTrigger {

	public String profileName;
	
	
	public void afterInsert(ObjectRepresentation objectRepresentation, OID oid) {

	}


	public boolean beforeInsert(ObjectRepresentation objectRepresentation) {
		System.out.println(objectRepresentation.getValueOf("profile"));
		ObjectRepresentation orep = (ObjectRepresentation) objectRepresentation.getValueOf("profile");
		profileName = (String) orep.getValueOf("name"); 
		return false;
	}

}
