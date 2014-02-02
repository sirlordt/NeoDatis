package org.neodatis.odb.impl.core.server.layers.layer3.engine;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IObjectWriterCallback;
import org.neodatis.tool.DLogger;

public class ServerObjectWriterCallback implements IObjectWriterCallback {

	public void metaObjectHasBeenInserted(long oid, NonNativeObjectInfo nnoi) {
		DLogger.info("Object " + nnoi + " has been inserted with id " + oid);

	}

	public void metaObjectHasBeenUpdated(long oid, NonNativeObjectInfo nnoi) {
		DLogger.info("Object " + nnoi + " has been updated with id " + oid);

	}

}
