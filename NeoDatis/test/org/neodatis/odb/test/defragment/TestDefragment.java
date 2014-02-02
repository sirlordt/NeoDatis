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
package org.neodatis.odb.test.defragment;

import java.math.BigInteger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestDefragment extends ODBTest {
	/** The name of the database file */
	public static final String ODB_FILE_NAME_1 = "defrag1.neodatis";

	public static final String ODB_FILE_NAME_2 = "defrag1-bis.neodatis";

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase(ODB_FILE_NAME_1);
		deleteBase(ODB_FILE_NAME_2);

		ODB odb = open(ODB_FILE_NAME_1);
		User user = new User("olivier", "olivier@neodatis.com", null);
		odb.store(user);
		odb.close();
		

		odb = open(ODB_FILE_NAME_1);
		odb.defragmentTo(DIRECTORY + ODB_FILE_NAME_2);

		ODB newOdb = open(ODB_FILE_NAME_2);

		// int n = odb.getObjects(User.class).size();
		// println("n="+n);
		BigInteger nbUser = odb.count(new CriteriaQuery(User.class));
		BigInteger nbNewUser = newOdb.count(new CriteriaQuery(User.class));

		assertEquals(nbUser, nbNewUser);

		assertEquals(odb.count(new CriteriaQuery(Profile.class)), newOdb.count(new CriteriaQuery(Profile.class)));
		odb.close();
		newOdb.close();
		deleteBase(ODB_FILE_NAME_1);
		deleteBase(ODB_FILE_NAME_2);

	}

	public void test2() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase(ODB_FILE_NAME_1);
		deleteBase(ODB_FILE_NAME_2);

		ODB odb = open(ODB_FILE_NAME_1);
		Profile p = new Profile("profile");
		for (int i = 0; i < 500; i++) {
			User user = new User("olivier " + i, "olivier@neodatis.com " + i, p);
			odb.store(user);
		}
		odb.close();

		odb = open(ODB_FILE_NAME_1);
		odb.defragmentTo(DIRECTORY + ODB_FILE_NAME_2);

		ODB newOdb = open(ODB_FILE_NAME_2);

		assertEquals(odb.count(new CriteriaQuery(User.class)), newOdb.count(new CriteriaQuery(User.class)));
		assertEquals(odb.count(new CriteriaQuery(Profile.class)), newOdb.count(new CriteriaQuery(Profile.class)));
		odb.close();
		newOdb.close();
		deleteBase(ODB_FILE_NAME_1);
		deleteBase(ODB_FILE_NAME_2);

	}

	public void test3() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase(ODB_FILE_NAME_1);
		deleteBase(ODB_FILE_NAME_2);
		OdbConfiguration.setAutomaticallyIncreaseCacheSize(true);
		ODB odb = open(ODB_FILE_NAME_1);
		for (int i = 0; i < 15000; i++) {
			User user = new User("olivier " + i, "olivier@neodatis.com " + i, new Profile("profile" + i));
			odb.store(user);
			/*
			 * if(i>996){ Configuration.setDebugEnabled(true); }
			 */
		}
		odb.close();

		odb = open(ODB_FILE_NAME_1);
		odb.defragmentTo(DIRECTORY + ODB_FILE_NAME_2);

		ODB newOdb = open(ODB_FILE_NAME_2);

		assertEquals(odb.count(new CriteriaQuery(User.class)), newOdb.count(new CriteriaQuery(User.class)));
		odb.close();
		newOdb.close();
		deleteBase(ODB_FILE_NAME_1);
		deleteBase(ODB_FILE_NAME_2);

	}

}
