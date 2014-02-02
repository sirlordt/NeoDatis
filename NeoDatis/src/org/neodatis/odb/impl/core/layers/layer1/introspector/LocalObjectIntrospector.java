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
package org.neodatis.odb.impl.core.layers.layer1.introspector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer1.introspector.IIntrospectionCallback;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer2.meta.ObjectReference;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.tool.wrappers.OdbReflection;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * The local implementation of the Object Instrospector.
 * 
 * @author osmadja
 * 
 */
public class LocalObjectIntrospector implements IObjectIntrospector {
	protected IStorageEngine storageEngine;
	protected IClassIntrospector classIntrospector;
	private IClassPool classPool;

	// private MetaModel localMetaModel;

	public LocalObjectIntrospector(IStorageEngine storageEngine) {
		this.storageEngine = storageEngine;
		this.classIntrospector = OdbConfiguration.getCoreProvider().getClassIntrospector();
		this.classPool = OdbConfiguration.getCoreProvider().getClassPool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.introspector.IObjectIntrospector#getMetaRepresentation
	 * (java.lang.Object, org.neodatis.odb.core.meta.ClassInfo, boolean,
	 * java.util.Map)
	 */
	public AbstractObjectInfo getMetaRepresentation(Object object, ClassInfo ci, boolean recursive, Map<Object,NonNativeObjectInfo> alreadyReadObjects,
			IIntrospectionCallback callback) {

		return getObjectInfo(object, ci, recursive, alreadyReadObjects, callback);
	}

	/**
	 * retrieve object data
	 * 
	 * @param object
	 * @param ci
	 * @param recursive
	 * @return The object info
	 */
	protected AbstractObjectInfo getObjectInfo(Object object, ClassInfo ci, boolean recursive, Map<Object,NonNativeObjectInfo> alreadyReadObjects,
			IIntrospectionCallback callback) {
		return getObjectInfoInternal(null, object, ci, recursive, alreadyReadObjects, callback);
	}

	protected AbstractObjectInfo getNativeObjectInfoInternal(ODBType type, Object object, boolean recursive, Map<Object,NonNativeObjectInfo> alreadyReadObjects,
			IIntrospectionCallback callback) {

		AbstractObjectInfo aoi = null;

		if (type.isAtomicNative()) {
			if (object == null) {
				aoi = new NullNativeObjectInfo(type.getId());
			} else {
				aoi = new AtomicNativeObjectInfo(object, type.getId());
			}
		} else if (type.isCollection()) {
			aoi = introspectCollection((Collection) object, recursive, alreadyReadObjects, type, callback);
		} else if (type.isArray()) {
			if (object == null) {
				aoi = new ArrayObjectInfo(null);
			} else {
				// Gets the type of the elements of the array
				String realArrayClassName = object.getClass().getComponentType().getName();
				ArrayObjectInfo aroi = null;

				if (recursive) {
					aroi = introspectArray(object, recursive, alreadyReadObjects, type, callback);
				} else {
					aroi = new ArrayObjectInfo((Object[]) object);
				}
				aroi.setRealArrayComponentClassName(realArrayClassName);
				aoi = aroi;
			}
		} else if (type.isMap()) {
			if (object == null) {
				aoi = new MapObjectInfo(null, type, type.getDefaultInstanciationClass().getName());
			} else {
				MapObjectInfo moi = null;
				String realMapClassName = object.getClass().getName();
				moi = new MapObjectInfo(introspectMap((Map) object, recursive, alreadyReadObjects, callback), type , realMapClassName);

				if (realMapClassName.indexOf("$") != -1) {
					moi.setRealMapClassName(type.getDefaultInstanciationClass().getName());
				} 
				aoi = moi;
			}
		} else if (type.isEnum()) {
			Enum enumObject = (Enum) object;

			if(enumObject==null){
				aoi = new NullNativeObjectInfo(type.getSize());
			}else{
				String enumClassName = enumObject == null ? null : enumObject.getClass().getName();
				
				// Here we must check if the enum is already in the meta model. Enum must be stored in the meta
				// model to optimize its storing as we need to keep track of the enum class
				// for each enum stored. So instead of storing the enum class name, we can store enum class id, a long
				// instead of the full enum class name string
				
				ClassInfo ci = getClassInfo(enumClassName);
				// while introspecting, we get the enumName (enum.name()) and not the toString() representation
				// So what will be stored is the name of the enum and not the value (toString)
				// Check EqualCrtiterion too
				String enumValue = enumObject == null ? null : enumObject.name();
				aoi = new EnumNativeObjectInfo(ci, enumValue);
			}
		}
		return aoi;
	}

	/**
	 * Build a meta representation of an object
	 * 
	 * <pre>
	 * warning: When an object has two fields with the same name (a private field with the same name in a parent class, the deeper field (of the parent) is ignored!)
	 * </pre>
	 * 
	 * @param object
	 * @param ci
	 * @param recursive
	 * @return The ObjectInfo
	 */
	protected AbstractObjectInfo getObjectInfoInternal(AbstractObjectInfo nnoi, Object object, ClassInfo ci, boolean recursive,
			Map<Object,NonNativeObjectInfo> alreadyReadObjects, IIntrospectionCallback callback) {

		Object value = null;

		if (object == null) {
			return NullNativeObjectInfo.getInstance();
		}
		Class clazz = object.getClass();
		ODBType type = ODBType.getFromClass(clazz);
		
		String className =  clazz.getName();


		if (type.isNative()) {
			return getNativeObjectInfoInternal(type, object, recursive, alreadyReadObjects, callback);
		}

		// sometimes the clazz.getName() may not match the ci.getClassName()
		// It happens when the attribute is an interface or superclass of the
		// real attribute class
		// In this case, ci must be updated to the real class info
		if (ci != null && !clazz.getName().equals(ci.getFullClassName())) {
			ci = getClassInfo(className);
			nnoi = null;
		}

		NonNativeObjectInfo mainAoi = (NonNativeObjectInfo) nnoi;

		boolean isRootObject = false;
		if (alreadyReadObjects == null) {
			alreadyReadObjects = new OdbHashMap<Object, NonNativeObjectInfo>();
			isRootObject = true;
		}

		if (object != null) {
			NonNativeObjectInfo cachedNnoi = alreadyReadObjects.get(object);
			if (cachedNnoi != null) {
				ObjectReference or = new ObjectReference(cachedNnoi);
				return or;
			}
			if(callback!=null){
				callback.objectFound(object);
			}
		}

		if (mainAoi == null) {
			mainAoi = buildNnoi(object, ci, null, null, null, alreadyReadObjects);
		}
		alreadyReadObjects.put(object, mainAoi);

		IOdbList<Field> fields = classIntrospector.getAllFields(className);
		AbstractObjectInfo aoi = null;
		int attributeId = -1;
		// For all fields
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);

			try {
				value = field.get(object);
				attributeId = ci.getAttributeId(field.getName());
				// neodatisee
				// This is ugly but it seems to be a bug/problem with java reflecting Class instances - see junit TestClassRefelection
				// It seems to aleatory, but executing a toString seems to force the name (transient attribute of Class) to be populated 
				if(field.getType()==Class.class && value !=null){
					value.toString();
				}
				if (attributeId == -1) {
					throw new ODBRuntimeException(NeoDatisError.OBJECT_INTROSPECTOR_NO_FIELD_WITH_NAME.addParameter(ci.getFullClassName())
							.addParameter(field.getName()));

				}
				ODBType valueType = null;
				if (value == null) {
					// If value is null, take the type from the field type
					// declared in the class
					valueType = ODBType.getFromClass(field.getType());
				} else {
					// Else take the real attribute type!
					valueType = ODBType.getFromClass(value.getClass());
				}

				// for native fields
				if (valueType.isNative()) {
					aoi = getNativeObjectInfoInternal(valueType, value, recursive, alreadyReadObjects, callback);
					mainAoi.setAttributeValue(attributeId, aoi);
				} else {
					//callback.objectFound(value);
					// Non Native Objects
					ClassInfo clai = getClassInfo(valueType.getName());

					if (value == null) {
						aoi = new NonNativeNullObjectInfo(clai);
						mainAoi.setAttributeValue(attributeId, aoi);
					} else {
						if (recursive) {
							aoi = getObjectInfoInternal(null, value, clai, recursive, alreadyReadObjects, callback);
							mainAoi.setAttributeValue(attributeId, aoi);
						} else {
							// When it is not recursive, simply add the object
							// values.add(value);
							throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR
									.addParameter("Should not enter here - ObjectIntrospector - 'simply add the object'"));
						}
					}
				}
			} catch (IllegalArgumentException e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in getObjectInfoInternal"), e);
			} catch (IllegalAccessException e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("getObjectInfoInternal"), e);
			}
		}
		if (isRootObject) {
			alreadyReadObjects.clear();
			alreadyReadObjects = null;
		}
		return mainAoi;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.introspector.IObjectIntrospector#buildNnoi(java
	 * .lang.Object, org.neodatis.odb.core.meta.ClassInfo,
	 * org.neodatis.odb.core.meta.AbstractObjectInfo[], long[], int[],
	 * java.util.Map)
	 */
	public NonNativeObjectInfo buildNnoi(Object object, ClassInfo classInfo, AbstractObjectInfo[] values, long[] attributesIdentification,
			int[] attributeIds, Map<Object,NonNativeObjectInfo> alreadyReadObjects) {
		NonNativeObjectInfo nnoi = new NonNativeObjectInfo(object, classInfo, values, attributesIdentification, attributeIds);

		if (storageEngine != null) {// for unit test purpose
			ISession session = storageEngine.getSession(true); 
			ICache cache = session.getCache();
			// Check if object is in the cache, if so sets its oid
			OID oid = cache.getOid(object, false);
			if (oid != null) {
				nnoi.setOid(oid);
				// Sets some values to the new header to keep track of the infos
				// when
				// executing NeoDatis without closing it, just committing.
				// Bug reported by Andy
				ObjectInfoHeader oih = cache.getObjectInfoHeaderFromOid(oid, false);
				
				// If session has been committed, the OIH cache has been reseted, reload the OIH from disk 
				if(oih==null && session.hasBeenCommitted()){
					oih = storageEngine.getObjectReader().readObjectInfoHeaderFromOid(oid, false);
				}
				
				if(oih==null){
					throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE.addParameter(oid));
				}
				nnoi.getHeader().setObjectVersion(oih.getObjectVersion());
				nnoi.getHeader().setUpdateDate(oih.getUpdateDate());
				nnoi.getHeader().setCreationDate(oih.getCreationDate());
			}
		}

		return nnoi;
	}

	private CollectionObjectInfo introspectCollection(Collection collection, boolean introspect, Map<Object,NonNativeObjectInfo> alreadyReadObjects, ODBType type,
			IIntrospectionCallback callback) {

		if (collection == null) {
			return new CollectionObjectInfo();
		}

		// A collection that contain all meta representations of the collection
		// objects
		Collection<AbstractObjectInfo> collectionCopy = new ArrayList<AbstractObjectInfo>(collection.size());
		// A collection to keep references all all non native objects of the
		// collection
		// This will be used later to get all non native objects contained in an
		// object
		Collection<NonNativeObjectInfo> nonNativesObjects = new ArrayList<NonNativeObjectInfo>(collection.size());

		AbstractObjectInfo aoi = null;
		Iterator iterator = collection.iterator();

		while (iterator.hasNext()) {
			Object o = iterator.next();
			ClassInfo ci = null;
			// Null objects are not inserted in list
			if (o != null) {
				ci = getClassInfo(o.getClass().getName());
				aoi = getObjectInfo(o, ci, introspect, alreadyReadObjects, callback);
				collectionCopy.add(aoi);
				if (aoi.isNonNativeObject()) {
					// o is not null, call the callback with it
					//callback.objectFound(o);
					// This is a non native object
					nonNativesObjects.add((NonNativeObjectInfo) aoi);
				}
			}
		}

		CollectionObjectInfo coi = new CollectionObjectInfo(collectionCopy, nonNativesObjects);
		String realCollectionClassName = collection.getClass().getName();
		if (realCollectionClassName.indexOf("$") != -1) {
			coi.setRealCollectionClassName(type.getDefaultInstanciationClass().getName());
		} else {
			coi.setRealCollectionClassName(realCollectionClassName);
		}
		return coi;
	}

	private Map<AbstractObjectInfo,AbstractObjectInfo> introspectMap(Map map, boolean introspect, Map<Object,NonNativeObjectInfo> alreadyReadObjects, IIntrospectionCallback callback) {

		Map<AbstractObjectInfo,AbstractObjectInfo> mapCopy = new OdbHashMap<AbstractObjectInfo, AbstractObjectInfo>();
		Collection keySet = map.keySet();
		Iterator keys = keySet.iterator();
		ClassInfo ciKey = null;
		ClassInfo ciValue = null;
		AbstractObjectInfo aoiForKey = null;
		AbstractObjectInfo aoiForValue = null;
		while (keys.hasNext()) {
			Object key = keys.next();
			Object value = map.get(key);

			if (key != null) {
				ciKey = getClassInfo(key.getClass().getName());
				if (value != null) {
					ciValue = getClassInfo(value.getClass().getName());
				}
				aoiForKey = getObjectInfo(key, ciKey, introspect, alreadyReadObjects, callback);
				aoiForValue = getObjectInfo(value, ciValue, introspect, alreadyReadObjects, callback);

				mapCopy.put(aoiForKey, aoiForValue);

			}
		}

		return mapCopy;
	}

	private ClassInfo getClassInfo(String fullClassName) {

		if (ODBType.getFromName(fullClassName).isNative()) {
			return null;
		}
		ISession session = storageEngine.getSession(true);
		MetaModel metaModel = session.getMetaModel();

		if (metaModel.existClass(fullClassName)) {
			return metaModel.getClassInfo(fullClassName, true);
		}
		ClassInfo ci = null;
		ClassInfoList ciList = null;
		ciList = classIntrospector.introspect(fullClassName, true);
		// to enable junit tests
		if (storageEngine != null) {
			storageEngine.addClasses(ciList);
			// For client Server : reset meta model
			if (!storageEngine.isLocal()) {
				metaModel = session.getMetaModel();
			}
		} else {
			metaModel.addClasses(ciList);
		}
		ci = metaModel.getClassInfo(fullClassName, true);
		return ci;
	}

	/**
	 * Used when byte code instrumentation is to check if an object has changed
	 * 
	 * @param object
	 * @return
	 */
	public boolean objectHasChanged(Object object) {
		Class clazz = classPool.getClass(object.getClass().getName());
		Field field;
		try {
			field = classIntrospector.getField(clazz, "hasChanged");
			Object value = field.get(object);
			return ((Boolean) value).booleanValue();
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in objectHasChanged(Object object)"), e);
		}
	}

	private ArrayObjectInfo introspectArray(Object array, boolean introspect, Map<Object,NonNativeObjectInfo> alreadyReadObjects, ODBType valueType,
			IIntrospectionCallback callback) {

		int length = OdbReflection.getArrayLength(array);
		Class elementType = array.getClass().getComponentType();
		ODBType type = ODBType.getFromClass(elementType);
		if (type.isAtomicNative()) {
			return intropectAtomicNativeArray(array, type);
		}
		if (!introspect) {
			return new ArrayObjectInfo((Object[]) array);
		}
		Object[] arrayCopy = new Object[length];

		for (int i = 0; i < length; i++) {
			Object o = OdbReflection.getArrayElement(array, i);
			ClassInfo ci = null;
			if (o != null) {
				ci = getClassInfo(o.getClass().getName());
				AbstractObjectInfo aoi = getObjectInfo(o, ci, introspect, alreadyReadObjects, callback);
				arrayCopy[i] = aoi;
			} else {
				arrayCopy[i] = new NonNativeNullObjectInfo();
			}
		}
		ArrayObjectInfo arrayOfAoi = new ArrayObjectInfo(arrayCopy, valueType, type.getId());
		return arrayOfAoi;
	}

	private ArrayObjectInfo intropectAtomicNativeArray(Object array, ODBType type) {

		int length = OdbReflection.getArrayLength(array);
		AtomicNativeObjectInfo anoi = null;
		Object[] arrayCopy = new Object[length];
		int typeId = 0;
		for (int i = 0; i < length; i++) {
			Object o = OdbReflection.getArrayElement(array, i);
			if (o != null) {
				// If object is not null, try to get the exact type
				typeId = ODBType.getFromClass(o.getClass()).getId();
				anoi = new AtomicNativeObjectInfo(o, typeId);
				arrayCopy[i] = anoi;
			} else {
				// Else take the declared type
				arrayCopy[i] = new NullNativeObjectInfo(type.getId());
			}
		}
		ArrayObjectInfo aoi = new ArrayObjectInfo(arrayCopy, ODBType.ARRAY, type.getId());
		return aoi;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.introspector.IObjectIntrospector#clear()
	 */
	public void clear() {
		storageEngine = null;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector#getClassIntrospector()
	 */
	public IClassIntrospector getClassIntrospector() {
		return classIntrospector;
	}
}
