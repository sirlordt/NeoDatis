package org.neodatis.odb.core.layers.layer3;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

/**
 * A callback interface - not used
 * @author osmadja
 *
 */
public interface IObjectWriterCallback {
	public void metaObjectHasBeenInserted(long oid, NonNativeObjectInfo nnoi);
	public void metaObjectHasBeenUpdated(long oid, NonNativeObjectInfo nnoi);
}
