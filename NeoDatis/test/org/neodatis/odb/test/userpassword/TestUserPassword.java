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
package org.neodatis.odb.test.userpassword;

import java.util.Locale;
import java.util.ResourceBundle;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBAuthenticationRuntimeException;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.update.MyObject;
import org.neodatis.odb.test.vo.login.Function;

public class TestUserPassword extends ODBTest {
	public void testWithoutUserAndPassword() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new MyObject(10, "t1"));
		odb.close();
		odb = open(baseName);
		assertEquals(1, odb.getObjects(MyObject.class).size());
		odb.close();
		deleteBase(baseName);
	}

	public void testWithUserAndPassword() throws Exception {
		String baseName = getBaseName();
		println(baseName);

		ODB odb = open(baseName, "user", "password");
		odb.store(new MyObject(10, "t1"));
		odb.close();
		try {
			odb = open(baseName);
			fail("it should have stop for invalid user/password");
		} catch (ODBAuthenticationRuntimeException e) {
			// odb.rollback();
			// odb.close();
			// e.printStackTrace();
		}
		odb = open(baseName, "user", "password");
		assertEquals(1, odb.getObjects(MyObject.class).size());
		odb.close();
		//deleteBase(baseName);
	}

	public void testWithoutUserAndPassword2() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new MyObject(10, "t1"));
		odb.close();

		try {
			odb = open(baseName, "user", "password");
			fail("it should have stop for invalid user/password");
		} catch (ODBAuthenticationRuntimeException e) {
			// odb.rollback();
			// odb.close();
			// e.printStackTrace();
		}
		deleteBase(baseName);
	}

	public void testWithoutUserAndPasswordWithAccents() throws Exception {
		// Configuration.setDatabaseCharacterEncoding("UTF-8");
		String baseName = getBaseName();
		deleteBase(baseName);

		String user = "user";
		String password = "~^áóíã";
		ODB odb = open(baseName, user, password);
		odb.store(new Function("t1"));
		odb.close();

		try {
			odb = open(baseName, user, password);
		} catch (ODBAuthenticationRuntimeException e) {
			odb.rollback();

			fail("User/Password with accents");

		}
		odb.close();
		deleteBase(baseName);
	}

	public void testWithoutUserAndPasswordWithAccentsFromPropertyFile() throws Exception {
		// Configuration.setDatabaseCharacterEncoding("UTF-8");
		String baseName = getBaseName();
		deleteBase(baseName);
		Locale locale = Locale.getDefault();
		println(locale);
		// The test-accent property file is in the test directory
		ResourceBundle r = ResourceBundle.getBundle("test-accent");
		String user = r.getString("user");
		String password = r.getString("password");
		println(password);
		ODB odb = open(baseName, user, password);
		odb.store(new Function("t1"));
		odb.close();

		try {
			odb = open(baseName, "\u00E7\u00E3o", "ol\u00E1 chico");
		} catch (ODBAuthenticationRuntimeException e) {
			odb.rollback();

			fail("User/Password with accents");

		}
		odb.close();
		deleteBase(baseName);
	}
}
