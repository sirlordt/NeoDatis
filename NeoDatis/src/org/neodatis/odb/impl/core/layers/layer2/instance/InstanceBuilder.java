/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.impl.core.layers.layer2.instance;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.IError;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * Class used to build instance from Meta Object representation. Layer 2 to
 * Layer 1 conversion.
 * 
 * @sharpen.ignore
 * @author osmadja
 * 
 */
public abstract class InstanceBuilder implements IInstanceBuilder {
	private static final String LOG_ID = "InstanceBuilder";

	private static final String LOG_ID_DEBUG = "InstanceBuilder.debug";

	private ITriggerManager triggerManager;
	protected IStorageEngine engine;
	private IClassIntrospector classIntrospector;
	private IClassPool classPool;
	/**
	 * To resolve cyclic reference while building an instance. Instead of using
	 * session cache for that, we use local cache(wich is smaller to speed up)
	 * instance building
	 * 
	 */
	private Map<OID, Object> localCache;

	public InstanceBuilder(IStorageEngine engine) {
		this.triggerManager = OdbConfiguration.getCoreProvider().getLocalTriggerManager(engine);
		this.classIntrospector = OdbConfiguration.getCoreProvider().getClassIntrospector();
		this.classPool = OdbConfiguration.getCoreProvider().getClassPool();
		this.engine = engine;
		this.localCache = new OdbHashMap<OID, Object>(50);
	}

	/** Local and server InstanceBuilder must define their own getSession() */
	protected abstract ISession getSession();

	/**
	 * The entry point to build an instance from an object meta representation
	 * 
	 * @param objectInfo
	 * @return
	 */
	public Object buildOneInstance(NonNativeObjectInfo objectInfo) {
		try {
			return internalBuildOneInstance(objectInfo);
		} finally {
			// Clears the local cache after every creation
			localCache.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder#
	 * buildOneInstance
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.AbstractObjectInfo)
	 */
	protected Object internalBuildOneInstance(AbstractObjectInfo objectInfo) {

		Object object = null;
		if (objectInfo instanceof NonNativeNullObjectInfo) {
			return null;
		}
		if (objectInfo.getClass() == NonNativeObjectInfo.class) {
			object = internalBuildOneInstance((NonNativeObjectInfo) objectInfo);
		} else {
			// instantiation cache is not used for native objects
			object = internalBuildOneInstance((NativeObjectInfo) objectInfo, null);
		}

		return object;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder#
	 * buildCollectionInstance
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.CollectionObjectInfo)
	 */
	protected Object internalBuildCollectionInstance(CollectionObjectInfo coi) {
		Collection<Object> newCollection = null;
		try {
			newCollection = (Collection<Object>) classPool.getClass(coi.getRealCollectionClassName()).newInstance();
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.COLLECTION_INSTANCIATION_ERROR.addParameter(coi.getRealCollectionClassName()),e);
		}
		Iterator iterator = coi.getCollection().iterator();
		AbstractObjectInfo aoi = null;
		while (iterator.hasNext()) {
			aoi = (AbstractObjectInfo) iterator.next();
			if (!aoi.isDeletedObject()) {
				newCollection.add(internalBuildOneInstance(aoi));
			}
		}
		return newCollection;
	}

	/**
	 * Builds an insatnce of an enum
	 * 
	 * @param enumClass
	 */
	protected Object internalBuildEnumInstance(EnumNativeObjectInfo enoi) {
		Class clazz = classPool.getClass(enoi.getEnumClassInfo().getFullClassName());
		Object theEnum = Enum.valueOf(clazz, enoi.getEnumName());
		return theEnum;
	}

	/**
	 * Builds an instance of an array
	 */
	protected Object internalBuildArrayInstance(ArrayObjectInfo aoi) {
		// first check if array element type is native (int,short, for example)
		ODBType type = ODBType.getFromName(aoi.getRealArrayComponentClassName());

		Class arrayClazz = type.getNativeClass();
		Object array = Array.newInstance(arrayClazz, aoi.getArray().length);
		Object object = null;
		AbstractObjectInfo aboi = null;
		for (int i = 0; i < aoi.getArrayLength(); i++) {

			aboi = (AbstractObjectInfo) aoi.getArray()[i];
			if (aboi != null && !aboi.isDeletedObject() && !aboi.isNull()) {
				object = internalBuildOneInstance(aboi);
				Array.set(array, i, object);
			}
		}
		return array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder#
	 * buildMapInstance
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.MapObjectInfo)
	 */
	protected Map internalBuildMapInstance(MapObjectInfo mapObjectInfo) {
		Map<AbstractObjectInfo,AbstractObjectInfo> map = mapObjectInfo.getMap();
		Map newMap;
		try {
			newMap = (Map) classPool.getClass(mapObjectInfo.getRealMapClassName()).newInstance();
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.MAP_INSTANCIATION_ERROR.addParameter(map.getClass().getName()));
		}

