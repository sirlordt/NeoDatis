/**
 * 
 */
package org.neodatis.odb.test.query.criteria;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.SimpleCompareKey;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestQueryOrderBy extends ODBTest {

	public void test1() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		odb.store(new Class1("c1"));
		odb.store(new Class1("c1"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c3"));
		odb.store(new Class1("c4"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		q.orderByAsc("name");
		Objects objects = odb.getObjects(q);
		assertEquals(6, objects.size());
		while (objects.hasNext()) {
			System.out.println(objects.next());
		}

		// println(objects);

		odb.close();

	}
	
	public void test11() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		odb.store(new Class1("c1"));
		odb.store(new Class1("c1"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c3"));
		odb.store(new Class1("c4"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		q.orderByAsc("name");
		Objects objects = odb.getObjects(q,false);
		assertEquals(6, objects.size());
		while (objects.hasNext()) {
			System.out.println(objects.next());
		}

		// println(objects);

		odb.close();

	}

	public void test2() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		odb.store(new Class1("c1"));
		odb.store(new Class1("c1"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c3"));
		odb.store(new Class1("c4"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		// q.orderByAsc("name");
		Objects objects = odb.getObjects(q);
		assertEquals(6, objects.size());

		println(objects);

		odb.close();

	}

	public void test3() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		int size = 500;
		for (int i = 0; i < size; i++) {
			odb.store(new Class1("c1"));
		}
		for (int i = 0; i < size; i++) {
			odb.store(new Class1("c2"));
		}
		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		// q.orderByAsc("name");
		Objects objects = odb.getObjects(q);
		assertEquals(size * 2, objects.size());

		for (int i = 0; i < size; i++) {
			Class1 c1 = (Class1) objects.next();
			assertEquals("c1", c1.getName());
		}
		for (int i = 0; i < size; i++) {
			Class1 c1 = (Class1) objects.next();
			assertEquals("c2", c1.getName());
		}

		odb.close();

	}

	public void test4() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		int size = 5;
		for (int i = 0; i < size; i++) {
			odb.store(new Function("f" + (i + 1)));
		}
		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Function.class);
		// q.orderByAsc("name");
		Objects objects = odb.getObjects(q, true, 0, 2);
		List l = new ArrayList<Function>(objects);
		assertEquals(2, l.size());

		odb.close();

		odb = open(baseName);

		q = new CriteriaQuery(Function.class);
		q.orderByAsc("name");
		objects = odb.getObjects(q, true, 0, 2);
		l = new ArrayList<Function>(objects);
		assertEquals(2, l.size());

		odb.close();

		odb = open(baseName);
		q = new CriteriaQuery(Function.class);
		q.orderByDesc("name");
		objects = odb.getObjects(q, true, 0, 2);
		l = new ArrayList<Function>(objects);
		assertEquals(2, l.size());

		odb.close();

	}

	public void test51() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new Function("Not Null"));
		odb.store(new Function(null));
		odb.close();

		odb = open(baseName);
		IQuery q = new CriteriaQuery(Function.class, Where.isNotNull("name"));
		// q.orderByAsc("name");
		Objects objects = odb.getObjects(q, true, 0, 10);
		List l = new ArrayList<Function>(objects);
		odb.close();
		assertEquals(1, l.size());

	}

	public void test5() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		int size = 5;
		for (int i = 0; i < size; i++) {
			odb.store(new Function("f1"));
		}
		odb.store(new Function(null));
		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Function.class, Where.isNotNull("name"));
		// q.orderByAsc("name");
		Objects objects = odb.getObjects(q, true, 0, 10);
		List l = new ArrayList<Function>(objects);
		assertEquals(size, l.size());

		odb.close();

		odb = open(baseName);

		q = new CriteriaQuery(Function.class, Where.isNotNull("name"));
		q.orderByAsc("name");
		objects = odb.getObjects(q, true, 0, 10);
		l = new ArrayList<Function>(objects);
		assertEquals(5, l.size());

		odb.close();

		odb = open(baseName);

		q = new CriteriaQuery(Function.class, Where.isNotNull("name"));
		q.orderByDesc("name");
		objects = odb.getObjects(q, true, 0, 10);
		l = new ArrayList<Function>(objects);
		assertEquals(5, l.size());

		odb.close();

	}

	public void test6() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		int size = 5;
		for (int i = 0; i < size; i++) {
			odb.store(new Function("f1"));
		}
		odb.store(new Function(null));
		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Function.class, Where.isNull("name"));
		// q.orderByAsc("name");
		Objects objects = odb.getObjects(q, true, 0, 10);
		List l = new ArrayList<Function>(objects);
		assertEquals(1, l.size());

		odb.close();

		odb = open(baseName);

		q = new CriteriaQuery(Function.class, Where.isNull("name"));
		q.orderByAsc("name");
		try{
			objects = odb.getObjects(q, true, 0, 10);
			fail("It should have thrown an excpetion as one object has the index attribute set to null => we can't build the index key");
		}catch (Exception e) {
			// TODO: handle exception
		}

		odb.close();

		odb = open(baseName);

		q = new CriteriaQuery(Function.class, Where.isNull("name"));
		q.orderByDesc("name");
		objects = odb.getObjects(q, true, 0, 10);
		l = new ArrayList<Function>(objects);
		assertEquals(1, l.size());

		odb.close();

	}
	//TODO nee
	public void testOrderBy() {
		if(!isLocal || !testNewFeature){
			// this test currently only works on local mode. In CS mode , the btree collection is not ordered by the key but by an index (integer)
			// this causes a class cast exception on the btree collection when trying to manually add an object by its key
			return;
		}
		String baseName = getBaseName();

		ODB odb = open(baseName);

		odb.store(new Class1("c1"));
		odb.store(new Class1("c1"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c3"));
		odb.store(new Class1("c4"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		q.orderByAsc("name");
		Objects<Class1> objects = odb.getObjects(q);
		assertEquals(6, objects.size());
		while (objects.hasNext()) {
			System.out.println(objects.next());
		}

		// Check if I can add some objects to the list. (this list is backed by a btree as there was an order by in the query.
		objects.addWithKey(new SimpleCompareKey("c5"), new Class1("c5"));
		println(objects);
		println(objects.size());
		assertEquals(7,objects.size());
		
		int i=0;
		objects.reset();
		while (objects.hasNext()) {
			Class1 c1 = objects.next();
			println(c1);
			if(i==6){
				assertEquals("c5",c1.getName() );
			}
			i++;
		}
		odb.close();

	}
	//TODO nee
	public void testOrderBy2() {
		if(!isLocal || !testNewFeature){
			// this test currently only works on local mode. In CS mode , the btree collection is not ordered by the key but by an index (integer)
			// this causes a class cast exception on the btree collection when trying to manually add an object by its key
			return;
		}
		String baseName = getBaseName();

		ODB odb = open(baseName);

		odb.store(new Class1("c1"));
		odb.store(new Class1("c1"));
		odb.store(new Class1("c2"));
		odb.store(new Class1("c4"));
		odb.store(new Class1("c5"));
		odb.store(new Class1("c6"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		q.orderByAsc("name");
		Objects<Class1> objects = odb.getObjects(q);
		assertEquals(6, objects.size());
		while (objects.hasNext()) {
			System.out.println(objects.next());
		}

		// Check if I can add some objects to the list. (this list is backed by a btree as there was an order by in the query.
		objects.addWithKey(new SimpleCompareKey("c3"), new Class1("c3"));
		println(objects);
		println(objects.size());
		assertEquals(7,objects.size());
		int i=0;
		objects.reset();
		while (objects.hasNext()) {
			Class1 c1 = objects.next();
			println(c1);
			if(i==3){
				assertEquals("c3",c1.getName() );
			}
			i++;
		}
		odb.close();

	}
	
	public void testOrderBy3() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

//		odb.store(new Class1("c1"));
//		odb.store(new Class1("c1"));
//		odb.store(new Class1("c2"));
//		odb.store(new Class1("c4"));
//		odb.store(new Class1("c5"));
//		odb.store(new Class1("c6"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		q.orderByAsc("name");
		Objects<Class1> objects = odb.getObjects(q);
		assertEquals(0, objects.size());
		while (objects.hasNext()) {
			System.out.println(objects.next());
		}

		// Check if I can add some objects to the list. (this list is backed by a btree as there was an order by in the query.
		objects.addWithKey(new SimpleCompareKey("c3"), new Class1("c3"));
		objects.addWithKey(new SimpleCompareKey("c2"), new Class1("c2"));
		objects.addWithKey(new SimpleCompareKey("c1"), new Class1("c1"));
		println(objects);
		println(objects.size());
		assertEquals(3,objects.size());
		int i=0;
		objects.reset();
		while (objects.hasNext()) {
			Class1 c1 = objects.next();
			println(c1);
			if(i==0){
				assertEquals("c1",c1.getName() );
			}
			i++;
		}
		odb.close();

	}
	
	public void testOrderBy4() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

//		odb.store(new Class1("c1"));
//		odb.store(new Class1("c1"));
//		odb.store(new Class1("c2"));
//		odb.store(new Class1("c4"));
//		odb.store(new Class1("c5"));
//		odb.store(new Class1("c6"));

		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(Class1.class);
		q.orderByAsc("name");
		Objects<Class1> objects = odb.getObjects(q);
		assertEquals(0, objects.size());
		while (objects.hasNext()) {
			System.out.println(objects.next());
		}

		Class1 c1 = new Class1("c1"); 
		Class1 c2 = new Class1("c2");
		Class1 c3 = new Class1("c3");
		
		
		// Check if I can add some objects to the list. (this list is backed by a btree as there was an order by in the query.
		objects.addWithKey(new SimpleCompareKey(c1.getName()), c1);
		objects.addWithKey(new SimpleCompareKey(c2.getName()), c2);
		objects.addWithKey(new SimpleCompareKey(c3.getName()), c3);
		println(objects);
		println(objects.size());
		assertEquals(3,objects.size());
		int i=0;
		objects.reset();
		Class1 c = null;
		while (objects.hasNext()) {
			c = objects.next();
			println(c);
			if(i==0){
				assertEquals("c1",c.getName() );
			}
			i++;
		}
		odb.close();
		
		objects.removeByKey(new SimpleCompareKey(c1.getName()), c1);
		assertEquals(2,objects.size());
		objects.removeByKey(new SimpleCompareKey(c2.getName()), c2);
		assertEquals(1,objects.size());
		objects.removeByKey(new SimpleCompareKey(c3.getName()), c3);
		assertEquals(0,objects.size());
		println("Final size is " + objects.size());

	}

}
