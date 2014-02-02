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
package org.neodatis.odb.core.server.layers.layer2.meta;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;

public class ClientNonNativeObjectInfo extends NonNativeObjectInfo {

	protected OID localOid;
	
	public ClientNonNativeObjectInfo() {
	}

	public ClientNonNativeObjectInfo(ObjectInfoHeader oip, ClassInfo classInfo) {
		super(oip, classInfo);
	}

	public ClientNonNativeObjectInfo(ClassInfo classInfo) {
		super(classInfo);
	}

	public ClientNonNativeObjectInfo(Object object, ClassInfo info,
			AbstractObjectInfo[] values, long[] attributesIdentification,
			int[] attributeIds) {
		super(object, info, values, attributesIdentification, attributeIds);
	}

	public OID getLocalOid() {
		return localOid;
	}

	public void setLocalOid(OID localOid) {
		this.localOid = localOid;
	}

	
}