		Iterator<AbstractObjectInfo> iterator = map.keySet().iterator();
		AbstractObjectInfo key = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			Object realKey = internalBuildOneInstance(key);
			Object realValue = internalBuildOneInstance(map.get(key));
			newMap.put(realKey, realValue);
		}
		return newMap;
	}

	/**
	 * Main entry point to build an instance from an object meta representation
	 * 
	 */
	protected Object internalBuildOneInstance(NonNativeObjectInfo objectInfo) {

		// Gets the session cache
		ICache cache = getSession().getCache();

		// verify if the object is marked as deleted
		if (objectInfo.isDeletedObject()) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_IS_MARKED_AS_DELETED_FOR_OID.addParameter(objectInfo.getOid()));
		}
		// Then check if object is in session cache
		Object o = cache.getObjectWithOid(objectInfo.getOid());
		if (o != null) {
			return o;
		}

		// Then check if object is in local cache. this is to avoid cyclic
		// reference problem
		o = localCache.get(objectInfo.getOid());
		if (o != null) {
			return o;
		}

		Class instanceClazz = null;
		String className = objectInfo.getClassInfo().getFullClassName();
		instanceClazz = classPool.getClass(className);
		
		o = classIntrospector.newFullInstanceOf(instanceClazz, objectInfo);
		
		if(o!=null){
			//neodatisee
			// the object was created by an instanciation helper, we still need to add it to the cache
			cache.addObject(objectInfo.getOid(), o, objectInfo.getHeader());

			if (triggerManager != null) {
				triggerManager.manageSelectTriggerAfter(objectInfo.getClassInfo().getFullClassName(), objectInfo, objectInfo.getOid());
			}

			if (OdbConfiguration.reconnectObjectsToSession()) {

				ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(engine.getBaseIdentification().getIdentification());
				crossSessionCache.addObject(o, objectInfo.getOid());

			}
			return o;
		}

		try {
			o = classIntrospector.newInstanceOf(instanceClazz);
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.INSTANCIATION_ERROR.addParameter(className), e);
		}

		// This can happen if ODB can not create the instance
		// TODO Check if returning null is correct
		if (o == null) {
			return null;
		}

		// Adds this incomplete instance in the cache to manage cyclic reference
		localCache.put(objectInfo.getOid(), o);

		ClassInfo ci = objectInfo.getClassInfo();
		List fields = classIntrospector.getAllFields(className);

		Field field = null;
		AbstractObjectInfo aoi = null;
		Object value = null;
		for (int i = 0; i < fields.size(); i++) {
			field = (Field) fields.get(i);
			// Gets the id of this field
			int attributeId = ci.getAttributeId(field.getName());
			// If attributeId==-1, the attribute does not exist in the metamodel
			if (attributeId == -1) {
				throw new ODBRuntimeException(NeoDatisError.CLASS_INFO_DO_NOT_HAVE_THE_ATTRIBUTE.addParameter(ci.getFullClassName())
						.addParameter(field.getName()));
			}
			aoi = objectInfo.getAttributeValueFromId(attributeId);

			if (aoi != null && (!aoi.isNull())) {

				if (aoi.isNative()) {
					value = internalBuildOneInstance((NativeObjectInfo) aoi, field.getType());
				} else if (aoi.isNonNativeObject()) {
					if (aoi.isDeletedObject()) {
						if (OdbConfiguration.displayWarnings()) {
							IError warning = NeoDatisError.ATTRIBUTE_REFERENCES_A_DELETED_OBJECT.addParameter(className).addParameter(
									objectInfo.getOid()).addParameter(field.getName());
							DLogger.info(warning.toString());
						}
						value = null;
					} else {
						value = internalBuildOneInstance((NonNativeObjectInfo) aoi);
					}
				}

				if (value != null) {

					try {
						field.set(o, value);
					} catch (Exception e) {
						throw new ODBRuntimeException(NeoDatisError.INSTANCE_BUILDER_WRONG_OBJECT_CONTAINER_TYPE.addParameter(
								objectInfo.getClassInfo().getFullClassName()).addParameter(value.getClass().getName()).addParameter(
								field.getType().getName()), e);
					}
				}
			}
		}
		if (o != null && !o.getClass().getName().equals(objectInfo.getClassInfo().getFullClassName())) {
			new ODBRuntimeException(NeoDatisError.INSTANCE_BUILDER_WRONG_OBJECT_TYPE.addParameter(
					objectInfo.getClassInfo().getFullClassName()).addParameter(o.getClass().getName()));
		}

		cache.addObject(objectInfo.getOid(), o, objectInfo.getHeader());

		if (triggerManager != null) {
			triggerManager.manageSelectTriggerAfter(objectInfo.getClassInfo().getFullClassName(), objectInfo, objectInfo.getOid());
		}

		if (OdbConfiguration.reconnectObjectsToSession()) {

			ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(engine.getBaseIdentification().getIdentification());
			crossSessionCache.addObject(o, objectInfo.getOid());

		}
		// sets the objects in the meta-representation
		//objectInfo.setObject(o);
		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder#
	 * buildOneInstance
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.NativeObjectInfo)
	 */
	protected Object internalBuildOneInstance(NativeObjectInfo objectInfo, Class fieldType) {
		if (objectInfo.isNull()) {
			return null;
		}
		if (objectInfo.isAtomicNativeObject()) {
			return objectInfo.getObject();
		}

		if (objectInfo.isCollectionObject()) {
			Object value = internalBuildCollectionInstance((CollectionObjectInfo) objectInfo);

			if (fieldType == null) {
				fieldType = objectInfo.getObject().getClass();
			}
			
			return value;

		}
		if (objectInfo.isArrayObject()) {
			return internalBuildArrayInstance((ArrayObjectInfo) objectInfo);
		}
		if (objectInfo.isMapObject()) {
			return internalBuildMapInstance((MapObjectInfo) objectInfo);
		}
		if (objectInfo.isEnumObject()) {
			EnumNativeObjectInfo enoi = (EnumNativeObjectInfo) objectInfo;
			return internalBuildEnumInstance((EnumNativeObjectInfo) objectInfo);
		}

		throw new ODBRuntimeException(NeoDatisError.INSTANCE_BUILDER_NATIVE_TYPE.addParameter(ODBType.getNameFromId(objectInfo
				.getOdbTypeId())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder#
	 * buildOneInstance
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.AtomicNativeObjectInfo)
	 */
	protected Object internalBuildOneInstance(AtomicNativeObjectInfo objectInfo) {

		int odbTypeId = objectInfo.getOdbTypeId();
		Long l = null;

		switch (odbTypeId) {
		case ODBType.NULL_ID:
			return null;
		case ODBType.STRING_ID:
			return objectInfo.getObject();
		case ODBType.DATE_ID:
		case ODBType.DATE_SQL_ID:
		case ODBType.DATE_TIMESTAMP_ID:
			return objectInfo.getObject();
		case ODBType.LONG_ID:
		case ODBType.NATIVE_LONG_ID:
			if (objectInfo.getObject().getClass() == Long.class) {
				return objectInfo.getObject();
			}
			return new Long(objectInfo.getObject().toString());

		case ODBType.INTEGER_ID:
		case ODBType.NATIVE_INT_ID:
			if (objectInfo.getObject().getClass() == Integer.class) {
				return objectInfo.getObject();
			}
			return new Integer(objectInfo.getObject().toString());
		case ODBType.BOOLEAN_ID:
		case ODBType.NATIVE_BOOLEAN_ID:
			if (objectInfo.getObject().getClass() == Boolean.class) {
				return objectInfo.getObject();
			}
			return new Boolean(objectInfo.getObject().toString());
		case ODBType.BYTE_ID:
		case ODBType.NATIVE_BYTE_ID:
			if (objectInfo.getObject().getClass() == Byte.class) {
				return objectInfo.getObject();
			}
			return new Byte(objectInfo.getObject().toString());

		case ODBType.SHORT_ID:
		case ODBType.NATIVE_SHORT_ID:
			if (objectInfo.getObject().getClass() == Short.class) {
				return objectInfo.getObject();
			}
			return new Short(objectInfo.getObject().toString());

		case ODBType.FLOAT_ID:
		case ODBType.NATIVE_FLOAT_ID:
			if (objectInfo.getObject().getClass() == Float.class) {
				return objectInfo.getObject();
			}
			return new Float(objectInfo.getObject().toString());

		case ODBType.DOUBLE_ID:
		case ODBType.NATIVE_DOUBLE_ID:
			if (objectInfo.getObject().getClass() == Double.class) {
				return objectInfo.getObject();
			}
			return new Double(objectInfo.getObject().toString());
		case ODBType.BIG_DECIMAL_ID:
			return new BigDecimal(objectInfo.getObject().toString());
		case ODBType.BIG_INTEGER_ID:
			return new BigInteger(objectInfo.getObject().toString());
		case ODBType.CHARACTER_ID:
		case ODBType.NATIVE_CHAR_ID:
			if (objectInfo.getObject().getClass() == Character.class) {
				return objectInfo.getObject();
			}
			return new Character(objectInfo.getObject().toString().charAt(0));
		case ODBType.OBJECT_OID_ID:
			if (objectInfo.getObject().getClass() == Long.class) {
				l = (Long) objectInfo.getObject();
			} else {
				OID oid = (OID) objectInfo.getObject();
				l = new Long(oid.getObjectId());
			}

			return OIDFactory.buildObjectOID(l.longValue());
		case ODBType.CLASS_OID_ID:
			if (objectInfo.getObject().getClass() == Long.class) {
				l = (Long) objectInfo.getObject();
			} else {
				l = new Long(objectInfo.getObject().toString());
			}

			return OIDFactory.buildClassOID(l.longValue());

		default:
			throw new ODBRuntimeException(NeoDatisError.INSTANCE_BUILDER_NATIVE_TYPE_IN_COLLECTION_NOT_SUPPORTED.addParameter(ODBType
					.getNameFromId(odbTypeId)));
		}

	}

	public String getSessionId() {
		return engine.getSession(true).getId();
	}

	public boolean isLocal() {
		return engine.isLocal();
	}

}
