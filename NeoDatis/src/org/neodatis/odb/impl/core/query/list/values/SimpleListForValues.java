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
package org.neodatis.odb.impl.core.query.list.values;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.impl.core.query.list.objects.SimpleList;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * A simple list to hold query result for Object Values API. It is used when no index and no order by is used and inMemory = true
 * @author osmadja
 *
 */
public class SimpleListForValues extends SimpleList<ObjectValues> implements Values {

	public SimpleListForValues() {
		super();
	}

	public SimpleListForValues(int initialCapacity) {
		super(initialCapacity);
	}

	public ObjectValues nextValues() {
		return next();
	}

	public boolean addWithKey(OdbComparable key, ObjectValues object) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED.addParameter("addWithKey"));
	}

	public boolean addWithKey(int key, ObjectValues object) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_IMPLEMENTED.addParameter("addWithKey"));
	}
}
