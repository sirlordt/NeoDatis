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

import org.neodatis.odb.core.server.trigger.ServerDeleteTrigger;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;
import org.neodatis.odb.core.server.trigger.ServerSelectTrigger;
import org.neodatis.odb.core.server.trigger.ServerUpdateTrigger;
import org.neodatis.odb.core.trigger.OIDTrigger;

public interface ODBServer {
	/**
	 * Adds a base to the server. If the base does not exist, it will be
	 * created. Can be called after server start.
	 * 
	 * @param baseIdentifier
	 *            The name that the client must use to reference this base
	 * @param fileName
	 *            The physical file name of this base
	 */
	void addBase(String baseIdentifier, String fileName);

	/**
	 * Adds a base to the server. If the base does not exist, it will be
	 * created. Can be called after server start.
	 * 
	 * @param baseIdentifier
	 * @param fileName
	 *            The name that the client must use to reference this base
	 * @param user
	 *            The user that will be used to open the database
	 * @param password
	 *            The password that will be used to open the base
	 */
	void addBase(String baseIdentifier, String fileName, String user, String password);

	/**
	 * Not yet implemented
	 * 
	 * @param baseIdentifier
	 * @param user
	 * @param password
	 */
	void addUserForBase(String baseIdentifier, String user, String password);

	/**
	 * actually starts the server. Starts listening incoming connections on the
	 * port.
	 * 
	 * @param inThread
	 *            If true, the server is started in an independent thread for
	 *            listening incoming connections, else it simply executes the
	 *            server (client connection) in the current thread
	 */
	void startServer(boolean inThread);

	/**
	 * Closes the server. Closes the socket server and all registered databases.
	 */
	void close();

	void setAutomaticallyCreateDatabase(boolean yes);

	ODB openClient(String baseIdentifier);

	/**
	 * Used to add an update trigger callback
	 * 
	 * @param trigger
	 */
	void addUpdateTrigger(String baseIdentifier, String className, ServerUpdateTrigger trigger);

	/**
	 * Used to add an insert trigger callback
	 * 
	 * @param trigger
	 */
	void addInsertTrigger(String baseIdentifier, String className, ServerInsertTrigger trigger);

	/**
	 * USed to add a delete trigger callback
	 * 
	 * @param trigger
	 */
	void addDeleteTrigger(String baseIdentifier, String className, ServerDeleteTrigger trigger);

	/**
	 * Used to add a select trigger callback
	 * 
	 * @param trigger
	 */
	void addSelectTrigger(String baseIdentifier, String className, ServerSelectTrigger trigger);
	
	void addOidTrigger(String baseIdentifier, String className, OIDTrigger trigger);
}
