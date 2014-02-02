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
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.transaction.CrossSessionCache;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestCrossSessionCacheMultiThread extends ODBTest {
	
	public void test1() throws Exception {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		String baseName = getBaseName();

		ODBServer server = ODBFactory.openServer(10000);
		
		ODB odb = server.openClient(baseName);
		
		int size = 10;
		Function[] functionArray = new Function[size];
		
		for(int i=0;i<size;i++){
			functionArray[i] = new Function("function"+i);
			odb.store(functionArray[i]);
		}
		odb.close();
		
		for(int i=0;i<size;i++){
			ThreadOdbUpdate t = new ThreadOdbUpdate(server,baseName,functionArray[i]);
			t.start();
		}
		
		Thread.sleep(2000);
		
		odb = server.openClient(baseName);
		Objects<Function> functions = odb.getObjects(Function.class);
		
		assertEquals(size, functions.size());
		CrossSessionCache.clearAll();
		int j=0;
		while(functions.hasNext()){
			Function f = functions.next();
			assertEquals("function"+j+"-updated from thread Thread-" + j, f.getName());
			j++;
		}
	}
}
