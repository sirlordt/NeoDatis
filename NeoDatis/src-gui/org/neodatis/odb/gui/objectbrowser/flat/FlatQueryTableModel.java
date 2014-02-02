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
package org.neodatis.odb.gui.objectbrowser.flat;

import java.util.Collection;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.ObjectInfoUtil;

public class FlatQueryTableModel extends DefaultTableModel {

	private List attributeList;
	private List valueList;

	/**
	 * 
	 * @param engine
	 * @param fullClassName
	 * @param objectInfoValues
	 *            A list of AbstractObjectInfo
	 */
	public FlatQueryTableModel(IStorageEngine engine, String fullClassName, Collection objectInfoValues) {
		super();
		init(engine, fullClassName, objectInfoValues);
	}

	private void init(IStorageEngine engine, String fullClassName, Collection objectInfoValues) {
		attributeList = ObjectInfoUtil.buildAttributeNameList(engine.getSession(true).getMetaModel().getClassInfo(fullClassName, true));
		valueList = ObjectInfoUtil.buildValueList(engine.getSession(true).getMetaModel().getClassInfo(fullClassName, true),
				objectInfoValues);
	}

	public String getColumnName(int column) {
		String name = attributeList.get(column).toString();

		if (name.length() > 20) {
			int index = name.lastIndexOf(".");
			name = "... " + name.substring(index + 1);
		}
		return name;
	}

	public int getColumnCount() {
		if (attributeList == null) {
			return 0;
		}
		return attributeList.size();
	}

	public int getRowCount() {
		if (valueList == null) {
			return 0;
		}
		return valueList.size();
	}

	public Object getValueAt(int row, int column) {
		List l = (List) valueList.get(row);
		if (column >= l.size()) {
			return "?";
		}
		Object o = l.get(column);
		if (o instanceof String) {
			return o;
		}
		AbstractObjectInfo aoi = (AbstractObjectInfo) o;
		if (aoi.isArrayObject()) {
			return arrayRepresentation((ArrayObjectInfo) aoi);
		}
		return o;
	}

	private Object arrayRepresentation(ArrayObjectInfo aoi) {
		StringBuffer buffer = new StringBuffer();
		AbstractObjectInfo element = null;
		for (int i = 0; i < aoi.getArrayLength(); i++) {
			if (i != 0) {
				buffer.append(",");
			}
			buffer.append(i).append("=");
			element = (AbstractObjectInfo) aoi.getArray()[i];
			if (element.isNull()) {
				buffer.append("null");
			} else {
				buffer.append(element);
			}
		}
		return buffer.toString();
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
	}

	/**
	 * @return Returns the attributeList.
	 */
	public List getAttributeList() {
		return attributeList;
	}

	/**
	 * @return Returns the valueList.
	 */
	public List getValueList() {
		return valueList;
	}

}
