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
package org.neodatis.odb.test.trigger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

public class TestAutoIncrementTrigger extends ODBTest {

	public static final String BASE = "trigger-auto-increment.neodatis";

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = null;
		deleteBase(BASE);

		try {

			odb = open(BASE);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new LocalAutoIncrementTrigger());

			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			odb.store(o);

			assertEquals(1, o.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test2Objects() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = null;
		deleteBase(BASE);

		try {

			odb = open(BASE);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new LocalAutoIncrementTrigger());

			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			odb.store(o);

			assertEquals(1, o.getId());

			odb.close();

			odb = open(BASE);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new LocalAutoIncrementTrigger());
			o = new ObjectWithAutoIncrementId("Object 2");

			odb.store(o);

			assertEquals(2, o.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test1000Objects() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = null;
		deleteBase(BASE);

		try {
			odb = open(BASE);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new LocalAutoIncrementTrigger());

			for (int i = 0; i < 1000; i++) {
				ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object " + (i + 1));
				odb.store(o);
				assertEquals(i + 1, o.getId());
			}
			odb.close();

			odb = open(BASE);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new LocalAutoIncrementTrigger());

			for (int i = 0; i < 1000; i++) {
				ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object - bis - " + (i + 1));
				odb.store(o);
				assertEquals(1000 + i + 1, o.getId());
			}
			odb.close();

		} finally {

		}
	}

}
