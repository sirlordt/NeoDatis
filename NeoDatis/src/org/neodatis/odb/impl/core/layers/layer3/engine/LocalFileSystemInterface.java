package org.neodatis.odb.impl.core.layers.layer3.engine;

import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.engine.FileSystemInterface;
import org.neodatis.odb.core.transaction.ISession;

public class LocalFileSystemInterface extends FileSystemInterface {

	protected ISession session; 
	
	public LocalFileSystemInterface(String name, ISession session, String fileName, boolean canWrite, boolean canLog, int bufferSize) {
		super(name, fileName, canWrite, canLog, bufferSize);
		this.session = session;
	}

	public LocalFileSystemInterface(String name, ISession session, IBaseIdentification parameters, boolean canLog, int bufferSize) {
		super(name, parameters, canLog, bufferSize);
		this.session = session;
	}
	
	public ISession getSession() {
		return session;
	}

}
