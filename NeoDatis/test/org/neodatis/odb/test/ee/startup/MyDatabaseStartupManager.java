/**
 * 
 */
package org.neodatis.odb.test.ee.startup;

import org.neodatis.odb.DatabaseStartupManager;
import org.neodatis.odb.ODB;

/**
 * @author olivier
 *
 */
public class MyDatabaseStartupManager implements DatabaseStartupManager {
	public boolean called;
	public void start(ODB odb) {
		called = true;
		System.out.println("Oh nice, " + odb.getName() + " have been started");

	}

}
