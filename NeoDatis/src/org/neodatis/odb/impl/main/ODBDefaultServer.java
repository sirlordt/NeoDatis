package org.neodatis.odb.impl.main;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.core.server.trigger.ServerDeleteTrigger;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;
import org.neodatis.odb.core.server.trigger.ServerSelectTrigger;
import org.neodatis.odb.core.server.trigger.ServerUpdateTrigger;
import org.neodatis.odb.core.trigger.OIDTrigger;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ODBServerImpl;

public class ODBDefaultServer implements ODBServer {
	protected ODBServer serverImpl;

	public ODBDefaultServer(int port) {
		this.serverImpl = new ODBServerImpl(port);
	}

	public void addBase(String baseIdentifier, String fileName) {
		serverImpl.addBase(baseIdentifier, fileName);
	}

	public void addBase(String baseIdentifier, String fileName, String user, String password) {
		serverImpl.addBase(baseIdentifier, fileName, user, password);
	}

	public void addUserForBase(String baseIdentifier, String user, String password) {
		serverImpl.addUserForBase(baseIdentifier, user, password);
	}

	public void close() {
		serverImpl.close();
	}

	public ODB openClient(String baseIdentifier) {
		return serverImpl.openClient(baseIdentifier);
	}

	public void setAutomaticallyCreateDatabase(boolean yes) {
		serverImpl.setAutomaticallyCreateDatabase(yes);
	}

	public void startServer(boolean inThread) {
		serverImpl.startServer(inThread);
	}

	public void addDeleteTrigger(String baseIdentifier, String className, ServerDeleteTrigger trigger) {
		serverImpl.addDeleteTrigger(baseIdentifier, className, trigger);
	}

	public void addInsertTrigger(String baseIdentifier, String className, ServerInsertTrigger trigger) {
		serverImpl.addInsertTrigger(baseIdentifier, className, trigger);
	}
	public void addOidTrigger(String baseIdentifier, String className, OIDTrigger trigger) {
		serverImpl.addOidTrigger(baseIdentifier, className, trigger);
	}

	public void addSelectTrigger(String baseIdentifier, String className, ServerSelectTrigger trigger) {
		serverImpl.addSelectTrigger(baseIdentifier, className, trigger);

	}

	public void addUpdateTrigger(String baseIdentifier, String className, ServerUpdateTrigger trigger) {
		serverImpl.addUpdateTrigger(baseIdentifier, className, trigger);

	}

}
