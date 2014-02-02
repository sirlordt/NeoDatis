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
package org.neodatis.odb.impl.core.layers.layer2.meta.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectReference;
import org.neodatis.odb.core.layers.layer2.meta.compare.ArrayModifyElement;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedAttribute;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedNativeAttributeAction;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedObjectReferenceAttributeAction;
import org.neodatis.odb.core.layers.layer2.meta.compare.IObjectInfoComparator;
import org.neodatis.odb.core.layers.layer2.meta.compare.NewNonNativeObjectAction;
import org.neodatis.odb.core.layers.layer2.meta.compare.SetAttributeToNullAction;
import org.neodatis.odb.core.server.layers.layer2.meta.ClientNonNativeObjectInfo;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * Manage Object info differences. compares two object info and tells which
 * objects in the object hierarchy has changed. This is used by the update to process to optimize it and actually update what has changed
 * 
 * 
 * @author olivier s
 * 
 */
public class ObjectInfoComparator implements IObjectInfoComparator {
	private static final int SIZE = 5;

	public List<NonNativeObjectInfo> changedObjectMetaRepresentations;
	private List<SetAttributeToNullAction> attributeToSetToNull;
	/** TODO check the interface, the two subclasses and their uses*/
	public List<ChangedAttribute> changedAttributeActions;

	private Map<NonNativeObjectInfo,Integer> alreadyCheckingObjects;

	public List<Object> newObjects;

	public List<NewNonNativeObjectAction> newObjectMetaRepresentations;

	public List<ChangedObjectInfo> changes;
	
	private List<ArrayModifyElement> arrayChanges;

	public int maxObjectRecursionLevel;

	private int nbChanges;
	private boolean supportInPlaceUpdate;

