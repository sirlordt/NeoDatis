package org.neodatis.odb.test.query.nq;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.human.Animal;
import org.neodatis.odb.test.vo.human.Human;
import org.neodatis.odb.test.vo.human.Man;
import org.neodatis.odb.test.vo.human.Woman;

public class TestPolyMorphic extends ODBTest {

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IQuery q = new SimpleNativeQuery() {
			public boolean match(Animal animal) {
				return true;
			}
		};
		q.setPolymorphic(true);
		Objects os = odb.getObjects(q);
		println(os);
		odb.close();
		assertEquals(4, os.size());
		deleteBase("multi");
	}

	public void test2() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase("multi");
		ODB odb = open("multi");

		odb.store(new Animal("dog", "M", "my dog"));
		odb.store(new Animal("cat", "F", "my cat"));

		odb.store(new Man("Joe"));
		odb.store(new Woman("Karine"));

		odb.close();

		odb = open("multi");
		IQuery q = new SimpleNativeQuery() {
			public boolean match(Human human) {
				return true;
			}
		};
		q.setPolymorphic(true);
		Objects os = odb.getObjects(q);
		println(os);
		odb.close();
		assertEquals(2, os.size());
		deleteBase("multi");
	}

	public void test8() throws Exception {
		if (!isLocal) {
			return;
		}
		int size = isLocal ? 3000 : 300;
		deleteBase("multi");
		ODB odb = open("multi");
		for (int i = 0; i < size; i++) {
			odb.store(new Animal("dog", "M", "my dog" + i));
			odb.store(new Animal("cat", "F", "my cat" + i));

			odb.store(new Man("Joe" + i));
			odb.store(new Woman("my Karine" + i));
		}

		odb.close();

		odb = open("multi");
		IQuery q = new SimpleNativeQuery() {
			public boolean match(Animal object) {
				return object.getName().startsWith("my ");
			}
		};
		q.setPolymorphic(true);
		Objects objects = odb.getObjects(q);
		odb.close();
		deleteBase("multi");
		assertEquals(size * 3, objects.size());

	}

}
