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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * A meta representation of a class
 * 
 * @author osmadja
 * 
 */
public class ClassInfo implements Serializable {

	/** Constant used for the classCategory variable to indicate a system class */
	public static final byte CATEGORY_SYSTEM_CLASS = 1;

	/** Constant used for the classCategory variable to indicate a user class */
	public static final byte CATEGORY_USER_CLASS = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** To specify the type of the class : system class or user class */
	private byte classCategory;

	/** The full class name with package */
	private String fullClassName;
	/** Extra info of the class - no used in java version */
	private String extraInfo;

	private IOdbList<ClassAttributeInfo> attributes;

	/**
	 * This map is redundant with the field 'attributes', but it is to enable
	 * fast access to attributes by name TODO use only the map and remove list
	 * key=attribute name, key =ClassInfoattribute
	 */
	private Map<String, ClassAttributeInfo> attributesByName;
	/**
	 * This map is redundant with the field 'attributes', but it is to enable
	 * fast access to attributes by id key=attribute Id(Integer), key
	 * =ClassAttributeInfo
	 */
	private Map<Integer, ClassAttributeInfo> attributesById;

	/**
	 * To keep session original numbers, original number of committed
	 * objects,first and last object position
	 */
	private CommittedCIZoneInfo original;

	/**
	 * To keep session numbers, number of committed objects,first and last
	 * object position
	 */
	private CommittedCIZoneInfo committed;

	/**
	 * To keep session uncommitted numbers, number of uncommitted objects,first
	 * and last object position
	 */
	private CIZoneInfo uncommitted;

	/** Physical location of this class in the file (in byte) */
	private long position;

	private OID id;

	/** Where is the previous class. -1, if it does not exist */
	private OID previousClassOID;

	/** Where is the next class, -1, if it does not exist */
	private OID nextClassOID;

	/** Where starts the block of attributes definition of this class ? */
	private long attributesDefinitionPosition;

	/** The size (in bytes) of the class block */
	private int blockSize;

	/** Infos about the last object of this class */
	private ObjectInfoHeader lastObjectInfoHeader;

	/**
	 * The max id is used to give a unique id for each attribute and allow
	 * refactoring like new field and/or removal
	 */
	private int maxAttributeId;

	private IOdbList<ClassInfoIndex> indexes;

	private transient IOdbList<Object> history;

	public ClassInfo() {
		this.original = new CommittedCIZoneInfo(this, null, null, 0);
		this.committed = new CommittedCIZoneInfo(this, null, null, 0);
		this.uncommitted = new CIZoneInfo(this, null, null, 0);
		this.previousClassOID = null;
		this.nextClassOID = null;
		this.blockSize = -1;
		this.position = -1;
		this.maxAttributeId = -1;
		this.classCategory = CATEGORY_USER_CLASS;
		this.history = new OdbArrayList<Object>();
	}

	public ClassInfo(String className) {
		this(className, "", null);
	}

	public ClassInfo(String className, String extraInfo) {
		this(className, extraInfo, null);
	}

	protected ClassInfo(String fullClassName, String extraInfo, IOdbList<ClassAttributeInfo> attributes) {
		this();
		this.fullClassName = fullClassName;
		this.extraInfo = extraInfo;
		this.attributes = attributes;
		this.attributesByName = new OdbHashMap<String, ClassAttributeInfo>();
		this.attributesById = new OdbHashMap<Integer, ClassAttributeInfo>();
		if (attributes != null) {
			fillAttributesMap();
		}
		this.maxAttributeId = (attributes == null ? 1 : attributes.size() + 1);
	}

