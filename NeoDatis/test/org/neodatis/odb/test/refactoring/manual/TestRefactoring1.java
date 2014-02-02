package org.neodatis.odb.test.refactoring.manual;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestRefactoring1 extends ODBTest {

	public void test1() throws Exception {
		ODB odb = open("refac");
		odb.close();
		Item item = new Item("oli");
		//item.date = new Date();
		//item.s1 = "Olivier";
		item.s2 = "Pierre";
		//deleteBase("refac");
		odb = open("refac");
		Objects<Item> items = odb.getObjects(Item.class);
		System.out.println(items.size());

		odb.store(item);

		odb.close();
		System.out.println("dOne");
	}
}
