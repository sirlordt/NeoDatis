package org.neodatis.odb.impl.core.layers.layer3.engine;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;

public class LocalObjectWriter extends AbstractObjectWriter {
	private ISession session;
	
	public LocalObjectWriter(IStorageEngine engine) {
		super(engine);
		this.session = engine.getSession(true);
		
	}

	public ISession getSession() {
		return session;
	}
	
	public IFileSystemInterface buildFSI() {
        return new LocalFileSystemInterface("local-data", getSession(), storageEngine.getBaseIdentification(), true, OdbConfiguration.getDefaultBufferSizeForData());
    }
	
	protected ITriggerManager buildTriggerManager(){
    	return OdbConfiguration.getCoreProvider().getLocalTriggerManager(storageEngine);
    }
}
