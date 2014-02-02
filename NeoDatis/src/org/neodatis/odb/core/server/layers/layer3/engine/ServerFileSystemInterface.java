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
package org.neodatis.odb.core.server.layers.layer3.engine;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.engine.FileSystemInterface;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;

public class ServerFileSystemInterface extends FileSystemInterface {
	private ISessionManager sessionManager;

	public ServerFileSystemInterface(String name,
			IBaseIdentification parameters, boolean canLog, int bufferSize)
		 {
		super(name, parameters, canLog, bufferSize);
		sessionManager = OdbConfiguration.getCoreProvider().getClientServerSessionManager();
	}

	public ISession getSession() {
		return sessionManager.getSession(parameters.getIdentification(),true);
	}
}
