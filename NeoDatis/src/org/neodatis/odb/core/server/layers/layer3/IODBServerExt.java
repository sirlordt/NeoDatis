package org.neodatis.odb.core.server.layers.layer3;

import java.util.Map;

import org.neodatis.odb.ODBServer;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;

public interface IODBServerExt extends ODBServer {

	public void startServer(boolean inThread);

	public Map getConnectionManagers();

	public IOSocketParameter getParameters(String baseIdentifier, boolean clientAndServerRunsInSameVM);

}