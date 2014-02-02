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

import java.io.UnsupportedEncodingException;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.mock.MockSession;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalFileSystemInterface;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestFileSystemInterface2 extends ODBTest {

	public void testReadWrite() throws Exception {
		deleteBase("testReadWrite.neodatis");
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY
				+ "testReadWrite.neodatis", true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(fsi.getLength(), false);

		for (int i = 0; i < 10000; i++) {
			fsi.writeInt(i, false, "int");

			long currentPosition = fsi.getPosition();
			if (i == 8000) {

				currentPosition = fsi.getPosition();
				fsi.useBuffer(false);
				// Using the for transaction method to avoid protected area
				// verification, check the setWritePosition method
				fsi.setWritePositionNoVerification(4, false);
				assertEquals(1, fsi.readInt());
				fsi.useBuffer(true);
				fsi.setWritePositionNoVerification(currentPosition, false);
			}
			if (i == 9000) {

				currentPosition = fsi.getPosition();
				fsi.useBuffer(false);
				fsi.setWritePositionNoVerification(8, false);
				fsi.writeInt(12, false, "int");
				fsi.useBuffer(true);
				fsi.setWritePositionNoVerification(currentPosition, false);
			}
		}
		fsi.setReadPosition(0);

		for (int i = 0; i < 10000; i++) {
			int j = fsi.readInt();
			if (i == 2) {
				assertEquals(12, j);
			} else {
				assertEquals(i, j);
			}
		}

		fsi.close();
		deleteBase("testReadWrite.neodatis");
	}

	public void testStringGetBytesWithoutEncoding() throws UnsupportedEncodingException {
		IByteArrayConverter byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();

		String test = "How are you my friend?";
		int size = 1000000;

		long t0 = OdbTime.getCurrentTimeInMs();
		// Execute with encoding
		for (int i = 0; i < size; i++) {
			byteArrayConverter.stringToByteArray(test, true, -1, true);
		}
		long t1 = OdbTime.getCurrentTimeInMs();
		// Execute without encoding
		for (int i = 0; i < size; i++) {
			// byteArrayConverter.stringToByteArray(test, false, -1, false);
		}
		long t2 = OdbTime.getCurrentTimeInMs();

		println("With Encoding=" + (t1 - t0) + " / Without Encoding=" + (t2 - t1));

	}

}