	public ObjectInfoComparator() {
		changedObjectMetaRepresentations = new ArrayList<NonNativeObjectInfo>(SIZE);
		attributeToSetToNull = new ArrayList<SetAttributeToNullAction>(SIZE);
		alreadyCheckingObjects = new OdbHashMap<NonNativeObjectInfo, Integer>(SIZE);
		newObjects = new ArrayList<Object>(SIZE);
		newObjectMetaRepresentations = new ArrayList<NewNonNativeObjectAction>(SIZE);
		changes = new ArrayList<ChangedObjectInfo>(SIZE);
		changedAttributeActions = new ArrayList<ChangedAttribute>(SIZE);
		arrayChanges = new ArrayList<ArrayModifyElement>();
		maxObjectRecursionLevel = 0;
		supportInPlaceUpdate = false;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#hasChanged(org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo, org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo)
	 */
	public boolean hasChanged(AbstractObjectInfo aoi1, AbstractObjectInfo aoi2) {
		return hasChanged(aoi1, aoi2, -1);
	}

	private boolean hasChanged(AbstractObjectInfo aoi1, AbstractObjectInfo aoi2, int objectRecursionLevel) {
        // If one is null and the other not
        if(aoi1.isNull()!=aoi2.isNull()){
            return true;
        }

        if (aoi1.isNonNativeObject() && aoi2.isNonNativeObject()) {
			return hasChanged((NonNativeObjectInfo) aoi1, (NonNativeObjectInfo) aoi2, objectRecursionLevel + 1);
		}
		if (aoi1.isNative() && aoi2.isNative()) {
			return hasChanged((NativeObjectInfo) aoi1, (NativeObjectInfo) aoi2, 0);
		}

		return false;
	}

	private boolean hasChanged(NativeObjectInfo aoi1, NativeObjectInfo aoi2, int objectRecursionLevel) {

		if (aoi1.getObject() == null && aoi2.getObject() == null) {
			return false;
		}
		if (aoi1.getObject() == null || aoi2.getObject() == null) {
			return true;
		}

		return !aoi1.getObject().equals(aoi2.getObject());
	}

	private boolean hasChanged(NonNativeObjectInfo nnoi1, NonNativeObjectInfo nnoi2, int objectRecursionLevel) {

		AbstractObjectInfo value1 = null;
		AbstractObjectInfo value2 = null;

		boolean hasChanged = false;
		// If the object is already being checked, return false, this second
		// check will not affect the check
		if (alreadyCheckingObjects.get(nnoi2) != null) {
			return false;
		}

		// Put the object in the temporary cache
		alreadyCheckingObjects.put(nnoi1, new Integer(1));
		alreadyCheckingObjects.put(nnoi2, new Integer(1));
		
		// if classes are not the same => object has changed
		if(!nnoi1.isNull()&&!nnoi2.isNull() && !nnoi1.getClassInfo().getFullClassName().equals(nnoi2.getClassInfo().getFullClassName())){
			return true;
		}

		// Warning ID Start with 1 and not 0
		for (int id = 1; id <= nnoi1.getMaxNbattributes(); id++) {
			value1 = nnoi1.getAttributeValueFromId(id);

			// Gets the value by the attribute id to be sure
			// Problem because a new object info may not have the right ids ?
			// Check if
			// the new oiD is ok.
			value2 = nnoi2.getAttributeValueFromId(id);

			if (value2 == null) {
				// this means the object to have attribute id
				storeChangedObject(nnoi1, nnoi2, id, objectRecursionLevel);
				hasChanged = true;
				continue;
			}

			if(value1==null){
				//throw new ODBRuntimeException("ObjectInfoComparator.hasChanged:attribute with id "+id+" does not exist on "+nnoi2);
				// This happens when this object was created with an version of ClassInfo (which has been refactored).
				// In this case,we simply tell that in place update is not supported so that the object will be rewritten with 
				// new metamodel
				supportInPlaceUpdate = false;
				continue;
			}

			// If both are null, no effect
			if (value1.isNull() && value2.isNull()) {
				continue;
			}
			/*
			// To support case where one is non native null object and the other is a native object
			if(value1.getClass()!=value2.getClass()){
				supportInPlaceUpdate=false;
				return true;
			}
			*/

			if (value1.isNull() || value2.isNull()) {
				supportInPlaceUpdate = false;
				hasChanged=true;
				storeActionSetAttributetoNull(nnoi1, id, objectRecursionLevel);
				continue;
			}

			if (!classAreCompatible(value1, value2)) {

				if (value2 instanceof NativeObjectInfo) {
					storeChangedObject(nnoi1, nnoi2, id, objectRecursionLevel);
					storeChangedAttributeAction(new ChangedNativeAttributeAction(nnoi1,nnoi2,nnoi1.getHeader().getAttributeIdentificationFromId(id),
							(NativeObjectInfo) value2, objectRecursionLevel, false,nnoi1.getClassInfo().getAttributeInfoFromId(id).getName()));
				}
				if (value2 instanceof ObjectReference) {
					if(value1 instanceof NonNativeObjectInfo){
						NonNativeObjectInfo nnoi = (NonNativeObjectInfo) value1;
						ObjectReference oref = (ObjectReference) value2;
						if (!nnoi.getOid().equals(oref.getOid())) {
							storeChangedObject(nnoi1, nnoi2, id, objectRecursionLevel);
							int attributeIdThatHasChanged = id;
							// this is the exact position where the object reference
							// definition is stored
							long attributeDefinitionPosition = nnoi2.getAttributeDefinitionPosition(attributeIdThatHasChanged);
							storeChangedAttributeAction(new ChangedObjectReferenceAttributeAction(attributeDefinitionPosition,
									(ObjectReference) value2, objectRecursionLevel));
						} else {
							continue;
						}
					}
				}

				hasChanged = true;
				continue;
			}

			if (value1.isAtomicNativeObject()) {
				if (!value1.equals(value2)) {
					// storeChangedObject(nnoi1, nnoi2, id,
					// objectRecursionLevel);
					storeChangedAttributeAction(new ChangedNativeAttributeAction(nnoi1,nnoi2,nnoi1.getHeader().getAttributeIdentificationFromId(id),
							(NativeObjectInfo) value2, objectRecursionLevel, false,nnoi1.getClassInfo().getAttributeInfoFromId(id).getName()));

					hasChanged = true;
					continue;
				}
				continue;
			}

			if (value1.isCollectionObject()) {
				CollectionObjectInfo coi1 = (CollectionObjectInfo) value1;
				CollectionObjectInfo coi2 = (CollectionObjectInfo) value2;
				boolean collectionHasChanged = manageCollectionChanges(nnoi1, nnoi2, id, coi1, coi2, objectRecursionLevel);
				hasChanged = hasChanged || collectionHasChanged;
				continue;
			}

			if (value1.isArrayObject()) {
				ArrayObjectInfo aoi1 = (ArrayObjectInfo) value1;
				ArrayObjectInfo aoi2 = (ArrayObjectInfo) value2;
				boolean arrayHasChanged = manageArrayChanges(nnoi1, nnoi2, id, aoi1, aoi2, objectRecursionLevel);
				hasChanged = hasChanged || arrayHasChanged;
				continue;
			}
			if (value1.isMapObject()) {
				MapObjectInfo moi1 = (MapObjectInfo) value1;
				MapObjectInfo moi2 = (MapObjectInfo) value2;
				boolean mapHasChanged = manageMapChanges(nnoi1, nnoi2, id, moi1, moi2, objectRecursionLevel);
				hasChanged = hasChanged || mapHasChanged; 
				continue;
			}

			if (value1.isEnumObject()) {
				EnumNativeObjectInfo enoi1 = (EnumNativeObjectInfo) value1;
				EnumNativeObjectInfo enoi2 = (EnumNativeObjectInfo) value2;
				boolean enumHasChanged = !enoi1.getEnumClassInfo().getId().equals(enoi2.getEnumClassInfo().getId()) || !enoi1.getEnumName().equals(enoi2.getEnumName());
				hasChanged = hasChanged || enumHasChanged; 
				continue;
			}

			if (value1.isNonNativeObject()) {
				NonNativeObjectInfo oi1 = (NonNativeObjectInfo) value1;
				NonNativeObjectInfo oi2 = (NonNativeObjectInfo) value2;
				// If oids are equal, they are the same objects
				if (oi1.getOid()!=null &&  oi1.getOid().equals(oi2.getOid())) {
					hasChanged = hasChanged(value1, value2, objectRecursionLevel + 1) || hasChanged;
				} else {
					// This means that an object reference has changed.
					hasChanged = true;
					// keep track of the position where the reference must be
					// updated
					long positionToUpdateReference = nnoi1.getAttributeDefinitionPosition(id);

					storeNewObjectReference(positionToUpdateReference, oi2, objectRecursionLevel,nnoi1.getClassInfo().getAttributeInfoFromId(id).getName());
					objectRecursionLevel++;
					
					// Value2 may have change too
					addPendingVerification(value2);
				}
				continue;
			}
		}

		Integer i1 = (Integer) alreadyCheckingObjects.get(nnoi1);
		Integer i2 = (Integer) alreadyCheckingObjects.get(nnoi2);
		
		if (i1 != null) {
			i1 = new Integer(i1.intValue() - 1);
		}
		if (i2 != null) {
			i2 = new Integer(i2.intValue() - 1);
		}
		// i1 may be null in situation where the 2 nnoi have the same hashcode but are not equal : see org.neodatis.odb.test.fromusers.jease.TestNode junit
		if (i1!=null && i1.intValue() == 0) {
			alreadyCheckingObjects.remove(nnoi1);
		} else {
			alreadyCheckingObjects.put(nnoi1, i1);
		}
		if (i2!=null &&  i2.intValue() == 0) {
			alreadyCheckingObjects.remove(nnoi2);
		} else {
			alreadyCheckingObjects.put(nnoi2, i2);
		}

		return hasChanged;
	}

	/**
	 * An object reference has changed and the new object has not been checked, so disabled in place update
	 * TODO this is not good => all reference update will be done by full update and not in place update
	 * @param value
	 */
	private void addPendingVerification(AbstractObjectInfo value) {
		supportInPlaceUpdate = false;
	}

	private void storeNewObjectReference(long positionToUpdateReference, NonNativeObjectInfo oi2, int objectRecursionLevel,String attributeName) {
		NewNonNativeObjectAction nnnoa = new NewNonNativeObjectAction(positionToUpdateReference, oi2, objectRecursionLevel,attributeName);
		newObjectMetaRepresentations.add(nnnoa);
		nbChanges++;
	}

	private void storeActionSetAttributetoNull(NonNativeObjectInfo nnoi, int id, int objectRecursionLevel) {
		nbChanges++;
		SetAttributeToNullAction action = new SetAttributeToNullAction(nnoi,id);
		attributeToSetToNull.add(action);		
	}

	private void storeArrayChange(NonNativeObjectInfo nnoi, int arrayAttributeId, int arrayIndex, AbstractObjectInfo value,boolean supportInPlaceUpdate) {
		nbChanges++;
		ArrayModifyElement  ame = new ArrayModifyElement(nnoi,arrayAttributeId,arrayIndex,value,supportInPlaceUpdate);
		arrayChanges.add(ame);		
	}

	private boolean classAreCompatible(AbstractObjectInfo value1, AbstractObjectInfo value2) {
		Class clazz1 = value1.getClass();
		Class clazz2 = value2.getClass();

		if (clazz1 == clazz2) {
			return true;
		}
		if ((clazz1 == NonNativeObjectInfo.class) && (clazz2 == ClientNonNativeObjectInfo.class)) {
			return true;
		}
		return false;

	}

	private void storeChangedObject(NonNativeObjectInfo aoi1, NonNativeObjectInfo aoi2, int fieldId, AbstractObjectInfo oldValue,
			AbstractObjectInfo newValue, String message, int objectRecursionLevel) {
		
		if (aoi1 != null && aoi2 != null) {
			if (aoi1.getOid()!=null && aoi1.getOid().equals(aoi2.getOid())) {
				changedObjectMetaRepresentations.add(aoi2);
				changes.add(new ChangedObjectInfo(aoi1.getClassInfo(), aoi2.getClassInfo(), fieldId, oldValue, newValue, message,
						objectRecursionLevel));
				// also the max recursion level
				if (objectRecursionLevel > maxObjectRecursionLevel) {
					maxObjectRecursionLevel = objectRecursionLevel;
				}
				nbChanges++;
			} else {
				newObjects.add(aoi2.getObject());
				String fieldName = aoi1.getClassInfo().getAttributeInfoFromId(fieldId).getName();
				// keep track of the position where the reference must be
				// updated - use aoi1 to get position, because aoi2 do not have position defined yet
				long positionToUpdateReference = aoi1.getAttributeDefinitionPosition(fieldId);
				storeNewObjectReference(positionToUpdateReference, aoi2, objectRecursionLevel, fieldName);
				//newObjectMetaRepresentations.add(aoi2);
			}
		} else {
			DLogger.info("Non native object with null object");
		}
	}

	private void storeChangedObject(NonNativeObjectInfo aoi1, NonNativeObjectInfo aoi2, int fieldId, int objectRecursionLevel) {
		nbChanges++;
		if (aoi1 != null && aoi2 != null) {
			changes.add(new ChangedObjectInfo(aoi1.getClassInfo(), aoi2.getClassInfo(), fieldId,  aoi1
					.getAttributeValueFromId(fieldId),  aoi2.getAttributeValueFromId(fieldId), objectRecursionLevel));
			// also the max recursion level
			if (objectRecursionLevel > maxObjectRecursionLevel) {
				maxObjectRecursionLevel = objectRecursionLevel;
			}

		} else {
			DLogger.info("Non native object with null object");
		}
	}

	/**
	 * Checks if something in the Collection has changed, if yes, stores the
	 * change
	 * 
	 * @param nnoi1
	 *            The first Object meta representation (nnoi =
	 *            NonNativeObjectInfo)
	 * @param nnoi2
	 *            The second object meta representation
	 * @param fieldIndex
	 *            The field index that this collection represents
	 * @param coi1
	 *            The Meta representation of the collection 1 (coi =
	 *            CollectionObjectInfo)
	 * @param coi2
	 *            The Meta representation of the collection 2
	 * @param objectRecursionLevel
	 * @return true if 2 collection representation are different
	 */
	private boolean manageCollectionChanges(NonNativeObjectInfo nnoi1, NonNativeObjectInfo nnoi2, int fieldId, CollectionObjectInfo coi1,
			CollectionObjectInfo coi2, int objectRecursionLevel) {
		Collection<AbstractObjectInfo> collection1 = coi1.getCollection();
		Collection<AbstractObjectInfo> collection2 = coi2.getCollection();

		if (collection1.size() != collection2.size()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Collection size has changed oldsize=").append(collection1.size()).append("/newsize=").append(collection2.size());
			storeChangedObject(nnoi1, nnoi2, fieldId, coi1, coi2, buffer.toString(), objectRecursionLevel);
			return true;
		}

		Iterator iterator1 = collection1.iterator();
		Iterator iterator2 = collection2.iterator();

		AbstractObjectInfo value1 = null;
		AbstractObjectInfo value2 = null;
		int index = 0;
		while (iterator1.hasNext()) {
			value1 = (AbstractObjectInfo) iterator1.next();
			value2 = (AbstractObjectInfo) iterator2.next();

			boolean hasChanged = this.hasChanged(value1, value2, objectRecursionLevel);
			if (hasChanged) {

				// We consider collection has changed only if object are
				// different, If objects are the same instance, but something in
				// the object has changed, then the collection has not
				// changed,only the object

				if (value1.isNonNativeObject() && value2.isNonNativeObject()) {
					NonNativeObjectInfo nnoia = (NonNativeObjectInfo) value1;
					NonNativeObjectInfo nnoib = (NonNativeObjectInfo) value2;
					if (nnoia.getOid()!=null && !nnoia.getOid().equals(nnoi2.getOid())) {
						// Objects are not the same instance -> the collection
						// has changed
						storeChangedObject(nnoi1, nnoi2, fieldId, value1, value2, "List element index " + index + " has changed",
								objectRecursionLevel);
					}
				} else {
					supportInPlaceUpdate = false;
					nbChanges++;
					//storeChangedObject(nnoi1, nnoi2, fieldId, value1, value2, "List element index " + index + " has changed", objectRecursionLevel);
				}
				return true;
			}
			index++;
		}
		return false;
	}

	/**
	 * Checks if something in the Arary has changed, if yes, stores the change
	 * 
	 * @param nnoi1
	 *            The first Object meta representation (nnoi =
	 *            NonNativeObjectInfo)
	 * @param nnoi2
	 *            The second object meta representation
	 * @param fieldIndex
	 *            The field index that this collection represents
	 * @param aoi1
	 *            The Meta representation of the array 1 (aoi = ArraybjectInfo)
	 * @param aoi2
	 *            The Meta representation of the array 2
	 * @param objectRecursionLevel
	 * @return true if the 2 array representations are different
	 */
	private boolean manageArrayChanges(NonNativeObjectInfo nnoi1, NonNativeObjectInfo nnoi2, int fieldId, ArrayObjectInfo aoi1,
			ArrayObjectInfo aoi2, int objectRecursionLevel) {
		Object[] array1 = aoi1.getArray();
		Object[] array2 = aoi2.getArray();

		if (array1.length != array2.length) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Array size has changed oldsize=").append(array1.length).append("/newsize=").append(array2.length);
			storeChangedObject(nnoi1, nnoi2, fieldId, aoi1, aoi2, buffer.toString(), objectRecursionLevel);
			supportInPlaceUpdate = false;
			return true;
		}

		AbstractObjectInfo value1 = null;
		AbstractObjectInfo value2 = null;
		
		// check if this array supports in place update
		boolean localSupportInPlaceUpdate = ODBType.hasFixSize(aoi2.getComponentTypeId());
		int index = 0;
		boolean hasChanged = false;
		try{
			for (int i = 0; i < array1.length; i++) {
				value1 = (AbstractObjectInfo) array1[i];
				value2 = (AbstractObjectInfo) array2[i];

				boolean localHasChanged = this.hasChanged(value1, value2, objectRecursionLevel);
				if (localHasChanged) {
					storeArrayChange(nnoi1, fieldId, i, value2,localSupportInPlaceUpdate);

                    if(localSupportInPlaceUpdate){
						hasChanged = true;
					}else{
						hasChanged = true;
						return hasChanged;
					}
				}
				index++;
			}
		}finally{
			if(hasChanged && !localSupportInPlaceUpdate){
				supportInPlaceUpdate = false;
			}
		}
		return hasChanged;
	}

