/**
 * 
 */
package org.neodatis.odb.test.ee.server.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.test.vo.login.User;

/**
 * @author olivier
 *
 */
public class LocalInsertTrigger extends InsertTrigger {

	public String profileName;
	
	public void afterInsert(Object object, OID oid) {
		// TODO Auto-generated method stub

	}

	public boolean beforeInsert(Object object) {
		System.out.println(object);
		User user = (User) object;
		profileName = user.getProfile().getName();
		return false;
	}

}
