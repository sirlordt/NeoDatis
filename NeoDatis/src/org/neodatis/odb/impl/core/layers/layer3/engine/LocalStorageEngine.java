package org.neodatis.odb.impl.core.layers.layer3.engine;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.engine.AbstractStorageEngine;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;

public class LocalStorageEngine extends AbstractStorageEngine {
	
	protected ISession session;

	public LocalStorageEngine(IBaseIdentification parameters) {
		super(parameters);
	}

	public ISession buildDefaultSession() {
		 session = OdbConfiguration.getCoreProvider().getLocalSession(this);	
		 return session;
	}

	public ISession getSession(boolean throwExceptionIfDoesNotExist) {
		return session;
	}
	
	public ClassInfoList addClasses(ClassInfoList classInfoList) {
		return getObjectWriter().addClasses(classInfoList);
	}
	
	public IObjectIntrospector buildObjectIntrospector() {
		return provider.getLocalObjectIntrospector(this);
	}

	public IObjectWriter buildObjectWriter() {
		return provider.getClientObjectWriter(this);
	}

	public IObjectReader buildObjectReader() {
		return provider.getClientObjectReader(this);
	}
	public ITriggerManager buildTriggerManager(){
    	return provider.getLocalTriggerManager(this);
    }

	

	
}
