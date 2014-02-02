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
package org.neodatis.odb.test.performance;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.test.ODBTest;

public class TestByteConversion extends ODBTest {
	static IByteArrayConverter byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();
	public static final int SIZE = 1000;

	public static final int SIZE0 = 1000;

	public void testPerfLong() {
		int size = 10000;
		long l = 474367843;

		long start1 = System.currentTimeMillis();
		for (int j = 0; j < size; j++) {
			byte b[] = new byte[8];
			int i, shift;
			for (i = 0, shift = 56; i < 8; i++, shift -= 8) {
				b[i] = (byte) (0xFF & (l >> shift));
			}

		}
		long end1 = System.currentTimeMillis();

		long start2 = System.currentTimeMillis();
		for (int j = 0; j < size; j++) {
			byte b[] = ByteBuffer.allocate(8).putLong(l).array();
		}
		long end2 = System.currentTimeMillis();

		println("Standard conversion = " + (end1 - start1));
		println("NIO conversion = " + (end2 - start2));
	}

	public void testLong() {
		long l1 = 785412;
		byte[] b = byteArrayConverter.longToByteArray(l1);
		long l2 = byteArrayConverter.byteArrayToLong(b, 0);
		assertEquals(l1, l2);

		l1 = Long.MAX_VALUE;
		b = byteArrayConverter.longToByteArray(l1);
		l2 = byteArrayConverter.byteArrayToLong(b, 0);
		assertEquals(l1, l2);

		l1 = Long.MIN_VALUE;
		b = byteArrayConverter.longToByteArray(l1);
		l2 = byteArrayConverter.byteArrayToLong(b, 0);
		assertEquals(l1, l2);
	}

	public void testInt() {

		int l1 = 785412;
		byte[] b = byteArrayConverter.intToByteArray(l1);
		int l2 = byteArrayConverter.byteArrayToInt(b, 0);

		assertEquals(l1, l2);
	}

	public void testFloat() {

		float l1 = (float) 785412.4875;
		byte[] b2 = byteArrayConverter.floatToByteArray(l1);
		float l2 = byteArrayConverter.byteArrayToFloat(b2);
		assertEquals(l1, l2, 0);
	}

	public void testDouble() {

		double l1 = 785412.4875;
		byte[] b2 = byteArrayConverter.doubleToByteArray(l1);
		double l2 = byteArrayConverter.byteArrayToDouble(b2);
		assertEquals(l1, l2, 0);
	}

	public void testBoolean() throws IOException {

		boolean b1 = true;
		byte[] b2 = byteArrayConverter.booleanToByteArray(b1);
		boolean b3 = byteArrayConverter.byteArrayToBoolean(b2, 0);
		assertEquals(b1, b3);
		b1 = false;
		b2 = byteArrayConverter.booleanToByteArray(b1);
		b3 = byteArrayConverter.byteArrayToBoolean(b2, 0);
		assertEquals(b1, b3);
	}

	public void testChar() throws IOException {

		char c = '\u00E1';
		byte[] b2 = byteArrayConverter.charToByteArray(c);
		char c1 = byteArrayConverter.byteArrayToChar(b2);
		assertEquals(c, c1);
	}

	public void testShort() throws IOException {

		short s = 4598;
		byte[] b2 = byteArrayConverter.shortToByteArray(s);
		short s2 = byteArrayConverter.byteArrayToShort(b2);
		// assertEquals(s,s2);

		s = 10000;
		b2 = byteArrayConverter.shortToByteArray(s);
		s2 = byteArrayConverter.byteArrayToShort(b2);
		assertEquals(s, s2);

		s = Short.MAX_VALUE;
		b2 = byteArrayConverter.shortToByteArray(s);
		s2 = byteArrayConverter.byteArrayToShort(b2);
		assertEquals(s, s2);

		s = Short.MIN_VALUE;
		b2 = byteArrayConverter.shortToByteArray(s);
		s2 = byteArrayConverter.byteArrayToShort(b2);
		assertEquals(s, s2);
	}

	public void testString() throws IOException {

		String s = "test1";
		byte[] b2 = byteArrayConverter.stringToByteArray(s, true, -1, true);
		String s2 = byteArrayConverter.byteArrayToString(b2, true, true);
		assertEquals(s, s2);
	}
	
	
	public void testBigDecimal1() throws IOException {

		BigDecimal bd1 = new BigDecimal(10);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}

	public void testBigDecimal2() throws IOException {

		BigDecimal bd1 = new BigDecimal(10.123456789123456789);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}

	public void testBigDecimal3() throws IOException {

		BigDecimal bd1 = new BigDecimal(0);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}

	public void testBigDecimal4() throws IOException {

		BigDecimal bd1 = new BigDecimal(10);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}

	public void testBigDecimal5() throws IOException {

		BigDecimal bd1 = new BigDecimal(0.000);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}
	public void testBigDecimal6() throws IOException {

		BigDecimal bd1 = new BigDecimal(0.000000000000000123456789);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}
	public void testBigDecimal7() throws IOException {

		BigDecimal bd1 = new BigDecimal(-1);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}
	public void testBigDecimal8() throws IOException {

		BigDecimal bd1 = new BigDecimal(-123456789);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}
	
	public void testBigDecimal9() throws IOException {

		BigDecimal bd1 = new BigDecimal(-0.000000000000000000000000000000123456789);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}
	public void testBigDecimal10() throws IOException {

		BigDecimal bd1 = new BigDecimal(123456789123456789123456789.123456789123456789);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}
	public void testBigDecimal11() throws IOException {

		BigDecimal bd1 = new BigDecimal(-0.00000);
		byte[] b2 = byteArrayConverter.bigDecimalToByteArray(bd1,true);
		BigDecimal bd2 = byteArrayConverter.byteArrayToBigDecimal(b2,true);
		assertEquals(bd1,bd2);
	}

}
