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

import java.io.IOException;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.test.ODBTest;

public class TestReservingSpace extends ODBTest {

	public void testSize() throws Exception {
		deleteBase("writing.neodatis");
		deleteBase("writing");
		deleteBase("reserving.neodatis");
		deleteBase("reserving");

		IStorageEngine engine1 = OdbConfiguration.getCoreProvider().getClientStorageEngine(
				new IOFileParameter(ODBTest.DIRECTORY + "writing.neodatis", true, null, null));
		IStorageEngine engine2 = OdbConfiguration.getCoreProvider().getClientStorageEngine(
				new IOFileParameter(ODBTest.DIRECTORY + "reserving.neodatis", true, null, null));
		IFileSystemInterface writingFsi = engine1.getObjectWriter().getFsi();
		IFileSystemInterface reservingFsi = engine2.getObjectWriter().getFsi();

		assertEquals(writingFsi.getLength(), reservingFsi.getLength());

		write(writingFsi, false);
		write(reservingFsi, true);

		assertEquals(writingFsi.getLength(), reservingFsi.getLength());
		engine1.commit();
		engine1.close();
		engine2.commit();
		engine2.close();
		deleteBase("writing.neodatis");
		deleteBase("reserving.neodatis");
	}

	public void write(IFileSystemInterface fsi, boolean writeInTransaction) throws IOException {
		fsi.writeInt(1, writeInTransaction, "1");

	}
}
