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

import java.text.ParseException;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.impl.tool.ObjectTool;

public class NativeAttributeValueWrapper implements Wrapper {
	private String name;
	private Object value;
	private long attributePosition;
	private NonNativeObjectInfo parent;

	public NativeAttributeValueWrapper(NonNativeObjectInfo nnoi, String name, Object value, long position) {
		this.parent = nnoi;
		this.name = name;
		this.value = value;
		this.attributePosition = position;
	}

	public String toString() {
		try {
			if (value == null || value instanceof NullNativeObjectInfo || value instanceof NonNativeNullObjectInfo) {
				value = "null";
			}
			if (value instanceof String) {
				// System.out.println("String value = " + value + " / name = "+
				// name +" / ci=" + parent);
				return String.valueOf(value);
			}
			if (value instanceof AtomicNativeObjectInfo) {
				AtomicNativeObjectInfo anoi = (AtomicNativeObjectInfo) value;
				return name + " = " + ObjectTool.atomicNativeObjectToString(anoi, ObjectTool.ID_CALLER_IS_ODB_EXPLORER);
			}

			if (value instanceof EnumNativeObjectInfo) {
				EnumNativeObjectInfo enoi = (EnumNativeObjectInfo) value;
				return name + " = " + enoi.getObject().toString() + " (enum)";
			}
			return String.format("%s: Unable to retrieve the value (%s, type=%s)",name,value.toString(),value.getClass().getName());

		} catch (Throwable e) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Error while reading field " + name + ":" + e.getMessage());
			return buffer.toString();
		}
	}

	public String objectToString() {
		if (value == null || value instanceof NullNativeObjectInfo) {
			value = "null";
		}
		AtomicNativeObjectInfo anoi = (AtomicNativeObjectInfo) value;
		return ObjectTool.atomicNativeObjectToString(anoi, ObjectTool.ID_CALLER_IS_ODB_EXPLORER);
	}

	/**
	 * @return Returns the attributePosition.
	 */
	public long getAttributePosition() {
		return attributePosition;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}

	public void setNewValue(String newValue) throws NumberFormatException, ParseException {
		AtomicNativeObjectInfo anoi = (AtomicNativeObjectInfo) value;
		int odbTypeId = anoi.getOdbTypeId();
		Object o = ObjectTool.stringToObject(odbTypeId, newValue, ObjectTool.ID_CALLER_IS_ODB_EXPLORER);
		anoi.setObject(o);
	}

	public AbstractObjectInfo getObject() {
		return (AbstractObjectInfo) value;
	}

	/**
	 * @return the parent
	 */
	public NonNativeObjectInfo getParent() {
		return parent;
	}

}
