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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.mock.MockSession;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalFileSystemInterface;
import org.neodatis.odb.impl.core.transaction.DefaultWriteAction;
import org.neodatis.odb.test.ODBTest;

public class TestFileSystemInterface1 extends ODBTest {

	public void testByte() throws Exception {
		byte b = 127;
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testByte.neodatis",
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeByte(b, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testByte.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		byte b2 = fsi.readByte();
		assertEquals(b, b2);
		fsi.close();
		deleteBase("testByte.neodatis");

	}

	public void testInt() throws Exception {
		int i = 259998;
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testInt.neodatis",
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeInt(i, false, "i");
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testInt.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		int i2 = fsi.readInt();
		assertEquals(i, i2);
		fsi.close();
		deleteBase("testInt.neodatis");

	}

	public void testChar() throws Exception {
		char c = '\u00E1';
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testChar.neodatis",
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeChar(c, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testChar.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		char c2 = fsi.readChar();
		assertEquals(c, c2);
		fsi.close();
		deleteBase("testChar.neodatis");

	}

	public void testShort() throws Exception {
		short s = 4598;
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testShort.neodatis",
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeShort(s, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testShort.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		short s2 = fsi.readShort();
		assertEquals(s, s2);
		fsi.close();
		deleteBase("testShort.neodatis");
	}

	public void testBoolean() throws Exception {
		boolean b1 = true;
		boolean b2 = false;
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"),
				ODBTest.DIRECTORY + "testBoolean.neodatis", true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeBoolean(b1, false);
		fsi.writeBoolean(b2, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testBoolean.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		boolean b11 = fsi.readBoolean();
		boolean b22 = fsi.readBoolean();
		assertEquals(b1, b11);
		assertEquals(b2, b22);
		fsi.close();
		deleteBase("testBoolean.neodatis");
	}

	public void testLong() throws Exception {
		long i = 259999865;
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testLong.neodatis",
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeLong(i, false, "i", DefaultWriteAction.POINTER_WRITE_ACTION);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testLong.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		long i2 = fsi.readLong();
		assertEquals(i, i2);
		fsi.close();
		deleteBase("testLong.neodatis");
	}

	public void testString() throws Exception {
		String baseName = getBaseName();
		String s = "ola chico, como você está ??? eu estou bem até amanhã de manhã, áááá'";
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), baseName,
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeString(s, false, true);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), baseName, false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		String s2 = fsi.readString(true);
		fsi.close();
		deleteBase(baseName);
		assertEquals(s, s2);
		
		
	}

	public void testBigDecimal() throws Exception {
		BigDecimal bd = new BigDecimal("-128451.1234567899876543210");
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY
				+ "testBigDecimal.neodatis", true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeBigDecimal(bd, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testBigDecimal.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		BigDecimal bd2 = fsi.readBigDecimal();
		assertEquals(bd, bd2);
		fsi.close();
		deleteBase("testBigDecimal.neodatis");
	}

	public void testBigInteger() throws Exception {
		BigInteger bd = new BigInteger("-128451");
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY
				+ "testBigDecimal.neodatis", true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeBigInteger(bd, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testBigDecimal.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		BigInteger bd2 = fsi.readBigInteger();
		assertEquals(bd, bd2);
		fsi.close();
		deleteBase("testBigDecimal.neodatis");
	}

	public void testFloat() throws Exception {
		float f = (float) 12544548.12454;
		IFileSystemInterface fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testFloat.neodatis",
				true, true, OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setWritePosition(0, false);
		fsi.writeFloat(f, false);
		fsi.close();

		fsi = new LocalFileSystemInterface("data", new MockSession("test"), ODBTest.DIRECTORY + "testFloat.neodatis", false, false,
				OdbConfiguration.getDefaultBufferSizeForData());
		fsi.setReadPosition(0);
		float f2 = fsi.readFloat();
		assertTrue(f == f2);
		fsi.close();
		deleteBase("testFloat.neodatis");
	}

}
