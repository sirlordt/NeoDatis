/**
 * 
 */
package org.neodatis.odb.test.ee.query.withnumbers;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;

/**
 * @author olivier
 *
 */
public class TestQueryWithDoubles extends ODBTest {
	public void test1(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.40001));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.40002));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.lt("double1", 1.40002)));
		odb.close();
		assertEquals(1, tcs.size());
		TestClass tc = tcs.getFirst();
		assertEquals(tc1.getDouble1(), tc.getDouble1());
		

		
	}
	public void test2(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.40001));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.40002));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.gt("double1", 1.40001)));
		odb.close();
		assertEquals(1, tcs.size());
		TestClass tc = tcs.getFirst();
		assertEquals(tc2.getDouble1(), tc.getDouble1());
	}
	public void test3(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.40001));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.40002));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.gt("double1", 1.4000001)));
		odb.close();
		assertEquals(2, tcs.size());
	}
	public void test4(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.40001));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.40002));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.lt("double1", 1.401)));
		odb.close();
		assertEquals(2, tcs.size());
	}
	public void test5(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.40001));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.40002));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.equal("double1", 1.40001)));
		odb.close();
		assertEquals(1, tcs.size());
	}
	public void test6(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.49999999999));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.499999999995));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.and().add(Where.lt("double1", 1.499999999996)).add(Where.gt("double1", 1.499999999994))));
		odb.close();
		assertEquals(1, tcs.size());
		TestClass tc = tcs.getFirst();
		assertEquals(tc2.getDouble1(), tc.getDouble1());
	}
	public void test7(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.49999999999123456789));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.499999999995));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.equal("double1", 1.49999999999123456789)));
		odb.close();
		assertEquals(1, tcs.size());
		TestClass tc = tcs.getFirst();
		assertEquals(tc1.getDouble1(), tc.getDouble1());
	}
	public void test8(){
		String baseName = getBaseName();
		
		TestClass tc1 = new TestClass();
		tc1.setDouble1(new Double(1.49999999999123456789));

		TestClass tc2 = new TestClass();
		tc2.setDouble1(new Double(1.499999999995));
		
		ODB odb = open(baseName);
		odb.store(tc1);
		odb.store(tc2);
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class, Where.not(Where.equal("double1", 2.49999999999123456789))));
		odb.close();
		assertEquals(2, tcs.size());
	}

}
