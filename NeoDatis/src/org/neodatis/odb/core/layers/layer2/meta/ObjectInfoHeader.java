
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

import org.neodatis.odb.OID;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * Some basic info about an object info like position, its class info,...
 * @author osmadja
 *
 */
public class ObjectInfoHeader implements Serializable {
	private long position;
	private OID previousObjectOID;
	private OID nextObjectOID;
    private OID classInfoId;
    /** Can be position(for native object) or id(for non native object, positions are positive e ids are negative*/
    private long [] attributesIdentification;
    private int [] attributeIds;
    private OID oid;
    private long creationDate;
    private long updateDate;
    private int objectVersion;

    public ObjectInfoHeader(long position, OID previousObjectOID, OID nextObjectOID, OID classInfoId, long [] attributesIdentification, int[] attributeIds) {
		this.position = position;
		this.oid = null;
		this.previousObjectOID = previousObjectOID;
		this.nextObjectOID = nextObjectOID;
		this.classInfoId = classInfoId;
        this.attributesIdentification = attributesIdentification;
        this.attributeIds = attributeIds;
        this.objectVersion = 1;
        this.creationDate = OdbTime.getCurrentTimeInMs();
    }
	public ObjectInfoHeader() {
		super();
		this.position = -1;
		this.oid = null;
		this.objectVersion = 1;
		this.creationDate = OdbTime.getCurrentTimeInMs();
	}
	public int getNbAttributes(){
		return attributesIdentification.length;
	}
	public OID getNextObjectOID() {
		return nextObjectOID;
	}
	public void setNextObjectOID(OID nextObjectOID) {
		this.nextObjectOID = nextObjectOID;
	}
	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}
    
//	/**
//     * @return Returns the classInfoId.
//     */
//    public long getClassInfoId() {
//        return classInfoId;
//    }
//    /**
//     * @param classInfoId The classInfoId to set.
//     */
//    public void setClassInfoId(long classInfoId) {
//        this.classInfoId = classInfoId;
//    }
    public OID getPreviousObjectOID() {
		return previousObjectOID;
	}
	public void setPreviousObjectOID(OID previousObjectOID) {
		this.previousObjectOID = previousObjectOID;
	}
	public OID getClassInfoId() {
		return classInfoId;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("oid=").append(oid).append(" - ");//.append("class info id=").append(classInfoId);
        buffer.append(" - position=").append(position).append(" | prev=").append(previousObjectOID);
        buffer.append(" | next=").append(nextObjectOID);
        buffer.append(" attrs =[");
        if(attributesIdentification!=null){
            for(int i=0;i<attributesIdentification.length;i++){
                buffer.append(attributesIdentification[i]).append( " ");
            }
        }else{
            buffer.append(" nulls ");
        }
        buffer.append(" ]");
        return buffer.toString();
    }


	public String oidsToString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(" [ prev=").append(String.valueOf(previousObjectOID)).append(" <- ");//.append("class info id=").append(classInfoId);
        buffer.append("oid=").append(String.valueOf(oid));
        buffer.append(" -> next=").append(String.valueOf(nextObjectOID)).append(" ] " );
        return buffer.toString();
    }

    public long[] getAttributesIdentification() {
        return attributesIdentification;
    }

    public void setAttributesIdentification(long[] attributesIdentification) {
        this.attributesIdentification = attributesIdentification;
    }

    public OID getOid() {
        return oid;
    }

    public void setOid(OID oid) {
        this.oid = oid;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    /** Return the attribute identification (position or id) from the attribute id
     * 
     *FIXME Remove dependency from StorageEngineConstant
     * @param attributeId
     * @return -1 if attribute with this id does not exist
     */
    public long getAttributeIdentificationFromId(int attributeId){
        if(attributeIds==null){
        	return StorageEngineConstant.NULL_OBJECT_ID_ID;
        }
    	for(int i=0;i<attributeIds.length;i++){
            if(attributeIds[i]==attributeId){
                return attributesIdentification[i];
            }
        }
        return StorageEngineConstant.NULL_OBJECT_ID_ID;
    }
    public long getAttributeId(int attributeIndex){
        return attributeIds[attributeIndex];
    }
	public void setAttributesIds(int[] ids) {
		attributeIds = ids;		
	}
	public int[] getAttributeIds() {
		return attributeIds;
	}
	
	public void setClassInfoId(OID classInfoId2) {
		this.classInfoId = classInfoId2;		
	}
	public int getObjectVersion() {
		return objectVersion;
	}
	public void setObjectVersion(int objectVersion) {
		this.objectVersion = objectVersion;
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (position ^ (position >>> 32));
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ObjectInfoHeader other = (ObjectInfoHeader) obj;
		if (position != other.position)
			return false;
		return true;
	}
	public void incrementVersionAndUpdateDate(){
		objectVersion++;
		updateDate = OdbTime.getCurrentTimeInMs();
		
	}
	
	public ObjectInfoHeader duplicate(){
		ObjectInfoHeader oih = new ObjectInfoHeader();
		oih.setAttributesIdentification(attributesIdentification);
		oih.setAttributesIds(attributeIds);
		oih.setClassInfoId(classInfoId);
		oih.setCreationDate(creationDate);
		oih.setNextObjectOID(nextObjectOID);
		oih.setObjectVersion(objectVersion);
		oih.setOid(oid);
		oih.setPosition(position);
		oih.setPreviousObjectOID(previousObjectOID);
		oih.setUpdateDate(updateDate);
		return oih;
		
	}
	
}

