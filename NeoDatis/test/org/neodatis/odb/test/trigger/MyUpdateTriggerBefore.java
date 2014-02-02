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
package org.neodatis.odb.test.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.trigger.UpdateTrigger;

public class MyUpdateTriggerBefore extends UpdateTrigger {

	public void afterInsert(Object object, OID oid) {
	}

	public boolean beforeInsert(Object object) {
		SimpleObject so = (SimpleObject) object;
		// just add 1
		so.setId(so.getId() + 1);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.UpdateTrigger#afterUpdate(org.neodatis.
	 * odb.ObjectRepresentation, java.lang.Object, org.neodatis.odb.OID)
	 */
	@Override
	public void afterUpdate(ObjectRepresentation oldObjectRepresentation, Object newObject, OID oid) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.UpdateTrigger#beforeUpdate(org.neodatis
	 * .odb.ObjectRepresentation, java.lang.Object, org.neodatis.odb.OID)
	 */
	@Override
	public boolean beforeUpdate(ObjectRepresentation oldObjectRepresentation, Object newObject, OID oid) {
		SimpleObject so = (SimpleObject) newObject;
		// just add 1
		so.setId(so.getId() + 1);
		return true;
	}

}
