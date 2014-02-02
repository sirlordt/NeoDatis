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
package org.neodatis.odb.impl.core.server.layers.layer3.engine;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.connection.SameVmConnection;
import org.neodatis.odb.core.server.layers.layer3.IODBServerExt;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.odb.core.server.message.CloseMessage;
import org.neodatis.odb.core.server.message.CloseMessageResponse;

/**
 * Client storage engine used when the client runs in the same Virtual machine
 * than the client. In this case ODB will not execute remote call via IO but it
 * will pass message (instead of sending them over the network. This can be very
 * useful for Web Application where Server and client use to run on the same VM.
 * 
 * @author osmadja
 * 
 */
public class SameVmClientEngine extends ClientStorageEngine {
	public static final String LOG_ID = "SameVmClientEngine";
	
	protected SameVmConnection connection;
	protected IODBServerExt server;
	
	
	public SameVmClientEngine(IODBServerExt server, String baseIdentifier)  {
		super(server.getParameters(baseIdentifier,true));
		this.server = server;
		// Call super class init
		super.initODBConnection();
	}

	public  Message sendMessage(Message msg) {
		checkConnection();
		return connection.manageMessage(msg);
	}
	
	protected void initMessageStreamer(){
		this.messageStreamer = null;
	}
	
	protected void initODBConnection()  {
		// Do nothing here
		// This is called by super class. But it is too early as 'server' attribute is not set yet 
	}

	private synchronized void checkConnection() {
		if(connection==null){
			connection = new SameVmConnection(parameters.getBaseIdentifier(), server,true);
		}		
	}
	
	public void close() {
		
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getIdentification()));
		}
		CloseMessage msg = new CloseMessage(parameters.getBaseIdentifier(), connectionId);
		CloseMessageResponse rmsg = (CloseMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while closing database :").addParameter(rmsg.getError()));
		}
		isClosed = true;
		provider.removeLocalTriggerManager(this);
	}
}
