/**
 * 
 */
package org.neodatis.odb.test.jdk15.generics;

import java.util.Iterator;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestGenerics extends ODBTest {

	public void testGetObects() {
		String baseName = getBaseName();

		ODB odb = open(baseName);
		odb.store(new Function("Test"));
		Objects<Function> functions = odb.getObjects(Function.class);
		Function f = functions.getFirst();
		odb.close();
		assertEquals(1, functions.size());
	}

	public void testGetObects2() {
		String baseName = getBaseName();

		ODB odb = open(baseName);
		odb.store(new Function("Test"));
		Objects<Function> functions = odb.getObjects(Function.class);
		Function f = functions.next();
		odb.close();
		assertEquals(1, functions.size());
	}

	public void testGetObects3() {
		String baseName = getBaseName();

		ODB odb = open(baseName);
		odb.store(new Function("Test"));
		Objects<Function> functions = odb.getObjects(Function.class);
		Iterator<Function> iterator = functions.iterator();
		Function f = iterator.next();
		odb.close();
		assertEquals(1, functions.size());
	}

}
