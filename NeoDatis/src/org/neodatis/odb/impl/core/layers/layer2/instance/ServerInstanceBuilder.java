package org.neodatis.odb.impl.core.layers.layer2.instance;

import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.transaction.ISession;

public class ServerInstanceBuilder extends InstanceBuilder {

	public ServerInstanceBuilder(IStorageEngine engine) {
		super(engine);
	}
	protected ISession getSession() {
		return engine.getSession(true);
	}
}
