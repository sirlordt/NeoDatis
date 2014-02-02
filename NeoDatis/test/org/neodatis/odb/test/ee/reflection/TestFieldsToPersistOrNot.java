/**
 * 
 */
package org.neodatis.odb.test.ee.reflection;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;



/**
 * @author olivier
 *
 */
public class TestFieldsToPersistOrNot extends ODBTest{
	public void testNotPersistField() throws Exception{
		String baseName = getBaseName();
		
		ODB odb = open(baseName);
		odb.getClassRepresentation(Function.class.getName(),false).doNotPersistAttribute("name");
		odb.store(new Function("f"));
		odb.close();
		
		odb = open(baseName);
		Objects<Function> functions = odb.getObjects(Function.class);
		
		Function f = functions.getFirst();
		odb.close();
		assertNull(f.getName());
		
	}
	

}
