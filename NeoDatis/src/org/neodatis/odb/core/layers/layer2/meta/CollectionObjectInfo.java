
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;


/**
 * Meta representation of a collection
 * @author osmadja
 *
 */
public class CollectionObjectInfo extends GroupObjectInfo {
	private String realCollectionClassName;
	
	public CollectionObjectInfo() {
		super(null,ODBType.COLLECTION_ID);
		realCollectionClassName = ODBType.DEFAULT_COLLECTION_CLASS_NAME;
	}
	public CollectionObjectInfo(Collection<AbstractObjectInfo> collection) {
		super(collection,ODBType.COLLECTION_ID);
		realCollectionClassName = ODBType.DEFAULT_COLLECTION_CLASS_NAME;
	}

	public CollectionObjectInfo(Collection<AbstractObjectInfo> collection, Collection<NonNativeObjectInfo> nonNativeObjects) {
		super(collection,ODBType.COLLECTION_ID);
		realCollectionClassName = ODBType.DEFAULT_COLLECTION_CLASS_NAME;
		setNonNativeObjects(nonNativeObjects);
	}
	public CollectionObjectInfo(Collection<AbstractObjectInfo> collection,ODBType type, Collection<NonNativeObjectInfo> nonNativeObjects) {
		super(collection,type);
		realCollectionClassName = ODBType.DEFAULT_COLLECTION_CLASS_NAME;
		setNonNativeObjects(nonNativeObjects);
	}

	public Collection<AbstractObjectInfo> getCollection(){
		return (Collection<AbstractObjectInfo>) theObject;
	}
	
	public String toString() {
		if(theObject!=null){
			return theObject.toString();
		}
		return "null collection";
	}
	public boolean isCollectionObject() {
		return true;
	}
	public String getRealCollectionClassName() {
		return realCollectionClassName;
	}
	public void setRealCollectionClassName(String realCollectionClass) {
		this.realCollectionClassName = realCollectionClass;
	}
	
	public AbstractObjectInfo createCopy(Map<OID,AbstractObjectInfo> cache,  boolean onlyData){
		Collection c = (Collection)theObject;

 		Collection<AbstractObjectInfo> newCollection = new OdbArrayList<AbstractObjectInfo>();
 		// To keep track of non native objects
 		IOdbList<NonNativeObjectInfo> nonNatives = new OdbArrayList<NonNativeObjectInfo>();
 		Iterator iterator = c.iterator();
 		
 		while(iterator.hasNext()){
 			AbstractObjectInfo aoi = (AbstractObjectInfo) iterator.next();
 			// create copy
 			aoi = aoi.createCopy(cache, onlyData);
			newCollection.add(aoi);
			
			if(aoi.isNonNativeObject()){
				nonNatives.add((NonNativeObjectInfo) aoi);
			}
 		}
		
		CollectionObjectInfo coi = new CollectionObjectInfo(newCollection, odbType,nonNatives);
		coi.setRealCollectionClassName( realCollectionClassName);
		return coi;
	}

	
}
