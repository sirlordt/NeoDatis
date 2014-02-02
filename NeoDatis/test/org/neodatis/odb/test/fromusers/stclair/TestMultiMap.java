/**
 * 
 */
package org.neodatis.odb.test.fromusers.stclair;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

/**
 * To test apache collecitons multi key map
 * 
 * @author olivier
 * 
 */
public class TestMultiMap extends ODBTest {
	public void test1() {

		String baseName = getBaseName();
		ODB odb = null;

		try {
			MyClass mc = new MyClass("multi");
			MultiKey mk = new MultiKey("1", "One");
			mc.getMap().put(mk, new Integer(1));

			mk = new MultiKey("2", "Two");
			mc.getMap().put(mk, new Integer(2));
			odb = open(baseName);
			OID oid = odb.store(mc);
			odb.commit();

			Objects<MyClass> objects = odb.getObjects(MyClass.class);
			assertEquals(1, objects.size());

			MyClass mc2 = (MyClass) odb.getObjectFromId(oid);
			println(mc2);
			assertEquals(MultiKeyMap.class, objects.getFirst().getMap().getClass());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test2() {

		String baseName = getBaseName();
		ODB odb = null;

		try {
			MyClass mc = new MyClass("multi");
			MultiKey mk = new MultiKey("1", "One");
			mc.getMap().put(mk, new Integer(1));

			mk = new MultiKey("2", "Two");
			mc.getMap().put(mk, new Integer(2));
			odb = open(baseName);
			odb.store(mc);
			odb.close();

			odb = open(baseName);
			Objects<MyClass> objects = odb.getObjects(MyClass.class);
			assertEquals(1, objects.size());
			assertEquals(MultiKeyMap.class, objects.getFirst().getMap().getClass());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

}
