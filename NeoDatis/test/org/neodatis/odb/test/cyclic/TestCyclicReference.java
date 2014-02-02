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
package org.neodatis.odb.test.cyclic;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.country.City;
import org.neodatis.odb.test.vo.country.Country2;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestCyclicReference extends ODBTest {

	public void setUp() throws Exception {
		super.setUp();
		deleteBase("cyclic.neodatis");
		ODB odb = open("cyclic.neodatis");
		for (int i = 0; i < 1; i++) {
			City brasilia = new City("Brasilia" + i);
			Country2 brasil = new Country2("Brasil" + i);

			brasilia.setCountry(brasil);
			brasil.setCapital(brasilia);
			brasil.setPopulation(450000);

			odb.store(brasil);
		}
		odb.store(new User("name", "email", new Profile("profile")));
		odb.close();
	}

	public void test1() throws Exception {

		ODB odb = open("cyclic.neodatis");
		Objects l = odb.getObjects(Country2.class, true);
		Country2 country = (Country2) l.getFirst();
		assertEquals("Brasil0", country.getName());
		assertEquals("Brasilia0", country.getCapital().getName());
		odb.close();
	}

	public void test15() throws Exception {
		println("-------------------");
		// LogUtil.logOn(ObjectWriter.LOG_ID, true);
		// LogUtil.logOn(ObjectReader.LOG_ID, true);
		ODB odb = open("cyclic.neodatis");
		Objects l = odb.getObjects(Country2.class, true);
		Country2 country = (Country2) l.getFirst();

		City city = country.getCapital();
		city.setName("rio de janeiro");
		country.setCapital(city);

		odb.store(country);
		odb.close();

		odb = open("cyclic.neodatis");
		l = odb.getObjects(Country2.class, true);
		country = (Country2) l.getFirst();
		assertEquals("rio de janeiro", country.getCapital().getName());
		l = odb.getObjects(new CriteriaQuery(City.class, Where.equal("name", "rio de janeiro")));
		assertEquals(1, l.size());
		l = odb.getObjects(new CriteriaQuery(City.class));
		assertEquals(1, l.size());
		odb.close();

	}

	public void test2() throws Exception {
		ODB odb = open("cyclic.neodatis");
		Objects l = odb.getObjects(Country2.class, true);
		Country2 country = (Country2) l.getFirst();

		City city = new City("rio de janeiro");
		country.setCapital(city);

		odb.store(country);
		odb.close();

		odb = open("cyclic.neodatis");
		l = odb.getObjects(Country2.class, true);
		country = (Country2) l.getFirst();
		assertEquals("rio de janeiro", country.getCapital().getName());
		l = odb.getObjects(new CriteriaQuery(City.class, Where.equal("name", "rio de janeiro")));
		assertEquals(1, l.size());
		l = odb.getObjects(new CriteriaQuery(City.class));
		assertEquals(2, l.size());
		odb.close();

	}

	public void testUniqueInstance1() throws Exception {
		ODB odb = open("cyclic.neodatis");

		Objects cities = odb.getObjects(City.class, true);
		Objects countries = odb.getObjects(Country2.class, true);

		Country2 country = (Country2) countries.getFirst();
		City city = (City) cities.getFirst();

		assertTrue(country == city.getCountry());
		assertTrue(city == country.getCities().get(0));

		assertTrue(city == country.getCapital());
		odb.close();

	}

	public void testUniqueInstance2() throws Exception {
		ODB odb = open("cyclic.neodatis");

		Objects countries = odb.getObjects(Country2.class, true);
		Objects cities = odb.getObjects(City.class, true);

		Country2 country = (Country2) countries.getFirst();
		City city = (City) cities.getFirst();

		assertTrue(country == city.getCountry());
		assertTrue(city == country.getCities().get(0));

		assertTrue(city == country.getCapital());
		odb.close();

	}

	public void test10() throws Exception {
		ODB odb = null;

		try {
			String baseName = getBaseName();
			deleteBase(baseName);
			odb = open(baseName);
			ClassA ca = new ClassA();
			ClassB cb = new ClassB(ca, "b");
			ca.setClassb(cb);
			ca.setName("a");
			odb.store(ca);
			ClassInfo ci = Dummy.getEngine(odb).getSession(true).getMetaModel().getClassInfo(ClassA.class.getName(), true);
			assertTrue(ci.hasCyclicReference());
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test11() throws Exception {
		ODB odb = null;

		try {
			odb = open("cyclic.neodatis");
			ClassInfo ci = Dummy.getEngine(odb).getSession(true).getMetaModel().getClassInfo(User.class.getName(), true);
			assertFalse(ci.hasCyclicReference());
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void tearDown() throws Exception {
		deleteBase("cyclic.neodatis");
	}

}
