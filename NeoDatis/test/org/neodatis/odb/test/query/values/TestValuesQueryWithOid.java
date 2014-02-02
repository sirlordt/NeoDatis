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
package org.neodatis.odb.test.query.values;

import java.io.IOException;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Values;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestValuesQueryWithOid extends ODBTest {

	public void test1() throws IOException, Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Handler handler = new Handler();
		for (int i = 0; i < 10; i++) {
			handler.addParameter(new Parameter("test " + i, "value " + i));
		}
		OID oid = odb.store(handler);
		odb.close();

		odb = open(baseName);
		Values values = odb.getValues(new ValuesCriteriaQuery(Handler.class, oid).field("parameters").sublist("parameters", "sub1", 1, 5,
				true).sublist("parameters", "sub2", 1, 10).size("parameters", "size"));

		println(values);
		ObjectValues ov = values.nextValues();

		List fulllist = (List) ov.getByAlias("parameters");
		assertEquals(10, fulllist.size());
		Long size = (Long) ov.getByAlias("size");
		assertEquals(10, size.longValue());

		Parameter p = (Parameter) fulllist.get(0);
		assertEquals("value 0", p.getValue());

		Parameter p2 = (Parameter) fulllist.get(9);
		assertEquals("value 9", p2.getValue());

		List sublist = (List) ov.getByAlias("sub1");
		assertEquals(5, sublist.size());
		p = (Parameter) sublist.get(0);
		assertEquals("value 1", p.getValue());

		p2 = (Parameter) sublist.get(4);
		assertEquals("value 5", p2.getValue());

		List sublist2 = (List) ov.getByAlias("sub2");
		assertEquals(9, sublist2.size());

		odb.close();

	}

}
