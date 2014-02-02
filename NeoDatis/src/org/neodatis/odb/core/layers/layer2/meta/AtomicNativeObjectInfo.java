
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



/** To keep info about a native object like int,char, long, Does not include array or collection
 * 
 * @author olivier s
 *
 */
public class AtomicNativeObjectInfo extends NativeObjectInfo implements Comparable{

	public AtomicNativeObjectInfo(Object object,int odbTypeId) {
		super(object,odbTypeId);
	}

	public String toString() {
		if(theObject!=null){
			return theObject.toString();
		}
		return "null";
	}

	public boolean equals(Object obj) {
		if(obj==null || ! (obj instanceof AtomicNativeObjectInfo)){
			return false;
		}
		AtomicNativeObjectInfo noi = (AtomicNativeObjectInfo) obj;
		if(theObject==noi.getObject()){
			return true;
		}
		return theObject.equals(noi.getObject());
	}

	public boolean isAtomicNativeObject() {
		return true;
	}

	public AbstractObjectInfo createCopy(Map<OID,AbstractObjectInfo> cache,  boolean onlyData){
		return new AtomicNativeObjectInfo(theObject,odbTypeId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		AtomicNativeObjectInfo anoi = (AtomicNativeObjectInfo) o;
		Comparable c2 = (Comparable) anoi.getObject();
		Comparable c1 = (Comparable) theObject;
		return c1.compareTo(c2);
	}

	
}
