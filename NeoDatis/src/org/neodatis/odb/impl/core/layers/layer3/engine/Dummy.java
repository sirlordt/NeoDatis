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
package org.neodatis.odb.impl.core.layers.layer3.engine;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.server.trigger.DefaultObjectRepresentation;
import org.neodatis.odb.impl.main.ODBAdapter;
import org.neodatis.odb.impl.main.ThreadSafeLocalODB;

/**
 * Undocumented class
 * @author osmadja
 *
 */
public class Dummy {
	public static IStorageEngine getEngine(ODB odb){
		
		if(odb instanceof ThreadSafeLocalODB){
			odb = ((ThreadSafeLocalODB) odb).getNonThreadSafeOdb();
		}
		if(odb instanceof ODBAdapter){
			ODBAdapter oa = (ODBAdapter) odb;
			return oa.getSession().getStorageEngine();
		}
		throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("getEngine not implemented for " + odb.getClass().getName()));
	}
	
	public static final NonNativeObjectInfo getNnoi(ObjectRepresentation objectRepresentation){
		if(objectRepresentation instanceof DefaultObjectRepresentation){
			NonNativeObjectInfo nnoi = ((DefaultObjectRepresentation) objectRepresentation).getNnoi();
			return nnoi;
		}
		throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("getNnoi not implemented for " + objectRepresentation.getClass().getName()));
	}


}
