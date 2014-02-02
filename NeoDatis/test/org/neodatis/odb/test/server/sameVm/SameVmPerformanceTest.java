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
package org.neodatis.odb.test.server.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class SameVmPerformanceTest extends ODBTest {
	private final static String NAME = "samevm";

	public void test1() throws Exception {
		if (!runAll) {
			return;
		}
		String baseName = getBaseName();
		int numberOfConnections = 1000;
		int size = 100;
		int port = PORT + 6;
		// deleteBase(NAME);
		ODBServer server = openServer(port);
		server.setAutomaticallyCreateDatabase(true);
		server.startServer(true);

		ODB odb = server.openClient(DIRECTORY + baseName);
		for (int j = 0; j < size; j++) {

			String name = "name-" + j;
			User user = new User(name, "email" + name, new Profile(name, new Function(name)));

			odb.store(user);

		}
		odb.close();

		long min = 1000;
		long max = -1;
		long sum = 0;

		// OdbConfiguration.setLogServerConnections(true);
		for (int i = 0; i < numberOfConnections; i++) {
			long start = System.currentTimeMillis();
			for (int j = 0; j < size; j++) {
				odb = server.openClient(DIRECTORY + baseName);
				/*
				 * String name = "name-" + i + "-" + j; User user = new
				 * User(name, "email" + name, new Profile(name, new
				 * Function(name))); odb.store(user); odb.commit();
				 */
				Objects<User> users = odb.getObjects(new CriteriaQuery(User.class, Where.equal("name", "name-" + (size / 2))));
				assertEquals(1, users.size());
				odb.store(new Function("f1"));

				odb.close();
			}
			long time = System.currentTimeMillis() - start;
			sum = sum + time;
			if (time < min) {
				min = time;
			}
			if (time > max) {
				max = time;
			}
			println("Connection " + (i + 1) + " duration=" + time + "ms");
			MemoryMonitor.displayCurrentMemory(String.format("i=%d",i), false);
		}
		server.close();
		// difference between min and max must be small : this indicates that
		// there is no degradation of performance over the time
		long mean = sum / numberOfConnections;
		long delta = Math.abs(mean - min);
		println("Min=" + min + "   /   Max=" + max + "   /   Mean = " + mean + "  /  Delta =" + delta);
		
		if(testPerformance){
			assertTrue(delta < 20);
		}
		
	}

}
