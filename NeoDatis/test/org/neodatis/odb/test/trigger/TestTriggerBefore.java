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
import org.neodatis.odb.OID;
import org.neodatis.odb.test.ODBTest;

public class TestTriggerBefore extends ODBTest {

	// fails when the trigger is called after the object introspection (1.9
	// beta2)
	public void test1() throws Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		ODB odb = null;
		String baseName = getBaseName();
		deleteBase(baseName);
		MyTriggerBefore myTrigger = new MyTriggerBefore();
		try {

			odb = open(baseName);
			odb.addInsertTrigger(SimpleObject.class, myTrigger);

			SimpleObject so = new SimpleObject(5);
			OID oid = odb.store(so);

			assertEquals(6, so.getId());
			odb.close();

			odb = open(baseName);
			SimpleObject so2 = (SimpleObject) odb.getObjectFromId(oid);
			assertEquals(6, so2.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
		deleteBase(baseName);
	}

	public void test2UpdateTriggers() throws Exception {
		if (!testNewFeature) {
			return;
		}

		ODB odb = null;
		String baseName = getBaseName();
		deleteBase(baseName);
		MyUpdateTriggerBefore myTrigger = new MyUpdateTriggerBefore();
		try {

			odb = open(baseName);

			SimpleObject so = new SimpleObject(5);
			OID oid = odb.store(so);

			assertEquals(5, so.getId());
			odb.close();

			odb = open(baseName);
			odb.addUpdateTrigger(SimpleObject.class, myTrigger);

			SimpleObject so2 = (SimpleObject) odb.getObjectFromId(oid);
			assertEquals(5, so2.getId());

			odb.store(so2);
			odb.close();
			assertEquals(6, so2.getId());

			odb = open(baseName);

			so2 = (SimpleObject) odb.getObjectFromId(oid);
			assertEquals(6, so2.getId());

		} finally {
			if (odb != null && !odb.isClosed()) {
				odb.close();
			}
		}
		deleteBase(baseName);
	}

}
