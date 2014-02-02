package org.neodatis.odb.impl.core.server.layers.layer1;

import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer1.introspector.LocalObjectIntrospector;

public class ServerObjectIntrospector extends LocalObjectIntrospector {

	public ServerObjectIntrospector(IStorageEngine storageEngine) {
		super(storageEngine);
	}
	
}
