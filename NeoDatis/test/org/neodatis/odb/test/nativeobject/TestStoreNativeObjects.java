/**
 * 
 */
package org.neodatis.odb.test.nativeobject;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

/**
 * To test storing native objects. This is not implemented yet
 * 
 * @author olivier
 * 
 */
public class TestStoreNativeObjects extends ODBTest {
	public void test1() {
		if (!testNewFeature) {
			return;
		}

		String baseName = getBaseName();

		ODB odb = open(baseName);
		odb.store(new MyMap<String, String>());
		odb.close();
	}
}