	private void fillAttributesMap() {
		ClassAttributeInfo cai = null;
		if (attributesByName == null) {
			attributesByName = new OdbHashMap<String, ClassAttributeInfo>();
			attributesById = new OdbHashMap<Integer, ClassAttributeInfo>();
		} else {
			// attributesMap.clear();
		}
		for (int i = 0; i < attributes.size(); i++) {
			cai = attributes.get(i);
			attributesByName.put(cai.getName(), cai);
			attributesById.put(new Integer(cai.getId()), cai);
		}
	}

	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != ClassInfo.class) {
			return false;
		}
		ClassInfo classInfo = (ClassInfo) obj;
		return classInfo.fullClassName.equals(fullClassName);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" [ ").append(fullClassName).append(" - id=").append(id);
		buffer.append(" - previousClass=").append(previousClassOID).append(" - nextClass=").append(nextClassOID).append(" - attributes=(");
		// buffer.append(" | position=").append(position);
		// buffer.append(" | class=").append(className).append(" | attributes=[");

		if (attributes != null) {
			for (int i = 0; i < attributes.size(); i++) {
				ClassAttributeInfo cai = (ClassAttributeInfo) attributes.get(i);
				buffer.append(cai.getName()).append(",");
			}
		} else {
			buffer.append("not yet defined");
		}
		buffer.append(") ]");

		/*
		 * buffer.append("- nbobjects=(");
		 * buffer.append(committed.getNbObjects()
		 * ).append(",").append(uncommitted
		 * .getNbObjects()).append(") | first object oid.=(").append(
		 * committed.first
		 * ).append(",").append(uncommitted.first).append(") | last object oid.=("
		 * ).append(committed.last).append(",")
		 * .append(uncommitted.last).append(")");
		 * buffer.append(" | definition=")
		 * .append(attributesDefinitionPosition);
		 * buffer.append(" | blockSize=").append(blockSize);
		 */
		return buffer.toString();
	}

	public IOdbList<ClassAttributeInfo> getAttributes() {
		return attributes;
	}

	public void setAttributes(IOdbList<ClassAttributeInfo> attributes) {
		this.attributes = attributes;
		// We must always keep the original maxAttribute ID. this is to enable refactoring removing fields
		this.maxAttributeId = Math.max(this.maxAttributeId,attributes.size());
		fillAttributesMap();
	}

	public CommittedCIZoneInfo getCommitedZoneInfo() {
		return committed;
	}

	public long getAttributesDefinitionPosition() {
		return attributesDefinitionPosition;
	}

	public void setAttributesDefinitionPosition(long definitionPosition) {
		this.attributesDefinitionPosition = definitionPosition;
	}

	public OID getNextClassOID() {
		return nextClassOID;
	}

	public void setNextClassOID(OID nextClassOID) {
		this.nextClassOID = nextClassOID;
	}

	public OID getPreviousClassOID() {
		return previousClassOID;
	}

	public void setPreviousClassOID(OID previousClassOID) {
		this.previousClassOID = previousClassOID;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * @return the fullClassName
	 */
	public String getFullClassName() {
		return fullClassName;
	}

	/**
	 * This method could be optimized, but it is only on Class creation, one
	 * time in the database life time... This is used to get all (non native)
	 * attributes a class info have to store them in the meta model before
	 * storing the class itself
	 * 
	 * @return
	 */
	public IOdbList<ClassAttributeInfo> getAllNonNativeAttributes() {
		IOdbList<ClassAttributeInfo> result = new OdbArrayList<ClassAttributeInfo>(attributes.size());
		ClassAttributeInfo cai = null;
		for (int i = 0; i < attributes.size(); i++) {
			cai = attributes.get(i);
			if (!cai.isNative() || cai.getAttributeType().isEnum()) {
				result.add(cai);
			} else if (cai.getAttributeType().isArray() && !cai.getAttributeType().getSubType().isNative()) {
				result.add(new ClassAttributeInfo(-1, "subtype", cai.getAttributeType().getSubType().getName(), null));
			}
		}
		return result;
	}

	public OID getId() {
		return id;
	}

	public void setId(OID id) {
		this.id = id;
	}

	public ClassAttributeInfo getAttributeInfoFromId(int id) {
		return attributesById.get(new Integer(id));
	}

	public int getAttributeId(String name) {
		ClassAttributeInfo cai = attributesByName.get(name);
		if (cai == null) {
			return -1;
		}
		return cai.getId();
	}

	public ClassAttributeInfo getAttributeInfoFromName(String name) {
		return attributesByName.get(name);
	}

	public ClassAttributeInfo getAttributeInfo(int index) {
		return attributes.get(index);
	}

	public int getMaxAttributeId() {
		return maxAttributeId;
	}

	public void setMaxAttributeId(int maxAttributeId) {
		this.maxAttributeId = maxAttributeId;
	}

	public ClassInfoCompareResult extractDifferences(ClassInfo newCI, boolean update) {
		String attributeName = null;
		ClassAttributeInfo cai1 = null;
		ClassAttributeInfo cai2 = null;
		ClassInfoCompareResult result = new ClassInfoCompareResult(getFullClassName());
		boolean isCompatible = true;
		IOdbList<ClassAttributeInfo> attributesToRemove = new OdbArrayList<ClassAttributeInfo>(10);
		IOdbList<ClassAttributeInfo> attributesToAdd = new OdbArrayList<ClassAttributeInfo>(10);
		int nbAttributes = attributes.size();
		for (int id = 0; id < nbAttributes; id++) {
			// !!!WARNING : ID start with 1 and not 0
			cai1 = attributes.get(id);
			if (cai1 == null) {
				continue;
			}
			attributeName = cai1.getName();
			cai2 = newCI.getAttributeInfoFromId(cai1.getId());
			if (cai2 == null) {
				result.addCompatibleChange("Field '" + attributeName + "' (type="+cai1.getFullClassname() +") has been removed");
				if (update) {
					// Simply remove the attribute from meta-model
					attributesToRemove.add(cai1);
				}
			} else {
				if (!ODBType.typesAreCompatible(cai1.getAttributeType(), cai2.getAttributeType())) {
					result.addIncompatibleChange("Type of Field '" + attributeName + "' has changed : old='" + cai1.getFullClassname()
							+ "' - new='" + cai2.getFullClassname() + "'");
					isCompatible = false;
				}
			}
		}
		int nbNewAttributes = newCI.attributes.size();
		for (int id = 0; id < nbNewAttributes; id++) {
			// !!!WARNING : ID start with 1 and not 0
			cai2 = newCI.attributes.get(id);
			if (cai2 == null) {
				continue;
			}
			attributeName = cai2.getName();
			cai1 = getAttributeInfoFromId(cai2.getId());
			if (cai1 == null) {
				result.addCompatibleChange("Field '" + attributeName + "' (type="+ cai2.getFullClassname()+") has been added");
				if (update) {
					// Sets the right id of attribute
					cai2.setId(maxAttributeId + 1);
					maxAttributeId++;
					// Then adds the new attribute to the meta-model
					attributesToAdd.add(cai2);
				}

			}
		}
		attributes.removeAll(attributesToRemove);
		attributes.addAll(attributesToAdd);
		fillAttributesMap();
		return result;
	}

	public int getNumberOfAttributes() {
		return attributes.size();
	}

	public ClassInfoIndex addIndexOn(String name, String[] indexFields, boolean acceptMultipleValuesForSameKey) {
		if (indexes == null) {
			indexes = new OdbArrayList<ClassInfoIndex>();
		}
		ClassInfoIndex cii = new ClassInfoIndex();
		cii.setClassInfoId(id);
		cii.setCreationDate(OdbTime.getCurrentTimeInMs());
		cii.setLastRebuild(cii.getCreationDate());
		cii.setName(name);
		cii.setStatus(ClassInfoIndex.ENABLED);
		cii.setUnique(!acceptMultipleValuesForSameKey);
		int[] attributeIds = new int[indexFields.length];
		for (int i = 0; i < indexFields.length; i++) {
			attributeIds[i] = getAttributeId(indexFields[i]);
			if(attributeIds[i]==-1){
				throw new ODBRuntimeException(NeoDatisError.CLASS_INFO_DO_NOT_HAVE_THE_ATTRIBUTE.addParameter(getFullClassName()).addParameter(indexFields[i]));
			}
		}
		cii.setAttributeIds(attributeIds);
		indexes.add(cii);
		return cii;
	}

	/**
	 * Removes an index
	 * 
	 * @param cii
	 */
	public void removeIndex(ClassInfoIndex cii) {
		indexes.remove(cii);
	}

	public int getNumberOfIndexes() {
		if (indexes == null) {
			return 0;
		}
		return indexes.size();

	}

	public ClassInfoIndex getIndex(int index) {
		if (indexes == null || index >= indexes.size()) {
			throw new ODBRuntimeException(NeoDatisError.INDEX_NOT_FOUND.addParameter(getFullClassName()).addParameter(index));
		}
		return indexes.get(index);
	}

	public void setIndexes(IOdbList<ClassInfoIndex> indexes2) {
		this.indexes = indexes2;
	}

	/**
	 * To detect if a class has cyclic reference
	 * 
	 * @return true if this class info has cyclic references
	 */
	public boolean hasCyclicReference() {
		return hasCyclicReference(new OdbHashMap<String, ClassInfo>());
	}

	/**
	 * To detect if a class has cyclic reference
	 * 
	 * @param alreadyVisitedClasses
	 *            A hashmap containg all the already visited classes
	 * @return true if this class info has cyclic references
	 */
	private boolean hasCyclicReference(Map<String, ClassInfo> alreadyVisitedClasses) {
		ClassAttributeInfo cai = null;
		boolean hasCyclicRef = false;
		if (alreadyVisitedClasses.get(fullClassName) != null) {
			return true;
		}
		Map<String, ClassInfo> localMap = new OdbHashMap<String, ClassInfo>();
		alreadyVisitedClasses.put(fullClassName, this);
		for (int i = 0; i < attributes.size(); i++) {
			cai = getAttributeInfo(i);
			if (!cai.isNative()) {
				localMap = new OdbHashMap<String, ClassInfo>(alreadyVisitedClasses);
				hasCyclicRef = cai.getClassInfo().hasCyclicReference(localMap);
				if (hasCyclicRef) {
					return true;
				}
			}
		}
		return false;
	}

	public byte getClassCategory() {
		return classCategory;
	}

	public void setClassCategory(byte classInfoType) {
		this.classCategory = classInfoType;
	}

	public ObjectInfoHeader getLastObjectInfoHeader() {
		return lastObjectInfoHeader;
	}

	public void setLastObjectInfoHeader(ObjectInfoHeader lastObjectInfoHeader) {
		this.lastObjectInfoHeader = lastObjectInfoHeader;
	}

	public CIZoneInfo getUncommittedZoneInfo() {
		return uncommitted;
	}

	/**
	 * Get number of objects: committed and uncommitted
	 * 
	 * @return The number of committed and uncommitted objects
	 */
	public long getNumberOfObjects() {
		return committed.getNbObjects() + uncommitted.getNbObjects();
	}

	public CommittedCIZoneInfo getOriginalZoneInfo() {
		return original;
	}

	public boolean isSystemClass() {
		return classCategory == CATEGORY_SYSTEM_CLASS;
	}

	public ClassInfoIndex getIndexWithName(String name) {
		ClassInfoIndex cii = null;
		if (indexes == null) {
			return null;
		}
		for (int i = 0; i < indexes.size(); i++) {
			cii = indexes.get(i);
			if (cii.getName().equals(name)) {
				return cii;
			}
		}
		return null;
	}

	public ClassInfoIndex getIndexForAttributeId(int attributeId) {
		ClassInfoIndex cii = null;
		if (indexes == null) {
			return null;
		}
		for (int i = 0; i < indexes.size(); i++) {
			cii = indexes.get(i);
			if (cii.getAttributeIds().length == 1 && cii.getAttributeId(0) == attributeId) {
				return cii;
			}
		}
		return null;
	}

	public ClassInfoIndex getIndexForAttributeIds(int[] attributeIds) {
		ClassInfoIndex cii = null;
		if (indexes == null) {
			return null;
		}
		for (int i = 0; i < indexes.size(); i++) {
			cii = indexes.get(i);
			if (cii.matchAttributeIds(attributeIds)) {
				return cii;
			}
		}
		return null;
	}

	public String[] getAttributeNames(int[] attributeIds) {

		int nbIds = attributeIds.length;
		String[] names = new String[nbIds];

		for (int i = 0; i < nbIds; i++) {
			names[i] = getAttributeInfoFromId(attributeIds[i]).getName();
		}
		return names;
	}

	public List<String> getAttributeNamesAsList(int[] attributeIds) {

		int nbIds = attributeIds.length;
		List<String> names = new ArrayList<String>(attributeIds.length);

		for (int i = 0; i < nbIds; i++) {
			names.add(getAttributeInfoFromId(attributeIds[i]).getName());
		}
		return names;
	}

	public IOdbList<ClassInfoIndex> getIndexes() {
		if (indexes == null) {
			return new OdbArrayList<ClassInfoIndex>();
		}
		return indexes;
	}

	public void removeAttribute(ClassAttributeInfo cai) {
		attributes.remove(cai);
		attributesByName.remove(cai.getName());
	}

	public void addAttribute(ClassAttributeInfo cai) {
		cai.setId(maxAttributeId++);
		attributes.add(cai);
		attributesByName.put(cai.getName(), cai);
	}

	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}

	public void addHistory(Object o) {
		if (history == null) {
			history = new OdbArrayList<Object>(1);
		}
		history.add(o);
	}

	public IOdbList<? extends Object> getHistory() {
		return history;
	}

	public boolean hasIndex(String indexName) {
		ClassInfoIndex cii = null;
		if (indexes == null) {
			return false;
		}
		for (int i = 0; i < indexes.size(); i++) {
			cii = indexes.get(i);
			if (indexName.equals(cii.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasIndex() {
		return indexes != null && !indexes.isEmpty();
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public ClassInfo duplicate(boolean onlyData) {
		ClassInfo ci = new ClassInfo(fullClassName);
		ci.extraInfo = extraInfo;

		ci.setAttributes(attributes);
		ci.setClassCategory(classCategory);
		ci.setMaxAttributeId(maxAttributeId);

		if (onlyData) {
			return ci;
		}

		ci.setAttributesDefinitionPosition(attributesDefinitionPosition);
		ci.setBlockSize(blockSize);
		ci.setExtraInfo(extraInfo);
		ci.setId(id);
		ci.setPreviousClassOID(previousClassOID);
		ci.setNextClassOID(nextClassOID);
		ci.setLastObjectInfoHeader(lastObjectInfoHeader);
		ci.setPosition(position);
		ci.setIndexes(indexes);

		return ci;
	}

}
