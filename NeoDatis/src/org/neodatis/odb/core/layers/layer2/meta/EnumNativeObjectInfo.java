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
 * Meta representation of an enum. Which is internally represented by a string :
 * Its name
 * 
 * @author osmadja
 * 
 */
public class EnumNativeObjectInfo extends NativeObjectInfo {

	private ClassInfo enumClassInfo;

	public EnumNativeObjectInfo(ClassInfo classInfo, String enumName) {
		super(enumName, ODBType.ENUM_ID);
		this.enumClassInfo = classInfo;
	}

	public String toString() {
		return getObject().toString();
	}

	public boolean isNull() {
		return getObject()==null;
	}

	public boolean isNative() {
		return true;
	}

	public boolean isEnumObject(){
		return true;
	}

	public AbstractObjectInfo createCopy(Map<OID, AbstractObjectInfo> cache, boolean onlyData) {
		return new EnumNativeObjectInfo(enumClassInfo, getObject()==null?null:getObject().toString());
	}
	public String getEnumName(){
		return getObject().toString();
	}

	public ClassInfo getEnumClassInfo() {
		return enumClassInfo;
	}

	public void setEnumClassInfo(ClassInfo enumClassInfo) {
		this.enumClassInfo = enumClassInfo;
	}

	
	
}
