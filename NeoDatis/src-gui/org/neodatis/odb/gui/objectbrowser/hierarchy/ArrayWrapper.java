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
package org.neodatis.odb.gui.objectbrowser.hierarchy;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public class ArrayWrapper implements Wrapper {
	private ArrayObjectInfo aoi;
	private String name;
	private NonNativeObjectInfo parent;

	public ArrayWrapper(NonNativeObjectInfo nnoi, String name, ArrayObjectInfo arrayObjectInfo) {
		this.parent = nnoi;
		this.aoi = arrayObjectInfo;
		this.name = name;
	}

	public String toString() {
		if (!aoi.isNull()) {
			return "Array [" + aoi.getArrayLength() + "] : " + name;
		}
		return "NULL Array";

	}

	public Object getArray() {
		return aoi.getObject();
	}

	public int getArraySize() {
		return aoi.getArrayLength();
	}

	public AbstractObjectInfo getObject() {
		return aoi;
	}

	/**
	 * @return the parent
	 */
	public NonNativeObjectInfo getParent() {
		return parent;
	}

}
