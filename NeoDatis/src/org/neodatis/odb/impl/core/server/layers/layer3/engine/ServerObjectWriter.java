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

import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.server.layers.layer2.meta.ClientNonNativeObjectInfo;
import org.neodatis.odb.core.server.layers.layer3.engine.IServerStorageEngine;
import org.neodatis.odb.core.server.layers.layer3.engine.ServerFileSystemInterface;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.impl.core.server.layers.layer3.oid.DefaultServerIdManager;
import org.neodatis.odb.impl.core.server.transaction.ServerSession;

public class ServerObjectWriter extends AbstractObjectWriter {

	private ISessionManager sessionManager;

	public ServerObjectWriter(IStorageEngine engine) {
		super(engine);
		this.sessionManager = OdbConfiguration.getCoreProvider().getClientServerSessionManager();
	}

	IServerStorageEngine getEngine(){
		return (IServerStorageEngine) storageEngine;
	}
	public void initIdManager() {
		this.idManager = new DefaultServerIdManager(this, objectReader, storageEngine.getCurrentIdBlockPosition(), storageEngine
				.getCurrentIdBlockNumber(), storageEngine.getCurrentIdBlockMaxOid());
	}

	public OID writeNonNativeObjectInfo(OID existingOid, NonNativeObjectInfo objectInfo, long position, boolean writeDataInTransaction,
			boolean isNewObject) {

		// To enable object auto-reconnect on the server side
		if (OdbConfiguration.reconnectObjectsToSession()
				&& objectInfo.getHeader().getOid() != null) {
			ServerSession session = (ServerSession) sessionManager.getSession(storageEngine.getBaseIdentification().getIdentification(),
					true);
			ObjectInfoHeader oih = session.getCache().getObjectInfoHeaderFromOid(objectInfo.getOid(), false);
			// only add in th cache if object does not exist in the cache
			if (oih == null) {
				session.getCache().addObjectInfo(objectInfo.getHeader());
			}
		}

		OID roid = super.writeNonNativeObjectInfo(existingOid, objectInfo, position, writeDataInTransaction, isNewObject);
		if (objectInfo instanceof ClientNonNativeObjectInfo) {
			ClientNonNativeObjectInfo cnnoi = (ClientNonNativeObjectInfo) objectInfo;
			ServerSession session = (ServerSession) getSession();
			session.associateIds(cnnoi, roid, cnnoi.getLocalOid());
			// Adds the abstract Objectinfo in the cache
			session.getCache().addObjectInfo(cnnoi.getHeader());
		}

		return roid;
	}

	public OID updateNonNativeObjectInfo(NonNativeObjectInfo nnoi, boolean forceUpdate) {

		// To enable object auto-reconnect on the server side
		if (OdbConfiguration.reconnectObjectsToSession() && nnoi.getHeader().getOid() != null) {
			ServerSession session = (ServerSession) sessionManager.getSession(storageEngine.getBaseIdentification().getIdentification(),
					true);
			ObjectInfoHeader oih = session.getCache().getObjectInfoHeaderFromOid(nnoi.getOid(), false);
			// only add in th cache if object does not exist in the cache
			if (oih == null) {
				session.getCache().addObjectInfo(nnoi.getHeader());
			}
		}

		OID roid = super.updateNonNativeObjectInfo(nnoi, forceUpdate);
		if (nnoi instanceof ClientNonNativeObjectInfo) {
			ClientNonNativeObjectInfo cnnoi = (ClientNonNativeObjectInfo) nnoi;
			ServerSession session = (ServerSession) getSession();
			session.associateIds(cnnoi, cnnoi.getOid(), cnnoi.getLocalOid());
		}

		return roid;
	}

	/**
	 * FIXME check using a class variable to keep the base identification
	 * 
	 */
	public ISession getSession() {
		return (ServerSession) sessionManager.getSession(getEngine().getBaseIdentification().getIdentification(),true);
	}

	public IFileSystemInterface buildFSI() {
		return new ServerFileSystemInterface("server-data", storageEngine.getBaseIdentification(), true, OdbConfiguration
				.getDefaultBufferSizeForData());
	}

	protected ITriggerManager buildTriggerManager() {
		return OdbConfiguration.getCoreProvider().getServerTriggerManager(storageEngine);
	}

}
