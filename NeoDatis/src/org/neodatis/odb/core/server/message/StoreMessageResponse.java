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
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.odb.impl.core.server.ReturnValue;


/**
 * A StoreMessageResponse is used by the Client/Server mode to answer a StoreMessage
 * 
 * @author olivier s
 * 
 */
public class StoreMessageResponse extends Message {
	private OID oid;
	private OID [] clientIds;
	private OID [] serverIds;
	/** Values the server wants to return to the client*/
	private List<ReturnValue> returnValues;

	private boolean newObject;
	public StoreMessageResponse(String baseId, String connectionId, String error) {
		super(Command.STORE, baseId,connectionId);
		setError(error);
	}
	public StoreMessageResponse(String baseId, String connectionId, OID oid, boolean newObject,OID [] clientIds, OID [] serverIds, List<ReturnValue> returnValues) {
		super(Command.STORE, baseId,connectionId);
		this.oid = oid;
		this.newObject = newObject;
		this.clientIds = clientIds;
		this.serverIds = serverIds;
		this.returnValues = returnValues;
	}

	public boolean isNewObject() {
		return newObject;
	}

	public OID getOid() {
		return oid;
	}
	public OID[] getClientIds() {
		return clientIds;
	}
	public OID[] getServerIds() {
		return serverIds;
	}
	public List<ReturnValue> getReturnValues() {
		return returnValues;
	}

}
