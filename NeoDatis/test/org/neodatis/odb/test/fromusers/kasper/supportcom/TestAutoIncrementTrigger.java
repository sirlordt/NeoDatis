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
package org.neodatis.odb.test.fromusers.kasper.supportcom;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

public class TestAutoIncrementTrigger extends ODBTest {

	public static final String BASE = "trigger-auto-increment-kasper.neodatis";

	public void test1WithServerTrigger() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {
			int size = 10;
			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addInsertTrigger(BASE, MyUser.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, MyProfile.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, MyFunction.class.getName(), new SequenceTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);
			OID[] oids = new OID[size];
			for (int i = 0; i < size; i++) {
				MyUser user1 = getUserInstance(i);

				// Call the store
				oids[i] = odb.store(user1);
			}

			odb.close();

			// Re open the db to check if ID was set
			odb = ODBFactory.openClient("localhost", port, BASE);
			int nfunction = 1;

			for (int i = 0; i < size; i++) {
				MyUser user = (MyUser) odb.getObjectFromId(oids[i]);
				assertEquals(new Long(i + 1), user.getId());
				assertEquals(new Long(i + 1), ((MyProfile) user.getProfile()).getId());
				MyFunction f1 = (MyFunction) user.getProfile().getFunctions().get(0);
				MyFunction f2 = (MyFunction) user.getProfile().getFunctions().get(1);
				MyFunction f3 = (MyFunction) user.getProfile().getFunctions().get(2);

				assertEquals("function 1 " + i, f1.getName());
				assertEquals("function 2 " + i, f2.getName());
				assertEquals("function 3 " + i, f3.getName());

				assertEquals(new Long(nfunction++), f1.getId());
				assertEquals(new Long(nfunction++), f2.getId());
				assertEquals(new Long(nfunction++), f3.getId());

			}

		} finally {
			if (odb != null && !odb.isClosed()) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	private MyUser getUserInstance(int i) {
		MyFunction f1 = new MyFunction("function 1 " + i);
		MyFunction f2 = new MyFunction("function 2 " + i);
		MyFunction f3 = new MyFunction("function 3 " + i);
		List list = new ArrayList();
		list.add(f1);
		list.add(f2);
		list.add(f3);
		MyProfile profile = new MyProfile("operator " + i, list);
		MyUser user = new MyUser("user " + i, "userr@neodatis.com", profile);
		return user;
	}

	
	
	

}