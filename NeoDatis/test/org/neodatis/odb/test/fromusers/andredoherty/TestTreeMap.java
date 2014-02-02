/**
 * 
 */
package org.neodatis.odb.test.fromusers.andredoherty;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier Test TreeMap storage . Bugs detected by Andre Doherty
 * 
 */
public class TestTreeMap extends ODBTest {

	public void test1() {
		ODB odb = null;
		String baseName = getBaseName();

		try {
			MyObject o = new MyObject();
			odb = open(baseName);
			odb.store(o);
			odb.close();

			odb = open(baseName);

			Objects<MyObject> objects = odb.getObjects(MyObject.class);
			assertEquals(1, objects.size());

		} finally {
			odb.close();
		}
	}

}
