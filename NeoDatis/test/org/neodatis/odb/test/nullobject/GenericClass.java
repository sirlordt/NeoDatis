package org.neodatis.odb.test.nullobject;

public class GenericClass {
	private Object object;
	private Object[] objects;

	public GenericClass(Object object) {
		super();
		this.object = object;
		this.objects = new Object[10];
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object[] getObjects() {
		return objects;
	}

	public void setObjects(Object[] objects) {
		this.objects = objects;
	}

}
