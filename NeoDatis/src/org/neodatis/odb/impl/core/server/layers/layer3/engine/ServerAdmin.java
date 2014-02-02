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

import java.io.IOException;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.layers.layer3.engine.IMessageStreamer;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;


/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class ServerAdmin {
	
	private IMessageStreamer messageStreamer;
	private String host;

	private int port;


	public ServerAdmin(String host, int port) {
		this.host = host;
		this.port = port;
		messageStreamer = OdbConfiguration.getCoreProvider().getMessageStreamer(host,port,"ServerAdmin");
	}

	public void close() throws IOException {
		messageStreamer.close();
	}

	/**
	 * Opens socket send message and close.
	 * 
	 * @TODO This is bad,should keep the socket alive..
	 * 
	 * @param msg
	 * @return The response message @
	 */
	public Message sendMessage(Message msg) {

		
		Message rmsg;
		try {
			messageStreamer.write(msg);
			rmsg = messageStreamer.read();
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_NET_ERROR,e);
		}

		return rmsg;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
