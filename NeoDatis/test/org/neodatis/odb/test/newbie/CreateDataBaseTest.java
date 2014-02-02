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
package org.neodatis.odb.test.newbie;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

/**
 * It is just a simple test to help the newbies
 * 
 * @author mayworm at <xmpp://mayworm@gmail.com>
 * 
 */
public class CreateDataBaseTest extends ODBTest {
	private static final String NEWBIE_ODB = "newbie.neodatis";

	/**
	 * Test if a new database could be created
	 */
	public void testCreateDataBase() {
		try {
			deleteBase(NEWBIE_ODB);
			ODB odb = open(NEWBIE_ODB);
			odb.close();
			boolean existFile = IOUtil.existFile(DIRECTORY + NEWBIE_ODB);
			assertTrue("ODB data file couldn't created", existFile);
			deleteBase(NEWBIE_ODB);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
