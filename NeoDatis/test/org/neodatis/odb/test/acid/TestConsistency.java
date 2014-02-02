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
package org.neodatis.odb.test.acid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestConsistency extends ODBTest {

	public static String ODB_FILE_NAME = "consistency.neodatis";

	public void createInconsistentFile() throws Exception {
		ODB odb = open(ODB_FILE_NAME);
		for (int i = 0; i < 10; i++) {
			Object o = getUserInstance();
			odb.store(o);
		}
		odb.close();

		odb = open(ODB_FILE_NAME);
		for (int i = 0; i < 10; i++) {
			Object o = getUserInstance();
			odb.store(o);
		}

	}

	private TestClass getTestClassInstance() {
		TestClass tc = new TestClass();
		tc.setBigDecimal1(new BigDecimal(1.123456789));
		tc.setBoolean1(true);
		tc.setChar1('d');
		tc.setDouble1(new Double(154.78998989));
		tc.setInt1(78964);
		tc.setString1("Ola chico como vc está ???");
		tc.setDate1(new Date());
		return tc;
	}

	private Object getUserInstance() {
		Function login = new Function("login");
		Function logout = new Function("logout");
		List<Function> list = new ArrayList<Function>();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);
		return user;
	}

	public void test1() {
		assertTrue(true);
	}

	public static void main(String[] args) throws Exception {
		new TestConsistency().createInconsistentFile();
		// new TestConsistency().openFile();
	}
}