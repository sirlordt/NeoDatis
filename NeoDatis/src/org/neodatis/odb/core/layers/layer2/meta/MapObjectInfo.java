
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

import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.tool.wrappers.map.OdbHashMap;


/**
 * Meta representation of a Map
 * @author osmadja
 *
 */
public class MapObjectInfo extends GroupObjectInfo {
	private String realMapClassName;
	public MapObjectInfo(Map map, String realMapClassName) {
		super(map,ODBType.MAP_ID);
		this.realMapClassName = realMapClassName;
	}
	public MapObjectInfo(Map<AbstractObjectInfo,AbstractObjectInfo> map,ODBType type, String realMapClassName) {
		super(map,type);
		this.realMapClassName = realMapClassName;		
	}

	public Map<AbstractObjectInfo,AbstractObjectInfo> getMap(){
		return (Map<AbstractObjectInfo,AbstractObjectInfo>) theObject;
	}
	public String toString() {
		if(theObject!=null){
			return theObject.toString();
		}
		return "null map";
	}
	public boolean isMapObject() {
		return true;
	}
	public String getRealMapClassName() {
		return realMapClassName;
	}
	public void setRealMapClassName(String realMapClassName) {
		this.realMapClassName = realMapClassName;
	}

	public AbstractObjectInfo createCopy(Map<OID,AbstractObjectInfo> cache,  boolean onlyData){
		Map m = (Map)theObject;

 		Map<AbstractObjectInfo,AbstractObjectInfo> newMap = new OdbHashMap<AbstractObjectInfo, AbstractObjectInfo>();
 		Iterator iterator = m.keySet().iterator();
 		
 		while(iterator.hasNext()){
 			AbstractObjectInfo keyAoi = (AbstractObjectInfo) iterator.next();
 			AbstractObjectInfo valueAoi = (AbstractObjectInfo) m.get(keyAoi);
 			
 			// create copies
 			keyAoi = keyAoi.createCopy(cache, onlyData);
 			valueAoi = valueAoi.createCopy(cache, onlyData);
 			
			newMap.put(keyAoi, valueAoi);
			
 		}
		
		MapObjectInfo moi = new MapObjectInfo(newMap, odbType,realMapClassName);
		return moi;
	}
}
