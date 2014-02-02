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
package org.neodatis.odb.core.layers.layer2.meta;

import java.util.Map;

import org.neodatis.odb.OID;

/**
 * A meta representation of an Array
 * 
 * @author osmadja
 * 
 */
public class ArrayObjectInfo extends GroupObjectInfo {
	private String realArrayComponentClassName;
	private int componentTypeId;

	public ArrayObjectInfo(Object[] array) {
		super(array, ODBType.ARRAY_ID);
		realArrayComponentClassName = ODBType.DEFAULT_ARRAY_COMPONENT_CLASS_NAME;
	}

	public ArrayObjectInfo(Object[] array, ODBType type, int componentId) {
		super(array, type);
		realArrayComponentClassName = ODBType.DEFAULT_ARRAY_COMPONENT_CLASS_NAME;
		componentTypeId = componentId;
	}

	public Object[] getArray() {
		return (Object[]) theObject;
	}

	public String toString() {
		if (theObject != null) {
			StringBuffer buffer = new StringBuffer();
			Object[] array = getArray();
			int length = array.length;
			buffer.append("[").append(length).append("]=(");
			for (int i = 0; i < length; i++) {
				if (i != 0) {
					buffer.append(",");
				}
				buffer.append(array[i]);
			}
			buffer.append(")");
			return buffer.toString();

		}
		return "null array";
	}

	public boolean isArrayObject() {
		return true;
	}

	public String getRealArrayComponentClassName() {
		return realArrayComponentClassName;
	}

	public void setRealArrayComponentClassName(String realArrayComponentClassName) {
		this.realArrayComponentClassName = realArrayComponentClassName;
	}

	public int getArrayLength() {
		return getArray().length;
	}

	public int getComponentTypeId() {
		return componentTypeId;
	}

	public void setComponentTypeId(int componentTypeId) {
		this.componentTypeId = componentTypeId;
	}

	public AbstractObjectInfo createCopy(Map<OID,AbstractObjectInfo> cache,  boolean onlyData){
		Object[] array = getArray();
		int length = array.length;

		AbstractObjectInfo[] aois = new AtomicNativeObjectInfo[length];

		for (int i = 0; i < length; i++) {
			AbstractObjectInfo aoi = (AbstractObjectInfo) array[i]; 
			aois[i] = aoi.createCopy(cache, onlyData);
		}
		
		ArrayObjectInfo arrayOfAoi = new ArrayObjectInfo(aois);
		arrayOfAoi.setRealArrayComponentClassName(realArrayComponentClassName);
		arrayOfAoi.setComponentTypeId(componentTypeId);
		return arrayOfAoi;
	}

}
