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
package org.neodatis.odb.test.refactoring;

import java.util.Iterator;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;

public class Result {

	public static int getNbBadTests() throws Exception {
		ByteCodeTest t = new ByteCodeTest();
		ODB odb = null;
		try {
			odb = t.openResultDb();
			Objects objects = odb.getObjects(TestResult.class);

			TestResult tr = (TestResult) objects.getFirst();
			System.out.println("Result : " + tr.getNbGoodTests() + " Tests OK, " + tr.getNbBadTests() + " failures");
			System.out.println();
			Iterator iterator = tr.getTests().keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = (String) tr.getTests().get(key);
				System.out.println(key + " : " + value);
			}
			return tr.getNbBadTests();
		} finally {
			if (odb != null) {
				odb.close();
			}
		}

	}

	public static void main(String[] args) throws Exception {

		String action = args[0];
		System.out.println("action=" + action.length() + " = ");
		System.out.println("'" + action + "'");
		ByteCodeTest t = new ByteCodeTest();
		if (action.startsWith("start")) {
			t.resetResultDb();
			System.out.println("File " + ByteCodeTest.RESULT_FILE_NAME + " deleted");
			System.exit(0);
		}

		if (action.startsWith("end")) {
			ODB odb = null;
			try {
				odb = t.openResultDb();
				Objects objects = odb.getObjects(TestResult.class);

				TestResult tr = (TestResult) objects.getFirst();
				System.out.println("Result : " + tr.getNbGoodTests() + " Tests OK, " + tr.getNbBadTests() + " failures");
				System.out.println();
				Iterator iterator = tr.getTests().keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					String value = (String) tr.getTests().get(key);
					System.out.println(key + " : " + value);
				}
			} finally {
				if (odb != null) {
					odb.close();
				}
			}
			System.exit(0);
		}
		System.out.println("Exit 1");
		System.exit(1);

	}
}
