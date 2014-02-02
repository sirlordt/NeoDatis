
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





/** To keep info about a native instance
 * 
 * @author olivier s
 *
 */
public abstract class NativeObjectInfo extends AbstractObjectInfo{

	/** The object being represented*/
	protected Object theObject;
	
	public NativeObjectInfo(Object object,int odbTypeId) {
		super(odbTypeId);
		this.theObject = object;
	}
	public NativeObjectInfo(Object object,ODBType odbType) {
		super(odbType);
		this.theObject = object;
	}

	public String toString() {
		if(theObject!=null){
			return theObject.toString();
		}
		return "null";
	}


	public boolean equals(Object obj) {
		if(obj==null ){
			return false;
		}
		NativeObjectInfo noi = (NativeObjectInfo) obj;
		if(theObject==noi.getObject()){
			return true;
		}
		return theObject.equals(noi.getObject());
	}

	public boolean isNativeObject() {
		return true;
	}
	
	public Object getObject() {
		return theObject;
	}
	public void setObject(Object object) {
		this.theObject = object;
	}
}
