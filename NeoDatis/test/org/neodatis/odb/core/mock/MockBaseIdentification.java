package org.neodatis.odb.core.mock;

import org.neodatis.odb.core.layers.layer3.IBaseIdentification;

public class MockBaseIdentification implements IBaseIdentification {

	public boolean canWrite() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getIdentification() {
		return "mock";
	}

	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IBaseIdentification#getDirectory()
	 */
	public String getDirectory() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IBaseIdentification#getPassword()
	 */
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IBaseIdentification#getUserName()
	 */
	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

}
