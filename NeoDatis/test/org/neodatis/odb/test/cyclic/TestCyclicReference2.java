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
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.country.City;
import org.neodatis.odb.test.vo.country.Country;

public class TestCyclicReference2 extends ODBTest {

	public void setUp() throws Exception {
		super.setUp();
		deleteBase("cyclic.neodatis");
		ODB odb = open("cyclic.neodatis");
		Country brasil = new Country("Brasil");

		for (int i = 0; i < 10; i++) {
			City city = new City("city" + i);

			city.setCountry(brasil);
			brasil.addCity(city);
		}
		odb.store(brasil);
		odb.close();
	}

	public void test1() throws Exception {

		ODB odb = open("cyclic.neodatis");
		Objects l = odb.getObjects(Country.class, true);
		Country country = (Country) l.getFirst();
		assertEquals("Brasil", country.getName());
		odb.close();
	}

	public void test2() throws Exception {
		println("-------------------");
		// LogUtil.logOn(ObjectWriter.LOG_ID, true);
		// LogUtil.logOn(ObjectReader.LOG_ID, true);
		ODB odb = open("cyclic.neodatis");
		Objects l = odb.getObjects(Country.class, true);
		Country country = (Country) l.getFirst();
		assertEquals(10, country.getCities().size());

		odb.close();

	}

	public void tearDown() throws Exception {
		deleteBase("cyclic.neodatis");
	}

}
