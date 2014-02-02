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
public class MyServerInsertTriggerToGetUserName extends ServerInsertTrigger {

	public String userName;
	
	
	public void afterInsert(ObjectRepresentation objectRepresentation, OID oid) {

	}

	public boolean beforeInsert(ObjectRepresentation objectRepresentation) {
		System.out.println(objectRepresentation.getValueOf("name"));
		userName = (String) objectRepresentation.getValueOf("name"); 
		return false;
	}

}
