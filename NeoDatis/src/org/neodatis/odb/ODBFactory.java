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
package org.neodatis.odb;

import org.neodatis.odb.impl.main.LocalODB;
import org.neodatis.odb.impl.main.ODBDefaultServer;
import org.neodatis.odb.impl.main.RemoteODBClient;
import org.neodatis.odb.impl.main.ThreadSafeLocalODB;

/**
 * The ODBFactory to obtain the right ODB implementation.
 * 
 * @author osmadja
 * 
 */
public class ODBFactory {

	/**
	 * A private constructor to avoid instantiation
	 * 
	 * 
	 */
	private ODBFactory() {
	}

	/**
	 * Open an ODB database protected by a user and password
	 * 
	 * @param fileName
	 *            The name of the ODB database
	 * @param user
	 *            The user of the database
	 * @param password
	 *            The password of the user
	 * @return The ODB database
	 */
	public static ODB open(String fileName, String user, String password) {
		ODB odBase = LocalODB.getInstance(fileName, user, password);
		
		if(OdbConfiguration.isMultiThread()){
			odBase = new ThreadSafeLocalODB(odBase);
		}
		return odBase;
	}

	/**
	 * Open a non password protected ODB database
	 * 
	 * @param fileName
	 *            The ODB database name
	 * @return A local ODB implementation
	 */
	public static ODB open(String fileName) {
		ODB odBase = LocalODB.getInstance(fileName);

		if(OdbConfiguration.isMultiThread()){
			odBase = new ThreadSafeLocalODB(odBase);
		}

		return odBase;
	}

	/**
	 * Open an ODB server on the specific port. This will the socketServer on
	 * the specified port. Must call startServer of the ODBServer to actually
	 * start the server
	 * 
	 * @param port
	 *            The server port
	 * @return The server
	 */
	public static ODBServer openServer(int port) {
		return new ODBDefaultServer(port);
	}

	/**
	 * Open an ODB Client
	 * 
	 * @param hostName
	 * @param port
	 * @param baseIdentifier
	 *            The base identifier : The alias used by the server to declare
	 *            database
	 * @return The ODB
	 */
	public static ODB openClient(String hostName, int port, String baseIdentifier) {
		return new RemoteODBClient(hostName, port, baseIdentifier);
	}

	/**
	 * 
	 * @param hostName
	 * @param port
	 * @param baseIdentifier
	 *            The base identifier : The alias used by the server to declare
	 *            database
	 * @param user
	 *            Remote access user
	 * @param password
	 *            Remote access password
	 * @return The ODB
	 */
	public static ODB openClient(String hostName, int port, String baseIdentifier, String user, String password) {
		return new RemoteODBClient(hostName, port, baseIdentifier, user, password);
	}

}
