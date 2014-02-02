/**
 * 
 */
package org.neodatis.odb.test.fromusers.OlegNekludov;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestListReOrdering extends ODBTest {
	public void test1() {
		String baseName = getBaseName();
		// OdbConfiguration.setInPlaceUpdate(true);
		ODB odb = null;

		try {
			odb = open(baseName);

			Container c = new Container();
			c.addItem(new Item("1"));
			c.addItem(new Item("2"));
			c.addItem(new Item("3"));
			c.addItem(new Item("4"));

			OID oid = odb.store(c);
			odb.close();

			odb = open(baseName);
			Container c2 = (Container) odb.getObjectFromId(oid);

			println("First get = " + c2);
			Objects<Item> itens = odb.getObjects(Item.class, true);

			int i = itens.size() - 1;
			while (itens.hasNext()) {
				Item it = itens.next();
				c2.setItem(i, it);
				println(it);
				i--;
			}

			odb.store(c2);
			println("before storing updated list = " + c2);
			odb.close();

			odb = open(baseName);
			c2 = (Container) odb.getObjectFromId(oid);
			println("after store = " + c2);
			assertEquals("4", c2.items.get(0).getName());
			assertEquals("3", c2.items.get(1).getName());
			assertEquals("2", c2.items.get(2).getName());
			assertEquals("1", c2.items.get(3).getName());

		} finally {
			if (odb != null) {
				odb.close();
				deleteBase(baseName);
			}
		}

	}
}
