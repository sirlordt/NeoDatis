
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
package org.neodatis.odb.impl.core.layers.layer3.oid;

import org.neodatis.odb.OID;


/**Used to obtain internal infos about all database ids
 * 
 * @author osmadja
 *
 */
public class FullIDInfo {
	private long id;
	private long position;
	private long blockId;
	private byte idStatus;
	
	private String objectClassName;
	private String objectToString;
	private OID prevOID;
	private OID nextOID;
	 
	public FullIDInfo(long id, long position, byte idStatus, long blockId, String objectClassName,String objectToString,OID prevOID,OID nextOID) {
		this.id = id;
		this.position = position;
		this.blockId = blockId;
		this.objectClassName = objectClassName;
		this.objectToString = objectToString;
		this.idStatus = idStatus;
		this.prevOID = prevOID;
		this.nextOID = nextOID;
	}

	public long getBlockId() {
		return blockId;
	}

	public void setBlockId(long blockId) {
		this.blockId = blockId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getObjectClassName() {
		return objectClassName;
	}

	public void setObjectClassName(String objectClassName) {
		this.objectClassName = objectClassName;
	}
	
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Id=").append(id).append(" - Posi=").append(position).append(" - Status=").append(idStatus).append(" - Block Id=").append(blockId);
		buffer.append(" - Type=").append(objectClassName);
		buffer.append(" - prev inst. pos=").append(prevOID);
		buffer.append(" - next inst. pos=").append(nextOID);
		buffer.append(" - Object=").append(objectToString);		
		return buffer.toString();
	}
	public static void main(String[] args) {
		FullIDInfo ii = new FullIDInfo(1,1,(byte)1,1,"","",null,null);
		ii.setObjectClassName("ola");
		System.out.println("ll="+ii.getObjectClassName());
	}

}
