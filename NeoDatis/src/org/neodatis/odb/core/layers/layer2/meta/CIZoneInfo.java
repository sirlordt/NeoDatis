
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

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;

/**
 * Class keep track of object pointers and number of objects of a class info for
 * a specific zone
 * 
 * <pre>
 * 	For example, to keep track of first committed and last committed object position. 
 * </pre>
 * 
 * @author osmadja
 * 
 */
public class CIZoneInfo implements Serializable {
	public OID first;

	public OID last;

	protected long nbObjects;
	
	public ClassInfo ci;

	public CIZoneInfo(ClassInfo ci, OID first, OID last, long nbObjects) {
		super();
		this.first = first;
		this.last = last;
		this.nbObjects = nbObjects;
		this.ci = ci;
	}

	public String toString() {
		return "(first=" + first + ",last=" + last + ",nb=" + nbObjects + ")";
	}

	public void reset() {
		first = null;
		last = null;
		nbObjects = 0;

	}

	public void set(CIZoneInfo zoneInfo) {
	
		this.nbObjects = zoneInfo.nbObjects;
		this.first = zoneInfo.first;
		this.last = zoneInfo.last;
	}

	public void decreaseNbObjects() {
		
		nbObjects--;
		if(nbObjects<0){
			throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("nb objects is negative! in "+ci.getFullClassName()));
		}
	}

	public void increaseNbObjects() {
		nbObjects++;
	}

	public long getNbObjects() {
		return nbObjects;
	}
	public boolean hasObjects(){
		return nbObjects!=0;
	}

	public void setNbObjects(long nb) {
		this.nbObjects = nb;
	}
}
