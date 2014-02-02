package org.neodatis.odb.core.transaction;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

/** An interface for temporary cache*/
public interface ITmpCache {

	public abstract NonNativeObjectInfo getReadingObjectInfoFromOid(OID oid);
	public boolean isReadingObjectInfoWithOid(OID oid);
	public abstract void startReadingObjectInfoWithOid(OID oid, NonNativeObjectInfo objectInfo);

	public abstract void clearObjectInfos();
	public int size();

}