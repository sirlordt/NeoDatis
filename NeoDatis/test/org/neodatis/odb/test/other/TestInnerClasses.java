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
package org.neodatis.odb.test.other;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

public class TestInnerClasses extends ODBTest {

	public void test1() throws Exception {
		ODB odb = null;
		String baseName = getBaseName();
		try {
			Student student = new Student("sacopa", "kiko", "criativa");

			odb = open(baseName);
			odb.store(student);
			odb.close();

			odb = open(baseName);
			odb.getObjects(Student.class);
			odb.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (odb != null) {
				odb.rollback();
				odb.close();
				fail("Inner class not supported");
			}
		}

	}

	class Person {
		private String name;
		private String address;

		public Person(String address, String name) {
			super();
			// TODO Auto-generated constructor stub
			this.address = address;
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	class Student extends Person {
		private String school;

		public Student(String address, String name, String school) {
			super(address, name);
			this.school = school;
		}

		public String getSchool() {
			return school;
		}

		public void setSchool(String school) {
			this.school = school;
		}

	}
}
