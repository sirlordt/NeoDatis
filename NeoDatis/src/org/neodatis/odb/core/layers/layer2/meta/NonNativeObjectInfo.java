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

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.tool.wrappers.OdbClassUtil;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * To keep info about a non native object. The NonNativeObjectInfo is the meta representation and is a class of the Layer in NeoDatis architecture (http://wiki.neodatis.org/odb-layers).
 * 
 * The NonNativeObjectInfo (nnoi) contains all the data of the attributes of an objects. 
 * 
 * <pre>
 * 
 *      * The object
 * The type of the object (org.neodatis.odb.core.meta.ODBType)
 * The ClassInfo of the object
 * The list of attributes (list of AbstractObjectInfo)
 * The ObjectInfoHeader that holds :
 *           o The object position (in the ODB file)
 *           o The Object OID
 *           o The previous Object OID
 *           o The next object OID
 *           o The ClassInfo OID
 *           o The ids (local id, to idenitfy the attribute) of the attributes
 *           o The OID or position of the attributes
 * 
 * </pre>
 * 
 * @author olivier s
 * 
 */
public class NonNativeObjectInfo extends AbstractObjectInfo {
	/** The object being represented */
	protected transient Object object;
	private ClassInfo classInfo;
	// private List attributeValues;
	private ObjectInfoHeader objectHeader;
	private AbstractObjectInfo[] attributeValues;
	/** To keep track of all non native objects , not used for instance */
	private IOdbList<NonNativeObjectInfo> allNonNativeObjects;
	private int maxNbattributes;

	public NonNativeObjectInfo() {
		super(null);
	}

	public NonNativeObjectInfo(ObjectInfoHeader oip, ClassInfo classInfo) {
		super(null);
		this.classInfo = classInfo;
		this.objectHeader = oip;
		if (classInfo != null) {
			this.maxNbattributes = classInfo.getMaxAttributeId();
			this.attributeValues = new AbstractObjectInfo[maxNbattributes];
		}
		this.allNonNativeObjects = null;// new
										// OdbArrayList<NonNativeObjectInfo>();
	}

	public NonNativeObjectInfo(ClassInfo classInfo) {
		super(null);
		this.classInfo = classInfo;
		this.objectHeader = new ObjectInfoHeader(-1, null, null, (classInfo != null ? classInfo.getId() : null), null, null);
		if (classInfo != null) {
			this.maxNbattributes = classInfo.getMaxAttributeId();
			this.attributeValues = new AbstractObjectInfo[maxNbattributes];
		}
		this.allNonNativeObjects = null;// new
										// OdbArrayList<NonNativeObjectInfo>();
	}

	public NonNativeObjectInfo(Object object, ClassInfo info, AbstractObjectInfo[] values, long[] attributesIdentification,
			int[] attributeIds) {
		super(ODBType.getFromName(info.getFullClassName()));
		this.object = object;
		this.classInfo = info;
		this.attributeValues = values;
		this.maxNbattributes = classInfo.getMaxAttributeId();
		if (attributeValues == null) {
			this.attributeValues = new AbstractObjectInfo[maxNbattributes];
		}
		this.objectHeader = new ObjectInfoHeader(-1, null, null, (classInfo != null ? classInfo.getId() : null), attributesIdentification,
				attributeIds);
		this.allNonNativeObjects = new OdbArrayList<NonNativeObjectInfo>();
	}

	public ObjectInfoHeader getHeader() {
		return objectHeader;
	}

	/**
	 * Return The meta representation of an attribute from its attribute id. Attribute ids are sequencial number(starting from 1) set by the classIntropector to identify attributes
	 * @param attributeId
	 * @return
	 */
	public AbstractObjectInfo getAttributeValueFromId(int attributeId) {
		return attributeValues[attributeId - 1];
	}

	/**
	 * Return the class info of the object. The Class Info is a meta representation of the java class
	 * @return
	 */
	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(ClassInfo classInfo) {
		if (classInfo != null) {
			this.classInfo = classInfo;
			this.objectHeader.setClassInfoId(classInfo.getId());
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(classInfo.getFullClassName()).append("(").append(getOid()).append(")=");

		if (attributeValues == null) {
			buffer.append("null attribute values");
			return buffer.toString();
		}

		for (int i = 0; i < attributeValues.length; i++) {
			if (i != 0) {
				buffer.append(",");
			}

			String attributeName = (classInfo != null ? (classInfo.getAttributeInfo(i)).getName() : "?");
			buffer.append(attributeName).append("=");
			Object object = attributeValues[i];
			if (object == null) {
				buffer.append(" null java object - should not happen , ");
			} else {
				ODBType type = ODBType.getFromClass(attributeValues[i].getClass());
				if (object instanceof NonNativeNullObjectInfo) {
					buffer.append("null");
					continue;
				}
				if (object instanceof NonNativeDeletedObjectInfo) {
					buffer.append("deleted object");
					continue;
				}
				if (object instanceof NativeObjectInfo) {
					NativeObjectInfo noi = (NativeObjectInfo) object;
					buffer.append(noi.toString());
					continue;
				}
				if (object instanceof ObjectReference) {
					buffer.append(object.toString());
					continue;
				}
				if (object instanceof NonNativeObjectInfo) {
					NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
					buffer.append("@").append(nnoi.getClassInfo().getFullClassName()).append("(id=").append(nnoi.getOid()).append(")");
					continue;
				}

				buffer.append("@").append(OdbClassUtil.getClassName(type.getName()));
			}
		}
		return buffer.toString();
	}

	/** Returns the oid of the next object of the same type
	 * 
	 * @return
	 */
	public OID getNextObjectOID() {
		return objectHeader.getNextObjectOID();
	}

	public void setNextObjectOID(OID nextObjectOID) {
		this.objectHeader.setNextObjectOID(nextObjectOID);
	}

	/** Returns the oid of the previous object of the same type
	 * 
	 * @return
	 */
	public OID getPreviousObjectOID() {
		return objectHeader.getPreviousObjectOID();
	}

	public void setPreviousInstanceOID(OID previousObjectOID) {
		this.objectHeader.setPreviousObjectOID(previousObjectOID);
	}

	/** Gets the physical position of the object in the NeoDatis database file
	 * 
	 */
	public long getPosition() {
		return objectHeader.getPosition();
	}

	public void setPosition(long position) {
		objectHeader.setPosition(position);
	}

	/** Gets the actual java object. May return null on client server mode as Client Server mode does work with java objects (layer1)
	 * 
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Return the value of the attribute 'attribute name'
	 * 
	 * <pre>
	 * For example, if  the class User has 2 attributes (name of type String, and profile of type Profile) calling getValueOf("name") on a nnoi (NonNativeObjectInfo) 
	 * that represents an instance of User will return its name
	 * </pre>
	 * @param attributeName
	 * @return
	 */
	public Object getValueOf(String attributeName) {
		int attributeId = -1;
		boolean isRelation = attributeName.indexOf(".") != -1;
		if (!isRelation) {
			attributeId = getClassInfo().getAttributeId(attributeName);
			return getAttributeValueFromId(attributeId).getObject();
		}
		int firstDotIndex = attributeName.indexOf(".");
		String firstAttributeName = OdbString.substring(attributeName, 0, firstDotIndex);
		attributeId = getClassInfo().getAttributeId(firstAttributeName);
		Object object = attributeValues[attributeId];
		if (object instanceof NonNativeObjectInfo) {
			NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
			return nnoi.getValueOf(OdbString.substring(attributeName, firstDotIndex + 1, attributeName.length()));
		}
		throw new ODBRuntimeException(NeoDatisError.CLASS_INFO_DO_NOT_HAVE_THE_ATTRIBUTE.addParameter(getClassInfo().getFullClassName())
				.addParameter(attributeName));
	}

	/**
	 * Return the value of the attribute 'attribute name'. Same as getValueOf, except that the object return is a meta representation of the real object : an AbstractObjectInfo (NonNative ObjectInfo, 
	 * NativeObjectnfo,....)
	 * 
	 * 
	 * <pre>
	 * For example, if  the class User has 2 attributes (name of type String, and profile of type Profile) calling getValueOf("name") on a nnoi (NonNativeObjectInfo) 
	 * that represents an instance of User will return its name
	 * </pre>
	 * @param attributeName
	 * @return
	 */
	public AbstractObjectInfo getMetaValueOf(String attributeName) {
		int attributeId = -1;
		boolean isRelation = attributeName.indexOf(".") != -1;
		if (!isRelation) {
			attributeId = getClassInfo().getAttributeId(attributeName);
			return getAttributeValueFromId(attributeId);
		}
		int firstDotIndex = attributeName.indexOf(".");
		String firstAttributeName = OdbString.substring(attributeName, 0, firstDotIndex);
		attributeId = getClassInfo().getAttributeId(firstAttributeName);
		Object object = attributeValues[attributeId];
		if (object instanceof NonNativeObjectInfo) {
			NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
			return nnoi.getMetaValueOf(OdbString.substring(attributeName, firstDotIndex + 1, attributeName.length()));
		}
		throw new ODBRuntimeException(NeoDatisError.CLASS_INFO_DO_NOT_HAVE_THE_ATTRIBUTE.addParameter(getClassInfo().getFullClassName())
				.addParameter(attributeName));
	}

	/**
	 * Used to change the value of an attribute
	 * 
	 * @param attributeName
	 * @param aoi
	 */
	public void setValueOf(String attributeName, AbstractObjectInfo aoi) {
		int attributeId = -1;
		boolean isRelation = attributeName.indexOf(".") != -1;
		if (!isRelation) {
			attributeId = getClassInfo().getAttributeId(attributeName);
			setAttributeValue(attributeId, aoi);
			return;
		}
		int firstDotIndex = attributeName.indexOf(".");
		String firstAttributeName = OdbString.substring(attributeName, 0, firstDotIndex);
		attributeId = getClassInfo().getAttributeId(firstAttributeName);
		Object object = attributeValues[attributeId];
		if (object instanceof NonNativeObjectInfo) {
			NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
			nnoi.setValueOf(OdbString.substring(attributeName, firstDotIndex + 1, attributeName.length()), aoi);
		}
		throw new ODBRuntimeException(NeoDatisError.CLASS_INFO_DO_NOT_HAVE_THE_ATTRIBUTE.addParameter(getClassInfo().getFullClassName())
				.addParameter(attributeName));
	}

	/**
	 * Return the oid of the object 
	 * @return The oid
	 */
	public OID getOid() {
		if (getHeader() == null) {
			throw new ODBRuntimeException(NeoDatisError.UNEXPECTED_SITUATION.addParameter("Null Object Info Header"));
		}
		return getHeader().getOid();
	}

	/**
	 *  Sets the oid of the object
	 * @param oid
	 */
	public void setOid(OID oid) {
		if (getHeader() != null) {
			getHeader().setOid(oid);
		}
	}
	/** To indicate that this is a non native object info
	 * 
	 */
	public boolean isNonNativeObject() {
		return true;
	}

	public boolean isNull() {
		return false;
	}

	public void clear() {
		attributeValues = null;
	}

	/**
	 * Create a copy oh this meta object
	 * 
	 * @param onlyData
	 *            if true, only copy attributes values
	 * @return
	 */
	public AbstractObjectInfo createCopy(Map<OID, AbstractObjectInfo> cache, boolean onlyData) {
		NonNativeObjectInfo nnoi = (NonNativeObjectInfo) cache.get(objectHeader.getOid());
		if (nnoi != null) {
			return nnoi;
		}

		if (onlyData) {
			ObjectInfoHeader oih = new ObjectInfoHeader();
			nnoi = new NonNativeObjectInfo(object, classInfo, null, oih.getAttributesIdentification(), oih.getAttributeIds());
		} else {
			nnoi = new NonNativeObjectInfo(object, classInfo, null, objectHeader.getAttributesIdentification(), objectHeader
					.getAttributeIds());
			nnoi.getHeader().setOid(getHeader().getOid());
		}
		AbstractObjectInfo[] newAttributeValues = new AbstractObjectInfo[attributeValues.length];
		for (int i = 0; i < attributeValues.length; i++) {
			newAttributeValues[i] = attributeValues[i].createCopy(cache, onlyData);
		}
		nnoi.attributeValues = newAttributeValues;
		cache.put(objectHeader.getOid(), nnoi);

		return nnoi;
	}

	public void setAttributeValue(int attributeId, AbstractObjectInfo aoi) {
		attributeValues[attributeId - 1] = aoi;

		/*
		 * if (aoi.isNonNativeObject()) {
		 * allNonNativeObjects.add((NonNativeObjectInfo) aoi); } else if
		 * (aoi.isGroup()) { // isGroup = if IsCollection || IsMap || IsArray
		 * GroupObjectInfo goi = (GroupObjectInfo) aoi;
		 * allNonNativeObjects.addAll(goi.getNonNativeObjects()); }
		 */

	}

	public AbstractObjectInfo[] getAttributeValues() {
		return attributeValues;
	}

	public int getMaxNbattributes() {
		return maxNbattributes;
	}

	/**
	 * The performance of this method is bad. But it is not used by the engine,
	 * only in the ODBExplorer
	 * 
	 * @param aoi
	 * @return
	 */
	public int getAttributeId(AbstractObjectInfo aoi) {
		for (int i = 0; i < attributeValues.length; i++) {
			if (aoi == attributeValues[i]) {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * Return the position where the position of an attribute is stored.
	 * 
	 * <pre>
	 * 	If a object has 3 attributes and if it is stored at position x
	 * Then the number of attributes (3) is stored at x+StorageEngineConstant.OBJECT_OFFSET_NB_ATTRIBUTES
	 * and first attribute id definition is stored at x+StorageEngineConstant.OBJECT_OFFSET_NB_ATTRIBUTES+size-of(int)
	 * and first attribute position is stored at x+StorageEngineConstant.OBJECT_OFFSET_NB_ATTRIBUTES+size-of(int)+size-of(int)
	 * 
	 * the second attribute id is stored at x+StorageEngineConstant.OBJECT_OFFSET_NB_ATTRIBUTES+size-of(int)+size-of(int)+size-of(long)
	 * the second attribute position is stored at x+StorageEngineConstant.OBJECT_OFFSET_NB_ATTRIBUTES+size-of(int)+size-of(int)+size-of(long)+size-of(int)
	 * 
	 * <pre>
	 * FIXME Remove dependency of StorageEngineConstant!
	 * 
	 * @param attributeId
	 * @return The position where this attribute is stored
	 */
	public long getAttributeDefinitionPosition(int attributeId) {
		long offset = StorageEngineConstant.OBJECT_OFFSET_NB_ATTRIBUTES;
		// delta =
		// Skip NbAttribute (int) +
		// Delta attribute (attributeId-1) * attribute definition size =
		// INT+LONG
		// Skip attribute Id (int)
		long delta = ODBType.INTEGER.getSize() + (attributeId - 1) * (ODBType.INTEGER.getSize() + ODBType.LONG.getSize())
				+ ODBType.INTEGER.getSize();
		return getPosition() + offset + delta;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public int hashCode() {
		// This happens when the object is deleted
		if (objectHeader == null) {
			return -1;
		}
		return objectHeader.hashCode();
	}

	/*
	 * public IOdbList<NonNativeObjectInfo> getAllNonNativeAttributes() { return
	 * allNonNativeObjects; }
	 */

	/**
	 * @param header
	 */
	public void setHeader(ObjectInfoHeader header) {
		this.objectHeader = header;

	}

}
