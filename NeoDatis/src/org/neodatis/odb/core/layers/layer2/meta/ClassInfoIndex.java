
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

import org.neodatis.btree.IBTree;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.query.execution.IndexTool;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * An index of a class info
 * @author osmadja
 *
 */
public class ClassInfoIndex implements Serializable{
	public static final byte ENABLED = 1;
	public static final byte DISABLED = 2;
	
	private OID classInfoId;
	private String name;
	private byte status;
	private boolean isUnique;
	private long creationDate;
	private long lastRebuild;
	private int [] attributeIds;
	private IBTree btree;
	
	public OID getClassInfoId() {
		return classInfoId;
	}
	public void setClassInfoId(OID classInfoId) {
		this.classInfoId = classInfoId;
	}
	public int[] getAttributeIds() {
		return attributeIds;
	}
	public void setAttributeIds(int[] attributeIds) {
		this.attributeIds = attributeIds;
	}
	public long getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	public boolean isUnique() {
		return isUnique;
	}
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	public long getLastRebuild() {
		return lastRebuild;
	}
	public void setLastRebuild(long lastRebuild) {
		this.lastRebuild = lastRebuild;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public int getAttributeId(int index){
		return attributeIds[index];
	}
	public void setBTree(IBTree btree) {
		this.btree = btree;		
	}
	public IBTree getBTree() {
		return this.btree;		
	}
	public OdbComparable computeKey(NonNativeObjectInfo nnoi) {
		return IndexTool.buildIndexKey(name, nnoi, attributeIds);
	}
	public int getNbAttributes() {
		return attributeIds.length;
	}
	
	/**
	 * Check if a list of attribute can use the index  
	 * @param attributeIdsToMatch
	 * @return true if the list of attribute can use this index
	 */
	public boolean matchAttributeIds(int[] attributeIdsToMatch){
		//TODO an index with lesser attribute than the one to match can be used
		if(attributeIds.length!=attributeIdsToMatch.length){
			return false;
		}
		boolean found = false;
		for(int i=0;i<attributeIdsToMatch.length;i++){
			found = false;
			for(int j=0;j<attributeIds.length;j++){
				if(attributeIds[j]==attributeIdsToMatch[i]){
					found = true;
					break;
				}
			}
			if(!found){
				return false;
			}
		}
		return true;
	}
	
}
