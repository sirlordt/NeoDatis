
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

import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;


/**
 * Used to store informations about object changes at attribute level
 * @author osmadja
 *
 */
public class ChangedNativeAttributeAction implements ChangedAttribute{
	/** The old object meta representation: is case of no in place update*/
	private NonNativeObjectInfo oldNnoi;
	/** The new object meta representation: is case of no in place update*/
	private NonNativeObjectInfo newNoi;
	
	private long updatePosition;
    private NativeObjectInfo noiWithNewValue;
	private int recursionLevel;
	private String attributeName;
	/** This boolean value is set to true when original object is null, is this case there is no way to do in place update*/
	private boolean reallyCantDoInPlaceUpdate;
	
	public ChangedNativeAttributeAction(NonNativeObjectInfo oldNnoi, NonNativeObjectInfo newNnoi, long position, NativeObjectInfo newNoi, int recursionLevel,boolean canDoInPlaceUpdate,String attributeName) {
		this.oldNnoi = oldNnoi;
		this.newNoi = newNnoi;
		this.updatePosition = position;
		this.noiWithNewValue = newNoi;
		this.recursionLevel = recursionLevel;
		this.reallyCantDoInPlaceUpdate = canDoInPlaceUpdate;
		this.attributeName = attributeName;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("field : '").append(attributeName).append("' - update position=").append(updatePosition).append(" - new value=").append(noiWithNewValue).append(" - level=").append(recursionLevel);
		return buffer.toString();
	}

	public NativeObjectInfo getNoiWithNewValue() {
		return noiWithNewValue;
	}

    public int getRecursionLevel() {
		return recursionLevel;
	}

	public long getUpdatePosition() {
		return updatePosition;
	}
	
	public boolean reallyCantDoInPlaceUpdate() {
		return reallyCantDoInPlaceUpdate;
	}

	public boolean inPlaceUpdateIsGuaranteed(){
		 return !reallyCantDoInPlaceUpdate && noiWithNewValue.isAtomicNativeObject() && noiWithNewValue.getOdbTypeId()!=ODBType.STRING_ID;
	}
    public boolean isString(){
        return noiWithNewValue.getOdbTypeId()==ODBType.STRING_ID;
        
    }

	public NonNativeObjectInfo getOldNnoi() {
		return oldNnoi;
	}

	public NonNativeObjectInfo getNewNoi() {
		return newNoi;
	}
}