	/**
	 * Checks if something in the Map has changed, if yes, stores the change
	 * 
	 * @param nnoi1
	 *            The first Object meta representation (nnoi =
	 *            NonNativeObjectInfo)
	 * @param nnoi2
	 *            The second object meta representation
	 * @param fieldIndex
	 *            The field index that this map represents
	 * @param moi1
	 *            The Meta representation of the map 1 (moi = MapObjectInfo)
	 * @param moi2
	 *            The Meta representation of the map 2
	 * @param objectRecursionLevel
	 * @return true if the 2 map representations are different
	 */
	private boolean manageMapChanges(NonNativeObjectInfo nnoi1, NonNativeObjectInfo nnoi2, int fieldId, MapObjectInfo moi1,
			MapObjectInfo moi2, int objectRecursionLevel) {

		Map<AbstractObjectInfo,AbstractObjectInfo> map1 = moi1.getMap();
		Map<AbstractObjectInfo,AbstractObjectInfo> map2 = moi2.getMap();

		if (map1.size() != map2.size()) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Map size has changed oldsize=").append(map1.size()).append("/newsize=").append(map2.size());
			storeChangedObject(nnoi1, nnoi2, fieldId, moi1, moi2, buffer.toString(), objectRecursionLevel);
			return true;
		}

		Iterator<AbstractObjectInfo> keys1 = map1.keySet().iterator();
		Iterator<AbstractObjectInfo> keys2 = map2.keySet().iterator();

