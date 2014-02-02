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
package org.neodatis.odb.test.ee.index;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.index.IndexedObject;

public class TestIndex extends ODBTest {
	
	/** neodatisee
	 * 
	 * @throws Exception
	 */
	public void testIndexWillNullKey() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);
		base.close();

		base = open(baseName);
		IndexedObject io = new IndexedObject(null,1,null);
		try{
			base.store(io);
			fail("Exception should have been thrown because we NeoDatis can not compute index key with null values");
		}catch (Exception e) {
			// TODO: handle exception
		}

		base.close();
		base = open(baseName);
		Objects<IndexedObject> objects = base.getObjects(IndexedObject.class);
		assertEquals(0, objects.size());
		base.close();
		
		deleteBase(baseName);
	}
	public void testIndexWillPartialNullKey() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" , "duration"};
		clazz.addUniqueIndexOn("index1", indexFields, true);
		base.close();

		base = open(baseName);
		IndexedObject io = new IndexedObject(null,1,null);
		try{
			base.store(io);
			fail("Exception should have been thrown because we NeoDatis can not compute index key with null values");
		}catch (Exception e) {

		}

		base.close();
		base = open(baseName);
		Objects<IndexedObject> objects = base.getObjects(IndexedObject.class);
		assertEquals(0, objects.size());
		base.close();

		deleteBase(baseName);
	}
}
