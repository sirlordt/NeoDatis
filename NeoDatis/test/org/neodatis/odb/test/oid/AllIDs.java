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
package org.neodatis.odb.test.oid;

import java.util.List;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class AllIDs extends ODBTest {
	public static String FILE_NAME = ODBTest.DIRECTORY + "ids.neodatis";

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase(FILE_NAME);
		IBaseIdentification parameter = new IOFileParameter(ODBTest.DIRECTORY + FILE_NAME, true, null, null);
		IStorageEngine engine = OdbConfiguration.getCoreProvider().getClientStorageEngine(parameter);
		Function function1 = new Function("login");
		engine.store(function1);
		Function function2 = new Function("login2");
		engine.store(function2);
		engine.commit();
		engine.close();

		engine = OdbConfiguration.getCoreProvider().getClientStorageEngine(parameter);
		List l = engine.getAllObjectIds();
		assertEquals(2, l.size());
		engine.close();

		deleteBase(FILE_NAME);

	}
}
