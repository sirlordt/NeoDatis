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
package org.neodatis.odb.test.instantiationhelper;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.instance.InstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.ParameterHelper;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * Test if the ODB retrieves objects without default constructor and null
 * arguments
 * 
 * @author mayworm at <xmpp://mayworm@gmail.com>
 */
public class TestInstanceHelper extends ODBTest {
	private ODB odb;
	private static final String TESTDBNAME = "instanceHelper.neodatis";

	public void setUp() throws Exception {
		super.setUp();
		deleteBase(TESTDBNAME);
		odb = open(TESTDBNAME);
	}

	public void tearDown() throws Exception {
		deleteBase(TESTDBNAME);
	}

	/**
	 * Create, store and try retrieve the object without default constructor
	 * 
	 * @throws Exception
	 */
	public void testUseInstanceHelper() throws Exception {
		OdbConfiguration.setEnableEmptyConstructorCreation(false);
		try {
			ClassRepresentation carRepresentation = odb.getClassRepresentation(Car.class);
			// create a db and store a object that has not default constructor
			Car car = new Car("Ranger", new Integer(2006));
			odb.store(car);
			odb.commit();
			closeAndReopenDb();
			Objects cars;
			try {
				checkCarRetrieval();
				fail("Expected exception");
			} catch (ODBRuntimeException e) {
				// expected
			}
			closeAndReopenDb();
			carRepresentation.addInstantiationHelper(new InstantiationHelper() {
				public Object instantiate() {
					return new Car("dummyModel", new Integer(1));
				}
			});
			checkCarRetrieval();
			closeAndReopenDb();
			carRepresentation.removeInstantiationHelper();
			carRepresentation.addParameterHelper(new ParameterHelper() {
				public Object[] parameters() {
					return new Object[0];
				}
			});
			try {
				checkCarRetrieval();
				fail("Expected Exception");
			} catch (ODBRuntimeException e) {
				// expected
			}
			odb.close();
		} finally {
			OdbConfiguration.setEnableEmptyConstructorCreation(true);
		}

	}

	/**
	 * Create, store and try retrieve the object without default constructor
	 * 
	 * @throws Exception
	 */
	public void testWithoutHelperUsingNoConstructor() throws Exception {
		// create a db and store a object that has not default constructor
		Car car = new Car("Ranger", new Integer(2006));
		odb.store(car);
		closeAndReopenDb();
		Objects cars;
		checkCarRetrieval();
		odb.close();
	}

	private void checkCarRetrieval() throws Exception {
		Objects cars = odb.getObjects(Car.class);
		assertEquals(1, cars.size());
		Car car = (Car) cars.getFirst();
		assertEquals(car.getModel(), "Ranger");
		assertEquals(car.getYear(), new Integer(2006));
		CriteriaQuery query = new CriteriaQuery(Car.class, Where.equal("model", "Ranger"));
		cars = odb.getObjects(query);
		car = (Car) cars.getFirst();
		assertEquals(car.getModel(), "Ranger");
	}

	private void closeAndReopenDb() throws Exception {
		odb.close();
		odb = open(TESTDBNAME);
	}
}
