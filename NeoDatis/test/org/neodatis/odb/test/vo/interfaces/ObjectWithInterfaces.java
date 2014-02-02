package org.neodatis.odb.test.vo.interfaces;

public class ObjectWithInterfaces {
	private Object attribute1;

	public Object getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(Object attribute1) {
		this.attribute1 = attribute1;
	}

	public ObjectWithInterfaces(Object attribute1) {
		super();
		this.attribute1 = attribute1;
	}

}
