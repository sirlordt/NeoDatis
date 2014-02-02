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
package org.neodatis.odb.test.arraycollectionmap;

import java.math.BigDecimal;
import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.arraycollectionmap.PlayerWithArray;

public class TestArray2D extends ODBTest {

	

	/**
	 * 2D Array
	 * 
	 * @throws Exception
	 */
	public void testArrayWith2Dimensions() throws Exception {
		if (!testNewFeature) {
			return;
		}
		ODB odb = null;
		int size = 50;
		String baseName = getBaseName();
		try {
			odb = open(baseName);
			Integer[][] array = new Integer[size][size];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					array[i][j] = new Integer(i * j);
				}
			}

			ObjectWith2DimensionsArrayOfInteger owna = new ObjectWith2DimensionsArrayOfInteger("t1", array);
			odb.store(owna);
			odb.close();

			odb = open(baseName);
			Objects l = odb.getObjects(ObjectWith2DimensionsArrayOfInteger.class);
			ObjectWith2DimensionsArrayOfInteger owna2 = (ObjectWith2DimensionsArrayOfInteger) l.getFirst();
		} catch (Exception e) {
			if (odb != null) {
				odb.rollback();
				odb = null;
			}
			fail("2 D Array not supported");
		} finally {
			if (odb != null) {
				odb.close();
			}
			deleteBase(baseName);
		}
	}
}
