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
package org.neodatis.odb.core.server.message;

import java.util.List;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.odb.impl.core.server.ReturnValue;

/** A StoreMessage is used by the Client/Server mode to store an object
 * 
 * @author olivier s
 *
 */
public class StoreMessage extends Message {
	private NonNativeObjectInfo nnoi;
	private OID [] clientIds;
	
	public StoreMessage(String baseId, String connectionId, NonNativeObjectInfo nnoi,OID [] localOids){
		super(Command.STORE, baseId,connectionId);
		this.nnoi = nnoi;		
		this.clientIds = localOids;
	}

	public NonNativeObjectInfo getNnoi() {
		return nnoi;
	}

	public OID[] getClientIds() {
		return clientIds;
	}
	
	public String toString() {
		return "Store";
	}


	

}
