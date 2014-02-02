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

package org.neodatis.odb.test.serialization;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.impl.core.layers.layer2.meta.serialization.Serializer;
import org.neodatis.odb.test.ODBTest;

public class TestSerialization extends ODBTest {

	public void testAtomicNativeCollectionString() throws Exception {
		String s1 = "olá chico";
		AtomicNativeObjectInfo anoi = null;

		anoi = new AtomicNativeObjectInfo(s1, ODBType.STRING_ID);

		String s = Serializer.getInstance().toString(anoi);
		// println(s);
		AtomicNativeObjectInfo anoi2 = (AtomicNativeObjectInfo) Serializer.getInstance().fromOneString(s);

		assertEquals(anoi, anoi2);
	}

	public void testAtomicNativeCollectionDate() throws Exception {
		Date date = new Date();
		AtomicNativeObjectInfo anoi = null;

		anoi = new AtomicNativeObjectInfo(date, ODBType.DATE_ID);

		String s = Serializer.getInstance().toString(anoi);
		// println(s);
		AtomicNativeObjectInfo anoi2 = (AtomicNativeObjectInfo) Serializer.getInstance().fromOneString(s);

		assertEquals(anoi, anoi2);
	}

	public void testAtomicNativeCollectionBigDecimal() throws Exception {
		BigDecimal bd = new BigDecimal("123456789.987654321");
		AtomicNativeObjectInfo anoi = null;

		anoi = new AtomicNativeObjectInfo(bd, ODBType.BIG_DECIMAL_ID);

		String s = Serializer.getInstance().toString(anoi);
		// println(s);
		AtomicNativeObjectInfo anoi2 = (AtomicNativeObjectInfo) Serializer.getInstance().fromOneString(s);

		assertEquals(anoi, anoi2);
	}

	public void testAtomicNativeCollectionInt() throws Exception {
		int i = 123456789;
		AtomicNativeObjectInfo anoi = null;

		anoi = new AtomicNativeObjectInfo(new Integer(i), ODBType.INTEGER_ID);

		String s = Serializer.getInstance().toString(anoi);
		// println(s);
		AtomicNativeObjectInfo anoi2 = (AtomicNativeObjectInfo) Serializer.getInstance().fromOneString(s);

		assertEquals(anoi, anoi2);
	}

	public void testAtomicNativeCollectionDouble() throws Exception {
		double d = 123456789.789456123;
		AtomicNativeObjectInfo anoi = null;

		anoi = new AtomicNativeObjectInfo(new Double(d), ODBType.DOUBLE_ID);

		String s = Serializer.getInstance().toString(anoi);
		// println(s);
		AtomicNativeObjectInfo anoi2 = (AtomicNativeObjectInfo) Serializer.getInstance().fromOneString(s);

		assertEquals(anoi, anoi2);
	}

	public void testRegExp() {
		// println("start");
		String token = "A;B;[C;D];E";
		// (*)&&^(*^)
		String pattern = "[\\[*\\]]";
		Pattern p = Pattern.compile(pattern);

		String[] array = token.split(pattern);
		Matcher m = p.matcher(token);

		// println(token);
		// println(m.groupCount());
		for (int i = 0; i < array.length; i++) {
			// println((i+1)+"="+array[i]);
		}
	}

}
