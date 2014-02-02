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
package org.neodatis.odb.test.io;

import java.io.File;
import java.io.FileFilter;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.wrappers.OdbSystem;

public class TestFileSystemInterface3 extends ODBTest {

	/**
	 * Test if the transaction file is created in the same directory if database
	 * 
	 */
	public void test1() {
		String baseName = getBaseName();
		String userDirectory = OdbSystem.getProperty("user.dir");
		println("User directory = " + userDirectory);
		println("base name = " + baseName);

		ODB odb = open(baseName);
		odb.store(new Function("test"));

		File f = new File(userDirectory);
		File[] files = f.listFiles(new MyFilter(baseName));

		assertEquals(0, files.length);
		odb.close();
		deleteBase(baseName);

	}

}

class MyFilter implements FileFilter {
	private String prefix;

	public MyFilter(String prefix) {
		super();
		this.prefix = prefix;
	}

	public boolean accept(File pathname) {
		return pathname.getAbsolutePath().indexOf(prefix) != -1;
	}

}