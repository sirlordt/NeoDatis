
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
package org.neodatis.odb.core.layers.layer2.meta.compare;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;

/**
Used to store informations about object changes
*/
public class ChangedObjectInfo {
	private ClassInfo oldCi;
	private ClassInfo newCi;
	private int fieldIndex;
	private AbstractObjectInfo oldValue;
	private AbstractObjectInfo newValue;
	private String message;
	private int objectRecursionLevel;

	public ChangedObjectInfo(ClassInfo oldCi,ClassInfo newCi, int fieldIndex, AbstractObjectInfo oldValue, AbstractObjectInfo newValue,int objectRecursionLevel) {
		this(oldCi,newCi,fieldIndex,oldValue,newValue,null,objectRecursionLevel);
	}
	public ChangedObjectInfo(ClassInfo oldCi,ClassInfo newCi, int fieldIndex, AbstractObjectInfo oldValue, AbstractObjectInfo newValue,String message,int objectRecursionLevel) {
		super();
		this.oldCi = oldCi;
		this.newCi = newCi;
		this.fieldIndex = fieldIndex;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.message = message;
		this.objectRecursionLevel = objectRecursionLevel;
	}
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if(message!=null){
			buffer.append(message).append(" | ");
		}
		if(oldCi.getId()!=newCi.getId()){
			buffer.append("old class=").append(oldCi.getFullClassName()).append(" | new class=").append(newCi.getFullClassName());
		}else{
			buffer.append("class=").append(oldCi.getFullClassName());	
		}
		buffer.append(" | field=").append(oldCi.getAttributeInfo(fieldIndex).getName());
		buffer.append(" | old=").append(oldValue.toString()).append(" | new=").append(newValue.toString());
		buffer.append(" | obj. hier. level=").append(objectRecursionLevel);
		return buffer.toString();	
	}

}
