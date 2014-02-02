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
import org.neodatis.odb.test.ODBTest;

public class TestUpdateTrigger extends ODBTest {

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
			ClientSideUpdateTrigger it = new ClientSideUpdateTrigger();
			
			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			OID oid = odb.store(o);

			assertEquals(0, it.getNbUpdatesAfter());
			assertEquals(0, it.getNbUpdatesBefore());
			odb.close();
			

			odb = ODBFactory.openClient("localhost", 12004, baseNameName);
			odb.addUpdateTrigger(ObjectWithAutoIncrementId.class,it );
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			oo.setName("this is a new name");
			odb.store(oo);
			assertEquals(1, it.getNbUpdatesBefore());
			assertEquals(1, it.getNbUpdatesAfter());
			


		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	
}
