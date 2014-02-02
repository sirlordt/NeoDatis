package org.neodatis.odb.core.mock;

import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.engine.AbstractStorageEngine;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class MockStorageEngine extends AbstractStorageEngine {
	protected ISession session;
	IObjectReader mockObjectReader;
	IObjectWriter mockObjectWriter;

	public MockStorageEngine() throws Exception {
		super(new MockBaseIdentification());
	}

	protected void init(String user, String password) {
		buildDefaultSession();
	}

	public IObjectWriter getObjectWriter() {
		return mockObjectWriter;
	}

	public IObjectReader getObjectReader() {
		return mockObjectReader;

	}

	public MetaModel getMetaModel() {
		return session.getMetaModel();
	}

	public ISession getSession(boolean throwExceptionIfDoesNotExist) {
		return session;
	}

	public ISession buildDefaultSession() {
		session = new MockSession("mock");
		return session;
	}

	public ClassInfoList addClasses(ClassInfoList classInfoList) {
		session.getMetaModel().addClasses(classInfoList);
		return classInfoList;
	}

	public IObjectIntrospector buildObjectIntrospector() {
		// TODO Auto-generated method stub
		return null;
	}

	public IObjectReader buildObjectReader() {
		mockObjectReader = new MockObjectReader(this);
		return mockObjectReader;
	}

	public IObjectWriter buildObjectWriter() {
		mockObjectWriter = new MockObjectWriter(this);
		return mockObjectWriter;
	}

	public ITriggerManager buildTriggerManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
