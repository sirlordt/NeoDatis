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
package org.neodatis.odb.impl.core.server.trigger;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.trigger.DefaultTriggerManager;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class DefaultServerTriggerManager extends DefaultTriggerManager {

	/**
	 * @param engine
	 */
	public DefaultServerTriggerManager(IStorageEngine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	protected boolean isNull(Object object){
		if(object==null){
			return true;
		}
		if(object instanceof NonNativeObjectInfo){
			NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
			return nnoi.isNull();
		}
		return false;
	}
	
	public Object transform(Object object) {
		DefaultObjectRepresentation dor = new DefaultObjectRepresentation((NonNativeObjectInfo) object);
		dor.addObserver(getStorageEngine().getSession(true));
		return dor;
	}
	
}
