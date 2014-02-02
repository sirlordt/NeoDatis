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
package org.neodatis.odb.test.arraycollectionmap;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.arraycollectionmap.Dictionnary;
import org.neodatis.odb.test.vo.login.Function;

public class TestMap extends ODBTest {

	public void setUp(String baseName) throws Exception {
		
		ODB odb = open(baseName);

		Dictionnary dictionnary1 = new Dictionnary("test1");
		dictionnary1.addEntry("olivier", "Smadja");
		dictionnary1.addEntry("kiko", "vidal");
		dictionnary1.addEntry("karine", "galvao");

		Dictionnary dictionnary2 = new Dictionnary("test2");
		dictionnary2.addEntry("f1", new Function("function1"));
		dictionnary2.addEntry("f2", new Function("function2"));
		dictionnary2.addEntry("f3", new Function("function3"));
		dictionnary2.addEntry(dictionnary1, new Function("function4"));
		dictionnary2.addEntry(null, new Function("function3"));
		dictionnary2.addEntry(null, null);
		dictionnary2.addEntry("f4", null);
		odb.store(dictionnary1);
		odb.store(dictionnary2);
		odb.store(new Function("login"));
		odb.close();
	}

	public void test1() throws Exception {
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);

		Objects l = odb.getObjects(Dictionnary.class, true);
		// assertEquals(2,l.size());
		Dictionnary dictionnary = (Dictionnary) l.getFirst();

		assertEquals("Smadja", dictionnary.get("olivier"));
		odb.close();

	}

	public void test2() throws Exception {
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);

		Objects l = odb.getObjects(Dictionnary.class);
		CriteriaQuery aq = new CriteriaQuery(Dictionnary.class, Where.equal("name", "test2"));
		l = odb.getObjects(aq);
		Dictionnary dictionnary = (Dictionnary) l.getFirst();

		assertEquals(new Function("function2").getName(), ((Function) dictionnary.get("f2")).getName());
		odb.close();

	}

	public void test3() throws Exception {
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);
		long size = odb.count(new CriteriaQuery(Dictionnary.class)).longValue();
		Dictionnary dictionnary1 = new Dictionnary("test1");
		dictionnary1.setMap(null);
		odb.store(dictionnary1);
		odb.close();

		odb = open(baseName);
		assertEquals(size + 1, odb.getObjects(Dictionnary.class).size());
		assertEquals(size + 1, odb.count(new CriteriaQuery(Dictionnary.class)).longValue());
		odb.close();

	}

	public void test4() throws Exception {
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);
		long n = odb.count(new CriteriaQuery(Dictionnary.class)).longValue();
		IQuery query = new CriteriaQuery(Dictionnary.class, Where.equal("name", "test2"));
		Objects l = odb.getObjects(query);
		Dictionnary dictionnary = (Dictionnary) l.getFirst();
		dictionnary.setMap(null);
		odb.store(dictionnary);
		odb.close();

		odb = open(baseName);
		assertEquals(n, odb.count(new CriteriaQuery(Dictionnary.class)).longValue());
		Dictionnary dic = (Dictionnary) odb.getObjects(query).getFirst();
		assertEquals(null, dic.getMap());
		odb.close();

	}

	public void test5updateIncreasingSize() throws Exception {
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);
		long n = odb.count(new CriteriaQuery(Dictionnary.class)).longValue();
		IQuery query = new CriteriaQuery(Dictionnary.class, Where.equal("name", "test2"));
		Objects l = odb.getObjects(query);
		Dictionnary dictionnary = (Dictionnary) l.getFirst();
		dictionnary.setMap(null);
		odb.store(dictionnary);
		odb.close();

		odb = open(baseName);
		assertEquals(n, odb.count(new CriteriaQuery(Dictionnary.class)).longValue());
		Dictionnary dic = (Dictionnary) odb.getObjects(query).getFirst();
		assertNull(dic.getMap());
		odb.close();

		odb = open(baseName);
		dic = (Dictionnary) odb.getObjects(query).getFirst();
		dic.addEntry("olivier", "Smadja");
		odb.store(dic);
		odb.close();

		odb = open(baseName);
		dic = (Dictionnary) odb.getObjects(query).getFirst();

		assertNotNull(dic.getMap());
		assertEquals("Smadja", dic.getMap().get("olivier"));
		odb.close();

	}

	public void test6updateDecreasingSize() throws Exception {
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);
		long n = odb.count(new CriteriaQuery(Dictionnary.class)).longValue();
		IQuery query = new CriteriaQuery(Dictionnary.class, Where.equal("name", "test2"));
		Objects l = odb.getObjects(query);
		Dictionnary dictionnary = (Dictionnary) l.getFirst();
		int mapSize = dictionnary.getMap().size();
		dictionnary.getMap().remove("f1");
		odb.store(dictionnary);
		odb.close();

		odb = open(baseName);
		assertEquals(n, odb.count(new CriteriaQuery(Dictionnary.class)).longValue());
		Dictionnary dic = (Dictionnary) odb.getObjects(query).getFirst();
		assertEquals(mapSize - 1, dic.getMap().size());
		odb.close();
	}

	public void test6updateChangingKeyValue() throws Exception {
		// to monitor in place updates
		AbstractObjectWriter.resetNbUpdates();
		String baseName = getBaseName();
		setUp(baseName);
		ODB odb = open(baseName);
		long n = odb.count(new CriteriaQuery(Dictionnary.class)).longValue();
		IQuery query = new CriteriaQuery(Dictionnary.class, Where.equal("name", "test2"));
		Objects l = odb.getObjects(query);
		Dictionnary dictionnary = (Dictionnary) l.getFirst();
		dictionnary.getMap().put("f1", "changed function");
		odb.store(dictionnary);
		odb.close();

		odb = open(baseName);
		assertEquals(n, odb.count(new CriteriaQuery(Dictionnary.class)).longValue());
		Dictionnary dic = (Dictionnary) odb.getObjects(query).getFirst();
		assertEquals("changed function", dic.getMap().get("f1"));
		odb.close();
	}

}
