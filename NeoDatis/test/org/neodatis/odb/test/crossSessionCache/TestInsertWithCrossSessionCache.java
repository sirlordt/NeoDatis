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
package org.neodatis.odb.test.crossSessionCache;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.odb.impl.core.transaction.CrossSessionCache;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestInsertWithCrossSessionCache extends ODBTest {
	
	public void test1() throws Exception {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function login = new Function("login");
		Function logout = new Function("logout");
		OID oid1 = odb.store(login);
		OID oid2 = odb.store(logout);

		odb.close();
		ICrossSessionCache cache = CacheFactory.getCrossSessionCache(odb.getName());

		assertEquals(oid1, cache.getOid(login));
		assertEquals(oid2, cache.getOid(logout));
	}

	public void testDisconnect() throws Exception {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		// Automatical reconnect off
		OdbConfiguration.setReconnectObjectsToSession(false);
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function login = new Function("login");
		Function logout = new Function("logout");
		OID oid1 = odb.store(login);
		OID oid2 = odb.store(logout);


		odb.close();

		odb = open(baseName);
		Objects objects = odb.getObjects(new CriteriaQuery(Function.class));
		assertEquals(2, objects.size());
		Function f = (Function) objects.getFirst();
		odb.disconnect(f);
		// Storing after disconnect should create a new one
		OID oid3 = odb.store(f);
		odb.close();

		odb = open(baseName);
		objects = odb.getObjects(new CriteriaQuery(Function.class));
		odb.close();
		println(objects.size() + " objects");

		assertEquals(3, objects.size());

		odb = open(baseName);
		
		f.setName("This is a reconnected function!");
		odb.store(f);
		odb.close();

		odb = open(baseName);
		objects = odb.getObjects(new CriteriaQuery(Function.class));
		Function ff = (Function) odb.getObjectFromId(oid3);
		odb.close();
		assertEquals(4, objects.size());
		assertEquals("login", ff.getName());
		println(objects.size() + " objects");
		OdbConfiguration.setReconnectObjectsToSession(true);

	}

	public void testReconnect() throws InterruptedException {

		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function f1 = new Function("f1");
		odb.store(f1);
		odb.close();

		odb = open(baseName);
		f1.setName("Function 1");
		odb.store(f1);
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(Function.class);
		assertEquals(1, os.size());
		Function ff1 = (Function) os.getFirst();
		odb.close();

		assertEquals("Function 1", ff1.getName());

	}

	public void testReconnectXXFunctions() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function f1 = new Function("f1");
		odb.store(f1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			f1.setName("Function " + i);
			odb.store(f1);
			odb.close();

			odb = open(baseName);
			assertEquals("Function " + i, f1.getName());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
	}

	public void testAutoReconnectXXFunctions() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function f1 = new Function("f1");
		odb.store(f1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			f1.setName("Function " + i);
			odb.store(f1);
			odb.close();

			odb = open(baseName);
			assertEquals("Function " + i, f1.getName());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
	}

	public void testReconnectUser() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user1", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		odb.store(user1);
		odb.close();

		odb = open(baseName);
		user1.setName("USER 11");
		odb.store(user1);
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(User.class);
		assertEquals(1, os.size());
		User uu1 = (User) os.getFirst();
		odb.close();

		assertEquals("USER 11", uu1.getName());

	}

	public void testReconnectXXUsers() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);
		User user1 = new User("user1", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		OID oid = odb.store(user1);
		odb.close();
		ICrossSessionCache cache = CacheFactory.getCrossSessionCache(odb.getName());
		for (int i = 0; i < 1000; i++) {
			if(i%1000==0){
				println(String.format("i=%d  ,  cache size=%s", i, cache.toString()));
			}
			odb = open(baseName);
			user1.setName("USER " + i);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("USER " + i, u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
	}

	/**
	 * to test automatical reconnect
	 * 
	 */
	public void testAutoReconnectXXUsers() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user1", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		OID oid = odb.store(user1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			user1.setName("USER " + i);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("USER " + i, u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
	}

	public void testAutoReconnectXXUsersWithNullProfile() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user1", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		OID oid = odb.store(user1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			user1.setName("USER " + i);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("USER " + i, u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
	}

	public void testAutoReconnectXXUsersNoModificationWithClose() throws InterruptedException {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		CrossSessionCache.clearAll();
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user1", "user@neodatis.org", null);
		OID oid = odb.store(user1);
		odb.close();
		

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("user1", u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(0, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(0, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
	}

	public void testAutoReconnectXXUsersNoModificationWithCommit() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user1", "user@neodatis.org", null);
		OID oid = odb.store(user1);
		odb.close();

		odb = open(baseName);
		for (int i = 0; i < 1000; i++) {
			
			odb.store(user1);
			odb.commit();
			if (i % 500 == 0) {
				MemoryMonitor.displayCurrentMemory(""+i, false);
			}

			User u = (User) odb.getObjectFromId(oid);
			assertEquals("user1", u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(0, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(0, odb.count(new CriteriaQuery(Function.class)).longValue());
			
		}
		odb.close();

	}
	
	public void testCacheWithSameDb() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		if(!testKnownProblems){
			return;
		}

		
		String baseName = getBaseName();
		ODB odb = open(baseName);
		Function f = new Function("function");
		OID oid = odb.store(f);
		odb.close();

		// delete database, but cross session cache is not cleared => object is still in the cache
		deleteBase(baseName);
		odb = open(baseName);
		try{
			odb.store(f);
		}catch (Exception e) {
			e.printStackTrace();
			fail("it should have worked. It failed because of the cross session cache that kept the object reference");
		}

		odb.close();

	}

	public void t1() {
		String s = "neodatis";
		assertTrue(s.startsWith("neodatis"));
	}
	public static void main(String[] args) throws InterruptedException {
		TestInsertWithCrossSessionCache t = new TestInsertWithCrossSessionCache();
		t.testAutoReconnectXXUsersNoModificationWithClose();
	}
}
