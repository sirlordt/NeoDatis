package org.neodatis.odb.impl.main;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.ExternalOID;
import org.neodatis.odb.ODBExt;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.oid.ExternalObjectOID;

public class ODBExtImpl implements ODBExt {
	protected IStorageEngine engine;

	public ODBExtImpl(IStorageEngine storageEngine) {
		this.engine = storageEngine;
	}

	public ExternalOID convertToExternalOID(OID oid) {
		return new ExternalObjectOID(oid,engine.getDatabaseId());
	}

	public TransactionId getCurrentTransactionId() {
		return engine.getCurrentTransactionId();
	}

	public DatabaseId getDatabaseId() {
		return engine.getDatabaseId();
	}

	public ExternalOID getObjectExternalOID(Object object) {
		return convertToExternalOID(engine.getObjectId(object, true));
	}

	public int getObjectVersion(OID oid, boolean useCache) {
		ObjectInfoHeader oih = engine.getObjectInfoHeaderFromOid(oid,useCache);
		if(oih==null){
			throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE.addParameter(oid));
		}
		return oih.getObjectVersion();
	}
	public long getObjectCreationDate(OID oid) {
		ObjectInfoHeader oih = engine.getObjectInfoHeaderFromOid(oid, true);
		if(oih==null){
			throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE.addParameter(oid));
		}
		return oih.getCreationDate();
	}
	public long getObjectUpdateDate(OID oid, boolean useCache) {
		ObjectInfoHeader oih = engine.getObjectInfoHeaderFromOid(oid,useCache);
		if(oih==null){
			throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE.addParameter(oid));
		}
		return oih.getUpdateDate();
	}
	public OID replace(OID oid, Object o){
		ObjectInfoHeader oih = engine.getObjectInfoHeaderFromOid(oid, true);
		oih.setOid(oid);
		engine.getSession(true).getCache().addObject(oid, o, oih);
		return engine.store(oid, o);
	}
}
