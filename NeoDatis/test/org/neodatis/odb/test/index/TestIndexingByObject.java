package org.neodatis.odb.test.index;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * Junit to test indexing an object when the index field is an object and not a
 * native attribute
 */
public class TestIndexingByObject extends ODBTest {

	public void test1() throws Exception {
		println("************START OF TEST1***************");
		String baseName = getBaseName();
		deleteBase("index-object");
		ODB odb = open("index-object");
		String[] fields = { "object" };
		odb.getClassRepresentation(IndexedObject2.class).addUniqueIndexOn("index1", fields, true);

		IndexedObject2 o1 = new IndexedObject2("Object1", new IndexedObject("Inner Object 1", 10, new Date()));
		odb.store(o1);
		odb.close();

		odb = open("index-object");
		// First get the object used to index
		Objects objects = odb.getObjects(IndexedObject.class);
		IndexedObject io = (IndexedObject) objects.getFirst();
		IQuery q = odb.criteriaQuery(IndexedObject2.class, Where.equal("object", io));
		objects = odb.getObjects(q);
		IndexedObject2 o2 = (IndexedObject2) objects.getFirst();
		odb.close();
		assertEquals(o1.getName(), o2.getName());
		println(q.getExecutionPlan().getDetails());
		assertFalse(q.getExecutionPlan().getDetails().indexOf("index1") == -1);
		deleteBase("index-object");
		println("************END OF TEST1***************");
	}

	public void test2() throws Exception {
		println("************START OF TEST2***************");
		deleteBase("index-object");
		ODB odb = open("index-object");
		String[] fields = { "object" };
		odb.getClassRepresentation(IndexedObject2.class).addUniqueIndexOn("index1", fields, true);

		int size = isLocal ? 5000 : 500;
		for (int i = 0; i < size; i++) {
			odb.store(new IndexedObject2("Object " + i, new IndexedObject("Inner Object " + i, i, new Date())));
		}
		odb.close();

		odb = open("index-object");

		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "Inner Object " + (size - 1)));

		// First get the object used to index, the last one. There is no index
		// on the class and field
		long start0 = OdbTime.getCurrentTimeInMs();
		Objects objects = odb.getObjects(q);
		long end0 = OdbTime.getCurrentTimeInMs();
		IndexedObject io = (IndexedObject) objects.getFirst();
		println("d0=" + (end0 - start0));
		println(q.getExecutionPlan().getDetails());
		q = odb.criteriaQuery(IndexedObject2.class, Where.equal("object", io));

		long start = OdbTime.getCurrentTimeInMs();
		objects = odb.getObjects(q);
		long end = OdbTime.getCurrentTimeInMs();
		println("d=" + (end - start));
		IndexedObject2 o2 = (IndexedObject2) objects.getFirst();
		odb.close();

		assertEquals("Object " + (size - 1), o2.getName());
		println(q.getExecutionPlan().getDetails());
		assertTrue(q.getExecutionPlan().useIndex());
		deleteBase("index-object");
		println("************END OF TEST2***************");
	}
	
	public void test3_BadAttributeInIndex() throws Exception {

		String baseName = getBaseName();
		ODB odb = null;
		String fieldName = "fkjdsfkjdhfjkdhjkdsh";
		try{
			odb = open(baseName);
			String[] fields = { fieldName };
			odb.getClassRepresentation(IndexedObject2.class).addUniqueIndexOn("index1", fields, true);
			fail("Should have thrown an exception because the field "+fieldName + " does not exist");
		}catch (Exception e) {
			// normal
		}
		finally{
			odb.close();
			deleteBase(baseName);
		}
	}

	public void test4() throws Exception {

		String baseName = getBaseName();
		ODB odb = open(baseName);
		String[] fields = { "object" };
		odb.getClassRepresentation(IndexedObject2.class).addUniqueIndexOn("index1", fields, true);

		String[] fields2 = { "name" };
		odb.getClassRepresentation(IndexedObject.class).addUniqueIndexOn("index2", fields2, true);

		int size = isLocal ? 5000 : 500;
		for (int i = 0; i < size; i++) {
			odb.store(new IndexedObject2("Object " + i, new IndexedObject("Inner Object " + i, i, new Date())));
		}
		odb.close();

		odb = open(baseName);

		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "Inner Object " + (size - 1)));

		// First get the object used to index, the last one. There is no index
		// on the class and field
		long start0 = OdbTime.getCurrentTimeInMs();
		Objects objects = odb.getObjects(q);
		long end0 = OdbTime.getCurrentTimeInMs();
		// check if index has been used
		assertTrue(q.getExecutionPlan().useIndex());

		IndexedObject io = (IndexedObject) objects.getFirst();
		println("d0=" + (end0 - start0));
		println(q.getExecutionPlan().getDetails());
		q = odb.criteriaQuery(IndexedObject2.class, Where.equal("object", io));

		long start = OdbTime.getCurrentTimeInMs();
		objects = odb.getObjects(q);
		long end = OdbTime.getCurrentTimeInMs();
		println("d=" + (end - start));
		IndexedObject2 o2 = (IndexedObject2) objects.getFirst();
		odb.close();

		assertEquals("Object " + (size - 1), o2.getName());
		println(q.getExecutionPlan().getDetails());
		assertTrue(q.getExecutionPlan().useIndex());
		deleteBase(baseName);
	}

}
