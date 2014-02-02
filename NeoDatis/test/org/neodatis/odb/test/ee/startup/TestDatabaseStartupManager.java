/**
 * 
 */
package org.neodatis.odb.test.ee.startup;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestDatabaseStartupManager extends ODBTest {
	public void test1(){
		try{
			MyDatabaseStartupManager manager = new MyDatabaseStartupManager();
			OdbConfiguration.registerDatabaseStartupManager(manager);
			String baseName = getBaseName();
			ODB odb = open(baseName);
			odb.close();
			
			assertTrue(manager.called);
			
		}finally{
			OdbConfiguration.removeDatabaseStartupManager();
		}
	}

}
