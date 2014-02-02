/**
 * 
 */
package org.neodatis.odb.test.ee.list;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.ComposedCompareKey;
import org.neodatis.odb.core.query.SimpleCompareKey;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.list.objects.InMemoryBTreeCollection;
import org.neodatis.odb.impl.core.query.list.objects.SimpleList;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * @author olivier
 * 
 */
public class TestList extends ODBTest {
	public void test1() {
		Objects<Function> functions = new SimpleList<Function>();

		functions.add(new Function("F1"));
		functions.add(new Function("F2"));
		functions.add(new Function("F3"));

		Iterator<Function> iterator = functions.iterator();
		try {
			while (iterator.hasNext()) {
				functions.remove(iterator.next());
			}
			fail("It should have thrown a concurrentModificationException");
		} catch (ConcurrentModificationException e) {
		}

	}

	public void test3() {
		Objects<Function> functions = new InMemoryBTreeCollection<Function>();

		functions.addWithKey(new SimpleCompareKey("F1"), new Function("F1"));
		functions.addWithKey(new SimpleCompareKey("F2"), new Function("F2"));
		functions.addWithKey(new SimpleCompareKey("F3"), new Function("F3"));

		Iterator<Function> iterator = functions.iterator();
		try {
			while (iterator.hasNext()) {
				functions.removeByKey(new SimpleCompareKey("F1"), iterator.next());
			}
			// fail("It should have thrown a concurrentModificationException");
		} catch (ConcurrentModificationException e) {
		}

	}

	public void test2() {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new Function("F1"));
		odb.store(new Function("F2"));
		odb.store(new Function("F3"));
		odb.close();

		odb = open(baseName);
		Objects<Function> functions = odb.getObjects(Function.class);
		Iterator<Function> iterator = functions.iterator();
		try {
			while (iterator.hasNext()) {
				functions.remove(iterator.next());
			}
			fail("It should have thrown a concurrentModificationException");
		} catch (ConcurrentModificationException e) {
			println("exception");
		}

	}

	public void test4() {
		if (!isLocal) {
			return;
		}

		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new Function("F1"));
		odb.store(new Function("F2"));
		odb.store(new Function("F3"));
		odb.close();

		odb = open(baseName);
		Objects<Function> functions = odb.getObjects(new CriteriaQuery(Function.class).orderByAsc("name"));
		Iterator<Function> iterator = functions.iterator();
		try {
			while (iterator.hasNext()) {
				Function f = iterator.next();
				functions.removeByKey(new SimpleCompareKey(f.getName()), f);
				println(f);
			}
			fail("It should have thrown a concurrentModificationException");
		} catch (ConcurrentModificationException e) {
			println("exception");
		}

	}

	public void test5() {
		if (!isLocal) {
			return;
		}

		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new Function("F1"));
		odb.store(new Function("F2"));
		odb.store(new Function("F3"));
		odb.close();

		odb = open(baseName);
		Objects<Function> functions = odb.getObjects(new CriteriaQuery(Function.class).orderByDesc("name"));
		Iterator<Function> iterator = functions.iterator();
		try {
			while (iterator.hasNext()) {
				Function f = iterator.next();
				functions.removeByKey(new SimpleCompareKey("F2"), f);
				println(f);
			}
			fail("It should have thrown a concurrentModificationException");
		} catch (ConcurrentModificationException e) {
			println("exception");
		}

	}

	public void test6(){
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		ODB odb = open(baseName);
		int size = 200;
		for(int i=0;i<size;i++){
			odb.store(new TestClass("string "+i, new BigDecimal(i), new Double(i), new Date()));
		}
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class).orderByAsc("string1,bigDecimal1,double1"));
		int len = tcs.size();
		TestClass tc0 = new TestClass("string 1000", new BigDecimal(1000), new Double(1001), null);
		TestClass tc1 = new TestClass("string 1001", new BigDecimal(1001), new Double(1002), null);
		// manually adds 2 objects
		tcs.addWithKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc0.getString1()), new SimpleCompareKey(tc0.getBigDecimal1()), new SimpleCompareKey(tc0.getDouble1()) }), tc0);
		tcs.addWithKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc1.getString1()), new SimpleCompareKey(tc1.getBigDecimal1()), new SimpleCompareKey(tc1.getDouble1()) }), tc1);
		
		int len2 = tcs.size();
		assertEquals(len+2, len2);
		TestClass tc = new TestClass("string 17", new BigDecimal(17), new Double(17), new Date());
		boolean b1 = tcs.removeByKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc.getString1()), new SimpleCompareKey(tc.getBigDecimal1()), new SimpleCompareKey(tc.getDouble1()) }), tc);
		// delete what was inserted manually
		boolean b2 = tcs.removeByKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc0.getString1()), new SimpleCompareKey(tc0.getBigDecimal1()), new SimpleCompareKey(tc0.getDouble1()) }), tc0);
		boolean b3 = tcs.removeByKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc1.getString1()), new SimpleCompareKey(tc1.getBigDecimal1()), new SimpleCompareKey(tc1.getDouble1()) }), tc1);

		assertEquals(len2-3, tcs.size());
		assertEquals(true,b1);
		assertEquals(true,b2);
		assertEquals(true,b3);

		// does not exist in colelction
		tc = new TestClass("string 17", new BigDecimal(17), new Double(18), new Date());
		b1 = tcs.removeByKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc.getString1()), new SimpleCompareKey(tc.getBigDecimal1()), new SimpleCompareKey(tc.getDouble1()) }), tc);
		assertEquals(len2-3, tcs.size());
		assertEquals(false,b1);

	}
	public void test7(){
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		ODB odb = open(baseName);
		int size = 20;
		for(int i=0;i<size;i++){
			odb.store(new TestClass("string "+i, new BigDecimal(i), new Double(i), new Date()));
			odb.store(new TestClass("string "+i, new BigDecimal(i), new Double(i), new Date()));
			odb.store(new TestClass("string "+i, new BigDecimal(i), new Double(i), new Date()));
		}
		odb.close();
		
		odb = open(baseName);
		Objects<TestClass> tcs = odb.getObjects(new CriteriaQuery(TestClass.class).orderByAsc("string1,bigDecimal1,double1"));
		int len = tcs.size();
		TestClass tc = new TestClass("string 17", new BigDecimal(17), new Double(17), new Date());
		boolean b = tcs.removeByKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc.getString1()), new SimpleCompareKey(tc.getBigDecimal1()), new SimpleCompareKey(tc.getDouble1()) }), tc);

		assertEquals(len-1, tcs.size());
		assertEquals(true,b);

		// does not exist in colelction
		tc = new TestClass("string 17", new BigDecimal(17), new Double(18), new Date());
		b = tcs.removeByKey(new ComposedCompareKey(new OdbComparable[]{new SimpleCompareKey(tc.getString1()), new SimpleCompareKey(tc.getBigDecimal1()), new SimpleCompareKey(tc.getDouble1()) }), tc);
		assertEquals(len-1, tcs.size());
		assertEquals(false,b);

	}

}
