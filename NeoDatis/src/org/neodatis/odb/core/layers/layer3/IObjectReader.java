package org.neodatis.odb.core.layers.layer3;

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
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.impl.core.layers.layer3.oid.FullIDInfo;
import org.neodatis.tool.wrappers.list.IOdbList;

public interface IObjectReader {

	/**
	 * Reads the database header
	 * 
	 * @param user
	 * @param password
	 */
	public abstract void readDatabaseHeader(String user, String password);

	/**
	 * Reads the database meta model
	 * 
	 * @param metaModel
	 *            An empty meta model
	 * @param full
	 *            To indicate if a full read must be done
	 * @return The modified metamodel
	 */
	public abstract MetaModel readMetaModel(MetaModel metaModel, boolean full);

	public abstract IOdbList<ClassInfoIndex> readClassInfoIndexesAt(long position,
			ClassInfo classInfo) ;

	public abstract NonNativeObjectInfo readNonNativeObjectInfoFromOid(
			ClassInfo classInfo, OID oid, boolean useCache,
			boolean returnObjects) ;

	/**
	 * reads some attributes of an object.
	 * 
	 * 
	 * <pre>
	 *     example of method call
	 *     readObjectInfoValues(classinfo,18000,true,[&quot;profile.name&quot;,&quot;profile.email&quot;],[&quot;profile.name&quot;,&quot;profile.email&quot;],0)
	 *     readObjectInfoValues(classinfo,21789,true,[&quot;name&quot;],[&quot;profile.name&quot;],1)
	 * </pre>
	 * 
	 * @param classInfo
	 *            If null, we are probably reading a native instance : String
	 *            for example
	 * @param oid
	 *            The oid of the object to read. if -1,the read will be done by
	 *            position
	 * @param useCache
	 *            To indicate if cache must be used. If not, the old version of
	 *            the object will read
	 * @param attributeNames
	 *            The names of attributes to read the values, an attributename
	 *            can contain relation like profile.name
	 * @param relationAttributeNames
	 *            The original names of attributes to read the values, an
	 *            attributename can contain relation like profile.name
	 * @param recursionLevel
	 *            The recursion level of this method call
	 * 
	 * @param orderByFields
	 * @param useOidForObject To indicate that if the object being read has an attribute that is not native, we will put the oid of the attribute (object) instead of reading the whole object 
	 * @return The map with attribute values
	 * 
	 * 
	 */
	public abstract AttributeValuesMap readObjectInfoValuesFromOID(
			ClassInfo classInfo, OID oid, boolean useCache,
			IOdbList<String> attributeNames, IOdbList<String> relationAttributeNames,
			int recursionLevel, String[] orderByFields, boolean useOidForObject);

	public abstract Object readAtomicNativeObjectInfoAsObject(long position,
			int odbTypeId);

	public abstract AtomicNativeObjectInfo readAtomicNativeObjectInfo(
			long position, int odbTypeId) ;

	public abstract long readOidPosition(OID oid) ;

	public abstract Object getObjectFromOid(OID oid, boolean returnInstance, boolean useCache);

	public abstract List<FullIDInfo> getAllIdInfos(String objectTypeToDisplay, byte idType,
			boolean displayObject) ;

	/**
	 * Returns the id of an object by reading the object header
	 * 
	 * @param position
	 * @param includeDeleted
	 * @return The oid of the object at the specific position
	 */
	public abstract OID getIdOfObjectAt(long position, boolean includeDeleted);

	public abstract void close();

	public abstract Object buildOneInstance(NonNativeObjectInfo objectInfo);

	/**
	 * Get a list of object matching the query
	 * 
	 * @param query
	 * @param inMemory
	 * @param startIndex
	 * @param endIndex
	 * @return The list of objects
	 * 
	 */
	public abstract <T>Objects<T> getObjects(IQuery query, boolean inMemory,
			int startIndex, int endIndex) ;

	/**
	 * Get a list of values matching the query
	 * 
	 * @param query
	 * @param startIndex
	 * @param endIndex
	 * @return The list of values
	 * 
	 */
	public abstract Values getValues(IValuesQuery query, int startIndex,
			int endIndex) ;

	/**
	 * Return Objects. Match the query without instantiating objects. Only
	 * instantiate object for object that match the query
	 * 
	 * @param query
	 *            The query to select objects
	 * @param inMemory
	 *            To indicate if object must be all loaded in memory
	 * @param startIndex
	 *            First object index
	 * @param endIndex
	 *            Last object index
	 * @param returnObjects
	 *            To indicate if object instances must be created
	 * @return The list of objects
	 * 
	 * 
	 */
	public abstract <T>Objects<T> getObjectInfos(IQuery query, boolean inMemory,
			int startIndex, int endIndex, boolean returnObjects,
			IMatchingObjectAction queryResultAction);

	public abstract String getBaseIdentification();

	public abstract IInstanceBuilder getInstanceBuilder();

	/**
	 * Reads the pointers(ids or positions) of an object that has the specific oid
	 * 
	 * @param oid
	 *            The oid of the object we want to read the pointers
	 * @return The ObjectInfoHeader
	 */
	public ObjectInfoHeader readObjectInfoHeaderFromOid(OID oid, boolean useCache) ;

	/** Returns information about all OIDs of the database 
	 * 
	 * @param idType
	 * @return
	 */
	public List<Long> getAllIds(byte idType) ;

	/**
	 * Gets the next object oid of the object with the specific oid
	 * 
	 * @param oid
	 * @return The oid of the next object. If there is no next object,
	 *         return null
	 */
	public OID getNextObjectOID(OID oid) ;

	/**
	 * Gets the real object position from its OID
	 * @param oid
	 *            The oid of the object to get the position
	 *            To indicate if an exception must be thrown if object is not
	 *            found
	 * @return The object position, if object has been marked as deleted then
	 *         return StorageEngineConstant.DELETED_OBJECT_POSITION
	 */
	public long getObjectPositionFromItsOid(OID oid, boolean useCache, boolean throwException) ;

	/**Reads a non non native Object Info (Layer2) from its position
	 * 
	 * @param classInfo
	 * @param oid can be null
	 * @param position
	 * @param useCache
	 * @param returnInstance
	 * @return The meta representation of the object
	 * 
	 */
	public NonNativeObjectInfo readNonNativeObjectInfoFromPosition(ClassInfo classInfo, OID oid, long position, boolean useCache, boolean returnInstance);

}