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
package org.neodatis.odb.impl.core.server.layers.layer3.oid;

import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.oid.DefaultIdManager;

public class DefaultServerIdManager extends DefaultIdManager {

	protected ISessionManager sessionManager;
	public DefaultServerIdManager(IObjectWriter objectWriter, IObjectReader objectReader, long currentBlockIdPosition, int currentBlockIdNumber,
			OID currentMaxId) {
		super(objectWriter, objectReader, currentBlockIdPosition, currentBlockIdNumber, currentMaxId);
		sessionManager = OdbConfiguration.getCoreProvider().getClientServerSessionManager();
	}
	protected ISession getSession() {
		return sessionManager.getSession(objectReader.getBaseIdentification(),true);
	}

}
