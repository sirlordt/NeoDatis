package org.neodatis.odb.core.layers.layer2.instance;

import java.lang.reflect.Constructor;

public interface IClassPool {

	Class getClass(String className);

	Constructor getConstructor(String className);

	void addConstructor(String className, Constructor constructor);

	public void reset();

}