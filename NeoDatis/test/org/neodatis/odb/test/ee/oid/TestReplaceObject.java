/**
 * 
 */
package org.neodatis.odb.test.ee.oid;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 *
 */
public class TestReplaceObject extends ODBTest {
	public void test1(){
		String baseName = getBaseName();
		
		Function function = new Function("f1");
		ODB odb = open(baseName);
		OID oid = odb.store(function);
		odb.close();
		
		odb = open(baseName);
		odb.ext().replace(oid, new Function("new function 1"));
		odb.close();
		
		odb = open(baseName);
		Objects<Function> functions = odb.getObjects(Function.class);
		Function newFunction = functions.getFirst();
		odb.close();
		
		assertEquals(1, functions.size());
		assertEquals("new function 1", newFunction.getName());
		println(functions);
	}
}
