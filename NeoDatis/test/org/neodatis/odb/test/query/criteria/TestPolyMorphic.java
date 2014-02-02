package org.neodatis.odb.test.query.criteria;

import java.math.BigInteger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.human.Animal;
import org.neodatis.odb.test.vo.human.Human;
import org.neodatis.odb.test.vo.human.Man;
import org.neodatis.odb.test.vo.human.Woman;

public class TestPolyMorphic extends ODBTest {

	public void test1() throws Exception {
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IQuery q = new CriteriaQuery(Object.class);
		q.setPolymorphic(true);
		Objects os = odb.getObjects(q);
		println(os);
		odb.close();
		assertEquals(4, os.size());
		deleteBase("multi");
	}

	public void test2() throws Exception {
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IQuery q = new CriteriaQuery(Human.class);
		q.setPolymorphic(true);
		Objects os = odb.getObjects(q);
		println(os);
		odb.close();
		assertEquals(2, os.size());
		deleteBase("multi");
	}

	public void test3() throws Exception {
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IValuesQuery q = new ValuesCriteriaQuery(Object.class).field("specie");
		q.setPolymorphic(true);
		Values os = odb.getValues(q);
		println(os);
		odb.close();
		assertEquals(4, os.size());
		deleteBase("multi");
	}

	public void test4() throws Exception {
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IValuesQuery q = new ValuesCriteriaQuery(Human.class).field("specie");
		q.setPolymorphic(true);
		Values os = odb.getValues(q);
		println(os);
		odb.close();
		assertEquals(2, os.size());
		deleteBase("multi");
	}

	public void test5() throws Exception {
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IValuesQuery q = new ValuesCriteriaQuery(Man.class).field("specie");
		q.setPolymorphic(true);
		Values os = odb.getValues(q);
		println(os);
		odb.close();
		assertEquals(1, os.size());
		deleteBase("multi");
	}

	public void test6() throws Exception {
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		CriteriaQuery q = new CriteriaQuery(Object.class);
		q.setPolymorphic(true);
		BigInteger nb = odb.count(q);
		println(nb);
		odb.close();
		assertEquals(new BigInteger("4"), nb);
		deleteBase("multi");
	}

	public void test7() throws Exception {
		int size = isLocal ? 30000 : 3000;
		deleteBase("multi");
		ODB odb = open("multi");
		for (int i = 0; i < size; i++) {
			odb.store(new Animal("dog", "M", "my dog"));
			odb.store(new Animal("cat", "F", "my cat"));

			odb.store(new Man("Joe" + i));
			odb.store(new Woman("Karine" + i));
		}

		odb.close();

		odb = open("multi");
		CriteriaQuery q = new CriteriaQuery(Object.class);
		q.setPolymorphic(true);
		BigInteger nb = odb.count(q);
		println(nb);
		odb.close();
		assertEquals(new BigInteger("" + 4 * size), nb);
		deleteBase("multi");
	}

	public void test8() throws Exception {
		int size = isLocal ? 30000 : 3000;
		deleteBase("multi");
		ODB odb = open("multi");
		for (int i = 0; i < size; i++) {
			odb.store(new Animal("dog" + i, "M", "my dog" + i));
			odb.store(new Animal("cat" + i, "F", "my cat" + i));

			odb.store(new Man("Joe" + i));
			odb.store(new Woman("Karine" + i));
		}

		odb.close();

		odb = open("multi");
		CriteriaQuery q = new CriteriaQuery(Object.class, Where.equal("specie", "man"));
		q.setPolymorphic(true);
		BigInteger nb = odb.count(q);
		println(nb);
		odb.close();
		assertEquals(new BigInteger("" + 1 * size), nb);
		deleteBase("multi");
	}

}
