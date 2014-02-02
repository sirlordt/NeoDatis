/*
NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

"This file is part of the NeoDatis ODB open source object database".

NeoDatis ODB is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

NeoDatis ODB is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test.query.values;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * @author olivier
 * 
 */
public class TestSubList extends ODBTest {
	public void testSubListJava() {
		List l = new ArrayList();
		l.add("param1");
		l.add("param2");
		l.add("param3");
		l.add("param4");

		int fromIndex = 1;
		int size = 2;
		int endIndex = fromIndex + size;
		List l2 = l.subList(fromIndex, endIndex);
		assertEquals(2, l2.size());
	}

	/** Test when size is bigger than list */
	public void testSubListJava2() {
		List l = new ArrayList();
		l.add("param1");
		l.add("param2");
		l.add("param3");
		l.add("param4");

		int fromIndex = 1;
		int size = 20;
		int endIndex = fromIndex + size;
		boolean throwException = false;

		if (!throwException) {
			if (endIndex > l.size()) {
				endIndex = l.size();
			}
		}

		List l2 = l.subList(fromIndex, endIndex);
		assertEquals(3, l2.size());
	}

	/** Test when start index is greater than list size */
	public void testSubListJava3() {
		List l = new ArrayList();

		int fromIndex = 100;
		int size = 20;
		int endIndex = fromIndex + size;
		boolean throwException = false;

		if (!throwException) {
			if (fromIndex > l.size() - 1) {
				fromIndex = 0;
			}
			if (endIndex > l.size()) {
				endIndex = l.size();
			}
		}

		List l2 = l.subList(fromIndex, endIndex);
		assertEquals(0, l2.size());
	}

