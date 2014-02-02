/*
 NeoDatis ODB : Native Object DatabaseName (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object databaseName".

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
package org.neodatis.odb.test.server.trigger.clientside;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class TestAutoIncrementTrigger extends ODBTest {

	//neodatisee
	public void test1() throws Exception {
		ODB odb = null;
		ODBServer myServer = null;
		String  baseNameName = getBaseName();
		try {

			myServer = ODBFactory.openServer(12004);
			myServer.addBase(baseNameName, baseNameName);
			myServer.startServer(true);
			odb = ODBFactory.openClient("localhost", 12004, baseNameName);
			ClientAutoIncrementTrigger it = new ClientAutoIncrementTrigger();
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class,it );
			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			OID oid = odb.store(o);

			assertEquals(1, o.getId());
			assertEquals(1, it.getNbCallsForBeforeInsert());
			assertEquals(1, it.getNbCallsForAfterInsert());
			odb.close();

			odb = ODBFactory.openClient("localhost", 12004, baseNameName);
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			assertEquals(1, oo.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	
	public void test2() throws Exception {
		ODB odb = null;
		ODBServer myServer = null;
		try {
			String  baseName = getBaseName();
			myServer = ODBFactory.openServer(12005);
			myServer.addBase(baseName, baseName);
			myServer.startServer(true);
			odb = ODBFactory.openClient("localhost", 12005, baseName);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new ClientAutoIncrementTrigger());
			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			OID oid = odb.store(o);

			assertEquals(1, o.getId());
			odb.close();

			odb = ODBFactory.openClient("localhost", 12005, baseName);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new ClientAutoIncrementTrigger());
			ObjectWithAutoIncrementId o2 = new ObjectWithAutoIncrementId("Object 2");

			OID oid2 = odb.store(o2);

			assertEquals(2, o2.getId());
			odb.close();

			odb = ODBFactory.openClient("localhost", 12005, baseName);
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid2);
			assertEquals(2, oo.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	public void test3() throws Exception {
		ODB odb = null;
		String  baseName = getBaseName();
		ODBServer myServer = null;
		try {
			int port = 12010;
			myServer = ODBFactory.openServer(port);
			myServer.addBase(baseName, baseName);
			myServer.startServer(true);

			odb = ODBFactory.openClient("localhost", port, baseName);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new ClientAutoIncrementTrigger());

			for (int i = 0; i < 1000; i++) {
				ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object " + (i + 1));
				odb.store(o);
				assertEquals(i + 1, o.getId());
				ID id = (ID) odb.getObjects(new CriteriaQuery(ID.class, Where.equal("idName", "test"))).getFirst();
				assertEquals(i + 1, id.getValue());
			}
			odb.close();

			odb = ODBFactory.openClient("localhost", port, baseName);
			Objects oos = odb.getObjects(ID.class);
			assertEquals(1, oos.size());
			ID idi = (ID) odb.getObjects(new CriteriaQuery(ID.class, Where.equal("idName", "test"))).getFirst();
			assertEquals(1000, idi.getValue());

			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new ClientAutoIncrementTrigger());
			OID oid = null;
			for (int i = 0; i < 1000; i++) {
				ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object - bis - " + (i + 1));
				oid = odb.store(o);
				assertEquals(1000 + i + 1, o.getId());
			}
			odb.close();

			odb = ODBFactory.openClient("localhost", port, baseName);
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			assertEquals(2000, oo.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	/**
	 * Test if registering a trigger on a client has side effects on other
	 * clients
	 * 
	 * @throws Exception
	 */
	public void test2clients() throws Exception {
		ODB odb1 = null;
		ODB odb2 = null;
		String  baseName = getBaseName();
		ODBServer myServer = null;
		try {

			myServer = ODBFactory.openServer(12005);
			myServer.addBase(baseName, baseName);
			myServer.startServer(true);

			odb1 = ODBFactory.openClient("localhost", 12005, baseName);
			odb2 = ODBFactory.openClient("localhost", 12005, baseName);

			odb1.addInsertTrigger(ObjectWithAutoIncrementId.class, new ClientAutoIncrementTrigger());

			ObjectWithAutoIncrementId o1 = new ObjectWithAutoIncrementId("Object 1");
			ObjectWithAutoIncrementId o2 = new ObjectWithAutoIncrementId("Object 2");
			odb1.store(o1);
			odb2.store(o2);
			assertEquals(1, o1.getId());
			assertEquals(0, o2.getId());

			ID id = (ID) odb1.getObjects(new CriteriaQuery(ID.class, Where.equal("idName", "test"))).getFirst();
			assertEquals(1, id.getValue());
			assertTrue(odb2.getObjects(new CriteriaQuery(ID.class, Where.equal("idName", "test"))).isEmpty());

		} finally {
			if (odb1 != null) {
				odb1.close();
			}
			if (odb2 != null) {
				odb2.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

}
