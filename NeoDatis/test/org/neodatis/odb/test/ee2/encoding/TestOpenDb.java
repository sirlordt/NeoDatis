/**
 * 
 */
package org.neodatis.odb.test.ee2.encoding;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestOpenDb extends ODBTest {
	private static class Data {
		String text = "test";
	}

	private void initDB() {
		ODB db = ODBFactory.open("test.db");
		for (int i = 0; i < 5; ++i)
			db.store(new Data());
		db.close();
	}

	public void test1() {
		initDB();
		int i = 0;
		String baseName = getBaseName();
		while (i<1000) {
			ODB db = open(baseName);
			try {
				for (final Data d : db.getObjects(Data.class).toArray(new Data[0])) {
					db.store(d);
					db.commit();
				}
			} finally {
				db.close();
			}
			i++;
			println(i);
		}
	}
}
