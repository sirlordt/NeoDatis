
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

import java.io.Serializable;
import java.util.Map;

import org.neodatis.odb.OID;




/** To keep meta informations about an object
 * 
 * @author olivier smadja
 *
 */
public abstract class AbstractObjectInfo implements Serializable{
	/** The Type Id of the object*/
	protected int odbTypeId;
	/** The Type of the object*/
	protected ODBType odbType;
	/** The position of the object*/
	protected long position;
	
	public AbstractObjectInfo(int typeId){
		this.odbTypeId = typeId;
	}
	public AbstractObjectInfo(ODBType type){
		if(type!=null){
			this.odbTypeId = type.getId();
		}
		this.odbType = type;
	}
	public boolean isNative() {
		return isAtomicNativeObject()|| isArrayObject() || isCollectionObject() || isMapObject();
	}
	
	public boolean isGroup(){
		return isCollectionObject()||isMapObject()||isArrayObject();
	}
	
    public boolean isNull() {
		return getObject()==null;
	}
	public abstract Object getObject();

	public abstract void setObject(Object object);
	public int getOdbTypeId() {
		return odbTypeId;
	}
	public void setOdbTypeId(int odbTypeId) {
		this.odbTypeId = odbTypeId;
	}
	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}
	public ODBType getOdbType() {
		if(odbType==null){
			odbType = ODBType.getFromId(odbTypeId);
		}
		return odbType;
	}
	public void setOdbType(ODBType odbType) {
		this.odbType = odbType;
	}
	
	public boolean isNonNativeObject(){
		return false;
	}
	public boolean isAtomicNativeObject(){
		return false;
	}
	
	public boolean isCollectionObject(){
		return false;
	}
	public boolean isMapObject(){
		return false;
	}
	public boolean isArrayObject(){
		return false;
	}
	public boolean isDeletedObject(){
		return false;
	}
    public boolean isObjectReference(){
        return false;
    }

	public boolean isEnumObject(){
		return false;
	}

    public abstract AbstractObjectInfo createCopy(Map<OID,AbstractObjectInfo> cache,  boolean onlyData);
}
