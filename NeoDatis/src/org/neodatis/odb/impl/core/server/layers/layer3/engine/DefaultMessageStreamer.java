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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.layers.layer3.engine.IMessageStreamer;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.tool.wrappers.OdbString;

/**
 * @sharpen.ignore
 * @author olivier
 * 
 */
public class DefaultMessageStreamer implements IMessageStreamer {
	private OutputStream out;

	private InputStream in;

	private ObjectOutputStream oos;

	private ObjectInputStream ois;

	private Socket socket;

	private String host;
	private int port;
	private String name;

	private boolean isClosed;
	

	public DefaultMessageStreamer(String host, int port, String name) {
		this.host = host;
		this.port = port;
		this.name = name;
		initSocket();
	}

	public DefaultMessageStreamer(Socket socket) throws IOException {
		this.socket = socket;
		out = socket.getOutputStream();
		in = socket.getInputStream();

		oos = new ObjectOutputStream(out);
		ois = new ObjectInputStream(new BufferedInputStream(in));
	}

	private void initSocket() {
		if (socket == null) {
			try {

				socket = new Socket(host, port);
				socket.setTcpNoDelay(true);
				out = socket.getOutputStream();
				in = socket.getInputStream();
				oos = new ObjectOutputStream(new BufferedOutputStream(out));
				ois = new ObjectInputStream(new BufferedInputStream(in));

			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.CLIENT_NET_ERROR, e);
			}
		}
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(name));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.server.layers.layer3.engine.IMessageStreamer
	 * #close()
	 */
	public void close() {
		try {
			oos.flush();
			oos.close();
			ois.close();
			out.close();
			in.close();
			isClosed = true;
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_NET_ERROR, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.server.layers.layer3.engine.IMessageStreamer
	 * #write(org.neodatis.odb.core.server.layers.layer3.engine.Message)
	 */
	public void write(Message message) throws Exception {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (Exception e) {
			throw e;
		}

	}

	public void clearCache(){
		try {
			oos.reset();
		} catch (IOException e) {
			throw new ODBRuntimeException(NeoDatisError.NET_SERIALISATION_ERROR.addParameter(OdbString.exceptionToString(e, true)));
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.server.layers.layer3.engine.IMessageStreamer
	 * #read()
	 */
	public Message read() throws Exception {
		try {
			return (Message) ois.readObject();
		} catch (Exception e) {
			throw e;
		}
	}

}
