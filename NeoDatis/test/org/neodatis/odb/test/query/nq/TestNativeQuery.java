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
package org.neodatis.odb.test.query.nq;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.ClassWithArrayOfBoolean;

public class TestNativeQuery extends ODBTest {

	public void test1() throws Exception {
		if (!isLocal || !useSameVmOptimization) {
			// native must be serializable to be executed in cs mode
			return;
		}

		String baseName = getBaseName();
		ODB odb = open(baseName);

		Boolean[] bbs1 = new Boolean[2];
		bbs1[0] = Boolean.TRUE;
		bbs1[1] = Boolean.FALSE;

		boolean[] bbs2 = new boolean[2];
		bbs2[0] = Boolean.TRUE.booleanValue();
		bbs2[1] = Boolean.FALSE.booleanValue();

		ClassWithArrayOfBoolean o = new ClassWithArrayOfBoolean("test", bbs1, bbs2);

		odb.store(o);
		odb.close();

		odb = open(baseName);
		IQuery query = new SimpleNativeQuery() {
			public boolean match(ClassWithArrayOfBoolean o) {
				return true;
			}
		};
		Objects objects = odb.getObjects(query);

		assertEquals(1, objects.size());

		ClassWithArrayOfBoolean o2 = (ClassWithArrayOfBoolean) objects.getFirst();
		assertEquals("test", o2.getName());
		assertEquals(Boolean.TRUE, o2.getBools1()[0]);
		assertEquals(Boolean.FALSE, o2.getBools1()[1]);

		assertEquals(Boolean.TRUE.booleanValue(), o2.getBools2()[0]);
		assertEquals(Boolean.FALSE.booleanValue(), o2.getBools2()[1]);

	}

}