		AbstractObjectInfo key1 = null;
		AbstractObjectInfo key2 = null;

		AbstractObjectInfo value1 = null;
		AbstractObjectInfo value2 = null;
		int index = 0;

		while (keys1.hasNext()) {
			key1 = keys1.next();
			key2 = keys2.next();

			boolean keysHaveChanged = this.hasChanged(key1, key2, objectRecursionLevel);

			if (keysHaveChanged) {
				storeChangedObject(nnoi1, nnoi2, fieldId, key1, key2, "Map key index " + index + " has changed", objectRecursionLevel);
				return true;
			}
			value1 = map1.get(key1);
			value2 = map2.get(key2);

			boolean valuesHaveChanged = this.hasChanged(value1, value2, objectRecursionLevel);
			if (valuesHaveChanged) {
				storeChangedObject(nnoi1, nnoi2, fieldId, value1, value2, "Map value index " + index + " has changed", objectRecursionLevel);
				return true;
			}
			index++;
		}
		return false;
	}

	protected void storeChangedAttributeAction(ChangedNativeAttributeAction caa) {
		nbChanges++;
		changedAttributeActions.add(caa);
	}

	protected void storeChangedAttributeAction(ChangedObjectReferenceAttributeAction caa) {
		nbChanges++;
		changedAttributeActions.add(caa);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getChangedObjectMetaRepresentation(int)
	 */
	public AbstractObjectInfo getChangedObjectMetaRepresentation(int i) {
		return changedObjectMetaRepresentations.get(i);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getChanges()
	 */
	public List<ChangedObjectInfo> getChanges() {
		return changes;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getNewObjectMetaRepresentations()
	 */
	public List<NewNonNativeObjectAction> getNewObjectMetaRepresentations() {
		return newObjectMetaRepresentations;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getNewObjectMetaRepresentation(int)
	 */
	public NewNonNativeObjectAction getNewObjectMetaRepresentation(int i) {
		return newObjectMetaRepresentations.get(i);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getNewObjects()
	 */
	public List<Object> getNewObjects() {
		return newObjects;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getMaxObjectRecursionLevel()
	 */
	public int getMaxObjectRecursionLevel() {
		return maxObjectRecursionLevel;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getChangedAttributeActions()
	 */
	public List<ChangedAttribute> getChangedAttributeActions() {
		return changedAttributeActions;
	}

	public void setChangedAttributeActions(List<ChangedAttribute> changedAttributeActions) {
		this.changedAttributeActions = changedAttributeActions;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getAttributeToSetToNull()
	 */
	public List<SetAttributeToNullAction> getAttributeToSetToNull() {
		return attributeToSetToNull;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#clear()
	 */
	public void clear() {

		changedObjectMetaRepresentations.clear();
		attributeToSetToNull.clear();
		alreadyCheckingObjects.clear();
		newObjects.clear();
		newObjectMetaRepresentations.clear();
		changes.clear();
		changedAttributeActions.clear();
		arrayChanges.clear();
		maxObjectRecursionLevel = 0;
		nbChanges = 0;
		supportInPlaceUpdate = false;

		/*
		changedObjectMetaRepresentations = null;
		attributeToSetToNull = null;
		alreadyCheckingObjects = null;
		newObjects = null;
		newObjectMetaRepresentations = null;
		changes = null;
		changedAttributeActions = null;
		arrayChanges = null;
		*/
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getNbChanges()
	 */
	public int getNbChanges() {
		return nbChanges;
	}

	public String toString() {
		return nbChanges+" changes";
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#getArrayChanges()
	 */
	public List<ArrayModifyElement> getArrayChanges() {
		return arrayChanges;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer2.meta.IObjectInfoComparator#supportInPlaceUpdate()
	 */
	public boolean supportInPlaceUpdate() {
		return supportInPlaceUpdate;
	}
	
}
