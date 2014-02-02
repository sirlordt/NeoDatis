/**
 * 
 */
package org.neodatis.odb.test.ee.server.trigger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

/**
 * @author olivier
 *
 */
public class TestInsertTrigger extends ODBTest {
	
	public void testInsertTriggerWithUserAndProfileSameVm(){
		String baseName = getBaseName();
		int port = 10001;
		
		MyServerInsertTrigger insertTrigger = new MyServerInsertTrigger();
		
		ODBServer server = ODBFactory.openServer(port);
		server.addBase(baseName, baseName);
		server.addInsertTrigger(baseName, User.class.getName(), insertTrigger);
		ODB odb = server.openClient(baseName);
		
		User user = new User("user name", "user email", new Profile("profile name", new Function("function name")));
		
		odb.store(user);
		
		odb.close();
		
		assertEquals("profile name", insertTrigger.profileName);
		
		
		
	}
	
	public void testInsertTriggerWithUserNameSameVm(){
		String baseName = getBaseName();
		int port = PORT+10;
		
		MyServerInsertTriggerToGetUserName insertTrigger = new MyServerInsertTriggerToGetUserName();
		
		ODBServer server = ODBFactory.openServer(port);
		server.addBase(baseName, baseName);
		server.addInsertTrigger(baseName, User.class.getName(), insertTrigger);
		ODB odb = server.openClient(baseName);
		
		User user = new User("user name", "user email", new Profile("profile name", new Function("function name")));
		
		odb.store(user);
		
		odb.close();
		
		assertEquals("user name", insertTrigger.userName);
		
		
		
	}

	
	public void testInsertTriggerWithUserAndProfileLocal(){
		String baseName = getBaseName();
		
		ODB odb = ODBFactory.open(baseName);
		LocalInsertTrigger insertTrigger = new LocalInsertTrigger();
		odb.addInsertTrigger(User.class, insertTrigger);
		User user = new User("user name", "user email", new Profile("profile name", new Function("function name")));
		
		odb.store(user);
		odb.close();
		
		assertEquals("profile name", insertTrigger.profileName);
		
		
		
	}

}
