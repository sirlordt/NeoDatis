/**
 * 
 */
package org.neodatis.odb.test.multithread;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class Test2Open extends ODBTest {
	public void test2() throws Exception {
		deleteBase("open2");

		ODB odb1 = open("open2");
		try {
			ODB odb2 = open("open2");
			fail("The file should be locked");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().indexOf("file is locked by the current Virtual machine") != -1);
		}
		odb1.close();

	}
}
