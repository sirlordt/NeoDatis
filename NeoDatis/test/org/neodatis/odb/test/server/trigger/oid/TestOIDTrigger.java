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
package org.neodatis.odb.test.server.trigger.oid;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.tool.IOUtil;

public class TestOIDTrigger extends ODBTest {

	public static final String BASE = "trigger-auto-increment-server.neodatis";

	public void testSettingOIDToField() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {

			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addOidTrigger(BASE, Tracklet.class.getName(), new MyOidTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);

			// Creates the object to be inserted
			Tracklet t = new Tracklet("tracklet 1");

			// Call the store
			OID oid = odb.store(t);

			assertEquals(oid.oidToString(), t.getId());
			
			odb.close();

			odb = ODBFactory.openClient("localhost", port, BASE);
			Tracklet t2 = (Tracklet) odb.getObjects(Tracklet.class).getFirst();
			assertEquals(oid.oidToString(), t2.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}
	
	public void testSettingOIDWhenSelecting() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {

			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addSelectTrigger(BASE, Tracklet.class.getName(), new MySelectTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);
			int size = 500;
			OID[] oids = new OID[size];
			for(int i=0;i<size;i++){
				// Creates the object to be inserted
				Tracklet t = new Tracklet("tracklet "+i);
				// Call the store
				oids[i] = odb.store(t);
			}

			odb.close();
			
			odb = ODBFactory.openClient("localhost", port, BASE);
			Objects<Tracklet> tt = odb.getObjects(Tracklet.class);
			int i=0;
			while(tt.hasNext()){
				Tracklet t = (Tracklet) tt.next();
				System.out.println(t);
				assertEquals(oids[i].oidToString(), t.getId());
				i++;
			}
			

			

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}
	
	
	public void testSettingOIDWithNonNativeObject() throws Exception {
		if(!testNewFeature){
			return;
		}
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {

			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addOidTrigger(BASE, Profile.class.getName(), new MyOidTrigger2());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);

			// Creates the object to be inserted
			ClassA a = new ClassA("profile");

			// Call the store
			OID oid = odb.store(a);

			assertEquals(oid.oidToString(), a.getB().getId());
			
			odb.close();

			odb = ODBFactory.openClient("localhost", port, BASE);
			ClassA a2 = (ClassA) odb.getObjects(Profile.class).getFirst();
			assertEquals(oid.oidToString(), a2.getB().getId());

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