	public void test1() throws IOException, Exception {
		deleteBase("valuesSubList");
		ODB odb = open("valuesSubList");

		Handler handler = new Handler();
		for (int i = 0; i < 10; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		odb.store(handler);
		odb.close();

		odb = open("valuesSubList");
		Values values = odb.getValues(new ValuesCriteriaQuery(Handler.class).field("parameters").sublist("parameters", "sub1", 1, 5, true)
				.sublist("parameters", "sub2", 1, 10).size("parameters", "size"));

		println(values);
		ObjectValues ov = values.nextValues();

		List fulllist = (List) ov.getByAlias("parameters");
		assertEquals(10, fulllist.size());
		Long size = (Long) ov.getByAlias("size");
		assertEquals(10, size.longValue());

		Parameter p = (Parameter) fulllist.get(0);
		assertEquals("value 0", p.getValue());

		Parameter p2 = (Parameter) fulllist.get(9);
		assertEquals("value 9", p2.getValue());

		List sublist = (List) ov.getByAlias("sub1");
		assertEquals(5, sublist.size());
		p = (Parameter) sublist.get(0);
		assertEquals("value 1", p.getValue());

		p2 = (Parameter) sublist.get(4);
		assertEquals("value 5", p2.getValue());

		List sublist2 = (List) ov.getByAlias("sub2");
		assertEquals(9, sublist2.size());

		odb.close();

	}

	public void test11() throws IOException, Exception {
		ODB odb = ODBFactory.open("valuesSubList");

		Handler handler = new Handler();
		for (int i = 0; i < 10; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		odb.store(handler);
		odb.close();

		odb = open("valuesSubList");
		Values values = odb.getValues(new ValuesCriteriaQuery(Handler.class).field("parameters").sublist("parameters", "sub1", 1, 5, true)
				.sublist("parameters", "sub2", 1, 10).size("parameters", "size"));

		ObjectValues ov = values.nextValues();

		// Retrieve Result values
		List fulllist = (List) ov.getByAlias("parameters");
		Long size = (Long) ov.getByAlias("size");
		List sublist = (List) ov.getByAlias("sub1");

		odb.close();

	}

	public void test2() throws IOException, Exception {
		deleteBase("valuesSubList2");
		ODB odb = open("valuesSubList2");

		Handler handler = new Handler();
		for (int i = 0; i < 500; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		OID oid = odb.store(handler);
		odb.close();

		odb = open("valuesSubList2");
		Handler h = (Handler) odb.getObjectFromId(oid);
		println("size of list = " + h.getListOfParameters().size());
		long start = OdbTime.getCurrentTimeInMs();
		Values values = odb.getValues(new ValuesCriteriaQuery(Handler.class).sublist("parameters", "sub", 490, 5, true).size("parameters",
				"size"));
		long end = OdbTime.getCurrentTimeInMs();
		println("time to load sublist of 5 itens from 40000 : " + (end - start));
		println(values);
		ObjectValues ov = values.nextValues();

		List sublist = (List) ov.getByAlias("sub");
		assertEquals(5, sublist.size());

		Long size = (Long) ov.getByAlias("size");
		assertEquals(500, size.longValue());

		Parameter p = (Parameter) sublist.get(0);
		assertEquals("value 490", p.getValue());

		Parameter p2 = (Parameter) sublist.get(4);
		assertEquals("value 494", p2.getValue());
		odb.close();

	}

	/** Using Object representation instead of real object */
	public void test3() throws IOException, Exception {
		int sublistSize = 10000;
		deleteBase("valuesSubList3");
		ODB odb = open("valuesSubList3");

		Handler handler = new Handler();
		for (int i = 0; i < sublistSize; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		odb.store(handler);
		odb.close();

		odb = open("valuesSubList3");
		long start = OdbTime.getCurrentTimeInMs();
		IValuesQuery q = new ValuesCriteriaQuery(Handler.class).sublist("parameters", "sub", 9990, 5, true);
		q.setReturnInstance(false);
		Values values = odb.getValues(q);
		long end = OdbTime.getCurrentTimeInMs();
		println("time to load sublist of 5 itens from 40000 : " + (end - start));
		println(values);
		ObjectValues ov = values.nextValues();

		List sublist = (List) ov.getByAlias("sub");
		assertEquals(5, sublist.size());
		NonNativeObjectInfo nnoi = (NonNativeObjectInfo) sublist.get(0);
		assertEquals("value 9990", nnoi.getValueOf("value"));

		NonNativeObjectInfo nnoi2 = (NonNativeObjectInfo) sublist.get(4);
		assertEquals("value 9994", nnoi2.getValueOf("value"));
		odb.close();

	}

	/** Using Object representation instead of real object */
	public void test5() throws IOException, Exception {
		int sublistSize = 400;
		if (!isLocal && !useSameVmOptimization) {
			sublistSize = 40;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Handler handler = new Handler();
		for (int i = 0; i < sublistSize; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		odb.store(handler);
		odb.close();

		odb = open("valuesSubList3");
		long start = OdbTime.getCurrentTimeInMs();
		IValuesQuery q = new ValuesCriteriaQuery(Handler.class).sublist("parameters", "sub", 0, 2, true);
		Values values = odb.getValues(q);
		long end = OdbTime.getCurrentTimeInMs();
		println("time to load sublist of 5 itens for " + sublistSize + " : " + (end - start));
		println(values);
		ObjectValues ov = values.nextValues();

		List sublist = (List) ov.getByAlias("sub");
		assertEquals(2, sublist.size());
		Parameter parameter = (Parameter) sublist.get(1);
		assertEquals("value 1", parameter.getValue());
		OID oid = odb.getObjectId(parameter);
		println(oid);

		odb.close();

	}

	/** Check if objects of list are known by ODB */
	public void test6() throws IOException, Exception {
		int sublistSize = 400;
		if (!isLocal && !useSameVmOptimization) {
			sublistSize = 40;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Handler handler = new Handler();
		for (int i = 0; i < sublistSize; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		odb.store(handler);
		odb.close();

		odb = open("valuesSubList3");
		long start = OdbTime.getCurrentTimeInMs();
		IQuery q = new CriteriaQuery(Handler.class);
		Objects objects = odb.getObjects(q);
		long end = OdbTime.getCurrentTimeInMs();

		Handler h = (Handler) objects.getFirst();
		Parameter parameter = (Parameter) h.getListOfParameters().get(0);
		assertEquals("value 0", parameter.getValue());
		OID oid = odb.getObjectId(parameter);

		assertNotNull(oid);
		odb.close();

	}

	public void test4() {
		deleteBase("sublist4");
		ODB odb = open("sublist4");
		int i = 0;
		List functions1 = new ArrayList();
		for (i = 0; i < 30; i++) {
			functions1.add(new Function("f1-" + i));
		}
		List functions2 = new ArrayList();
		for (i = 0; i < 60; i++) {
			functions2.add(new Function("f2-" + i));
		}
		List functions3 = new ArrayList();
		for ( i = 0; i < 90; i++) {
			functions3.add(new Function("f3-" + i));
		}
		User user1 = new User("User1", "user1@neodtis.org", new Profile("profile1", functions1));
		User user2 = new User("User2", "user1@neodtis.org", new Profile("profile2", functions2));
		User user3 = new User("User3", "user1@neodtis.org", new Profile("profile3", functions3));

		odb.store(user1);
		odb.store(user2);
		odb.store(user3);
		odb.close();

		odb = open("sublist4");
		User u = (User) odb.getObjects(User.class).getFirst();
		System.out.println(u);

		IValuesQuery q = new ValuesCriteriaQuery(Profile.class).field("name").sublist("functions", 1, 2, false).size("functions", "fsize");
		Values v = odb.getValues(q);
		i = 0;
		while (v.hasNext()) {
			ObjectValues ov = v.nextValues();
			String profileName = (String) ov.getByAlias("name");
			println(profileName);
			assertEquals("profile" + (i + 1), profileName);
			assertEquals(new Long(30 * (i + 1)), ov.getByAlias("fsize"));
			List l = (List) ov.getByAlias("functions");
			println(l);
			assertEquals(2, l.size());
			i++;

		}
		odb.close();
	}

}
