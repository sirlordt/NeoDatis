package org.neodatis.odb.test.cache;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

public class TestHashCode extends ODBTest {

	/** a problem reported by glsender - 1875544 */
	public void test1() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		MyObjectWithMyHashCode my = null;

		// creates 1000 objects
		for (int i = 0; i < 1000; i++) {
			my = new MyObjectWithMyHashCode(new Long(1000));
			odb.store(my);
		}
		odb.close();

		odb = open(baseName);
		Objects objects = odb.getObjects(MyObjectWithMyHashCode.class);
		assertEquals(1000, objects.size());

		while (objects.hasNext()) {
			my = (MyObjectWithMyHashCode) objects.next();
			odb.delete(my);
		}
		odb.close();
		odb = open(baseName);
		objects = odb.getObjects(MyObjectWithMyHashCode.class);
		odb.close();
		IOUtil.deleteFile(baseName);
		assertEquals(0, objects.size());

	}

	/** a problem reported by glsender */
	public void test2() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		MyObjectWithMyHashCode2 my = null;

		// creates 1000 objects
		for (int i = 0; i < 1000; i++) {
			my = new MyObjectWithMyHashCode2(new Long(1000));
			odb.store(my);
		}
		odb.close();

		odb = open(baseName);
		Objects objects = odb.getObjects(MyObjectWithMyHashCode2.class);
		assertEquals(1000, objects.size());

		while (objects.hasNext()) {
			my = (MyObjectWithMyHashCode2) objects.next();
			odb.delete(my);
		}
		odb.close();

		odb = open(baseName);
		objects = odb.getObjects(MyObjectWithMyHashCode2.class);
		odb.close();
		IOUtil.deleteFile(baseName);
		assertEquals(0, objects.size());

	}
}
