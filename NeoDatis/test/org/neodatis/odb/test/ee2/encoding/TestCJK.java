/**
 * 
 */
package org.neodatis.odb.test.ee2.encoding;

import java.io.UnsupportedEncodingException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestCJK extends ODBTest {

	public void testCjk() throws UnsupportedEncodingException {
		String charSet = OdbConfiguration.getDatabaseCharacterEncoding();

		try {
			String baseName = "getBaseName()";
			
			OdbConfiguration.setDatabaseCharacterEncoding("UTF-8");
			ODB odb = open(baseName);

			// Store the object
			Function f1 = new Function("浜崎あゆみ ");
			Function f2 = new Function("滨崎步");
			odb.store(f1);
			odb.store(f2);
			odb.commit();

			Objects<Function> datas = odb.getObjects(Function.class);
			odb.close();
			Function ff1 = datas.next();
			Function ff2 = datas.next();

			println(ff1);
			println(ff2);

			assertEquals(f1.getName(), ff1.getName());
			assertEquals(f2.getName(), ff2.getName());
		} finally {
			OdbConfiguration.setDatabaseCharacterEncoding(charSet);
		}
	}

}
