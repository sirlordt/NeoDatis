package org.neodatis.odb.core.mock;

import java.util.List;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * For test purpose
 * 
 * @author osmadja
 * @sharpen.ignore
 * 
 */
public class MockObjectReader implements IObjectReader {

	public MockObjectReader(IStorageEngine engine) {

	}

	public MetaModel readMetaModel(MetaModel metaModel, boolean full) {
		return new SessionMetaModel();
	}

	public Object buildOneInstance(NonNativeObjectInfo objectInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public List getAllIdInfos(String objectTypeToDisplay, byte idType, boolean displayObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAllIds(byte idType) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBaseIdentification() {
		// TODO Auto-generated method stub
		return null;
	}

	public OID getIdOfObjectAt(long position, boolean includeDeleted) {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstanceBuilder getInstanceBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	public OID getNextObjectOID(OID oid) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObjectFromOid(OID oid, boolean returnInstance, boolean useCache) {
		// TODO Auto-generated method stub
		return null;
	}

	public Objects getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnObjects,
			IMatchingObjectAction queryResultAction) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getObjectPositionFromItsOid(OID oid, boolean useCache, boolean throwException) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Objects getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public Values getValues(IValuesQuery query, int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomicNativeObjectInfo readAtomicNativeObjectInfo(long position, int odbTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object readAtomicNativeObjectInfoAsObject(long position, int odbTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	public IOdbList<ClassInfoIndex> readClassInfoIndexesAt(long position, ClassInfo classInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public void readDatabaseHeader(String user, String password) {
		// TODO Auto-generated method stub

	}

	public NonNativeObjectInfo readNonNativeObjectInfoFromOid(ClassInfo classInfo, OID oid, boolean useCache, boolean returnObjects) {
		// TODO Auto-generated method stub
		return null;
	}

	public NonNativeObjectInfo readNonNativeObjectInfoFromPosition(ClassInfo classInfo, OID oid, long position, boolean useCache,
			boolean returnInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectInfoHeader readObjectInfoHeaderFromOid(OID oid, boolean useCache) {
		// TODO Auto-generated method stub
		return null;
	}

	public AttributeValuesMap readObjectInfoValuesFromOID(ClassInfo classInfo, OID oid, boolean useCache, IOdbList<String> attributeNames,
			IOdbList<String> relationAttributeNames, int recursionLevel, String[] orderByFields, boolean useOidForObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public long readOidPosition(OID oid) {
		// TODO Auto-generated method stub
		return 0;
	}

}
