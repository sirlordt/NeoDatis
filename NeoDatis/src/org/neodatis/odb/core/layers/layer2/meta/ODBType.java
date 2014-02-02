
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
package org.neodatis.odb.core.layers.layer2.meta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.impl.core.oid.OdbClassOID;
import org.neodatis.odb.impl.core.oid.OdbObjectOID;
import org.neodatis.tool.wrappers.OdbClassUtil;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * Contains the list for the ODB types
 * 
 * @author olivier s
 * 
 */
public final class ODBType implements Serializable{
	public static int nb;
	/**
	 * 
	 */
	private static final long serialVersionUID = 341217747918380780L;
	private boolean isPrimitive; 
	private final int id;

	private String name;

	private int size;

	private transient Class superClass;
	/** Used to instantiate the class when complex subclass is referenced. example, when a Collection$SynchronizedMap is referenced
	 * ODB, will use HashMap instead
	 */
	private Class defaultInstanciationClass;
    private long position;
    private transient static IClassPool classPool = null;

	/** For array element type*/
	private ODBType subType;

    public final static int NULL_ID = 0;
    public final static int NATIVE_BOOLEAN_ID = 10;

	/** 1 byte */
	public final static int NATIVE_BYTE_ID = 20;
	// Not used in Java, for .Net compatibility
	public final static int NATIVE_SIGNED_BYTE_ID = 21;

	public final static int NATIVE_CHAR_ID = 30;

	/** 2 byte */
	public final static int NATIVE_SHORT_ID = 40;

	/** 4 byte */
	public final static int NATIVE_INT_ID = 50;

	/** 8 bytes */
	public final static int NATIVE_LONG_ID = 60;

	/** 4 byte */
	public final static int NATIVE_FLOAT_ID = 70;

	/** 8 byte */
	public final static int NATIVE_DOUBLE_ID = 80;

	public final static int BYTE_ID = 90;
	// Not used in Java, for .Net compatibility
	public final static int SIGNED_BYTE_ID = 91;
	public final static int SHORT_ID = 100;
	public final static int INTEGER_ID = 110;
	public final static int LONG_ID = 120;
	public final static int FLOAT_ID = 130;
	public final static int DOUBLE_ID = 140;
	public final static int CHARACTER_ID = 150;
	public final static int BOOLEAN_ID = 160;
	public final static int DATE_ID = 170;
	public final static int DATE_SQL_ID = 171;
	public final static int DATE_TIMESTAMP_ID = 172;
	public final static int DATE_CALENDAR_ID = 173;
	public final static int DATE_GREGORIAN_CALENDAR_ID = 174;
	public final static int OID_ID = 180;
	public final static int OBJECT_OID_ID = 181;
	public final static int CLASS_OID_ID = 182;
	
	public final static int BIG_INTEGER_ID = 190;
	public final static int BIG_DECIMAL_ID = 200;
	public final static int STRING_ID = 210;
	/** Enums are internally stored as String: the enum name*/
	public final static int ENUM_ID = 211;
	
	
	public final static int NATIVE_FIX_SIZE_MAX_ID = CLASS_OID_ID;
	public final static int NATIVE_MAX_ID = STRING_ID;

	public final static int COLLECTION_ID = 250;

	public final static int ARRAY_ID = 260;
    
    public final static int MAP_ID = 270;
    

	public final static int NON_NATIVE_ID = 300;

    public final static ODBType NULL = new ODBType(true,NULL_ID, "null", 1);
    /** 1 byte */
	public final static ODBType NATIVE_BOOLEAN = new ODBType(true,NATIVE_BOOLEAN_ID, Boolean.TYPE.getName(), 1);

	/** 1 byte */
	public final static ODBType NATIVE_BYTE = new ODBType(true,NATIVE_BYTE_ID, Byte.TYPE.getName(), 1);

	/** 2 byte */
	public final static ODBType NATIVE_CHAR = new ODBType(true,NATIVE_CHAR_ID, Character.TYPE.getName(), 2);

	/** 2 byte */
	public final static ODBType NATIVE_SHORT = new ODBType(true,NATIVE_SHORT_ID, Short.TYPE.getName(), 2);

	/** 4 byte */
	public final static ODBType NATIVE_INT = new ODBType(true,NATIVE_INT_ID, Integer.TYPE.getName(), 4);

	/** 8 bytes */
	public final static ODBType NATIVE_LONG = new ODBType(true,NATIVE_LONG_ID, Long.TYPE.getName(), 8);

	/** 4 byte */
	public final static ODBType NATIVE_FLOAT = new ODBType(true,NATIVE_FLOAT_ID, Float.TYPE.getName(), 4);

	/** 8 byte */
	public final static ODBType NATIVE_DOUBLE = new ODBType(true,NATIVE_DOUBLE_ID, Double.TYPE.getName(), 8);

	public final static ODBType BYTE = new ODBType(false,BYTE_ID, Byte.class.getName(), 1);
	
	public final static ODBType SHORT = new ODBType(false,SHORT_ID, Short.class.getName(), 2);

	public final static ODBType INTEGER = new ODBType(false,INTEGER_ID, Integer.class.getName(), 4);

	public final static ODBType BIG_INTEGER = new ODBType(false,BIG_INTEGER_ID, BigInteger.class.getName(), 1);

	public final static ODBType LONG = new ODBType(false,LONG_ID, Long.class.getName(), 8);

	public final static ODBType FLOAT = new ODBType(false,FLOAT_ID, Float.class.getName(), 4);

	public final static ODBType DOUBLE = new ODBType(false,DOUBLE_ID, Double.class.getName(), 8);

	public final static ODBType BIG_DECIMAL = new ODBType(false,BIG_DECIMAL_ID, BigDecimal.class.getName(), 1);

	public final static ODBType CHARACTER = new ODBType(false,CHARACTER_ID, Character.class.getName(), 2);

	public final static ODBType BOOLEAN = new ODBType(false,BOOLEAN_ID, Boolean.class.getName(), 1);

	public final static ODBType DATE = new ODBType(false,DATE_ID, java.util.Date.class.getName(), 8);
	
	public final static ODBType DATE_SQL = new ODBType(false,DATE_SQL_ID, java.sql.Date.class.getName(), 8);
	
	public final static ODBType DATE_TIMESTAMP = new ODBType(false,DATE_TIMESTAMP_ID, java.sql.Timestamp.class.getName(), 8);
	
	public final static ODBType DATE_CALENDAR = new ODBType(false,DATE_CALENDAR_ID, java.util.Calendar.class.getName(), 8);
	public final static ODBType DATE_GREGORIAN_CALENDAR = new ODBType(false,DATE_GREGORIAN_CALENDAR_ID, java.util.GregorianCalendar.class.getName(), 8);

	public final static ODBType STRING = new ODBType(false,STRING_ID, String.class.getName(), 1);
	
	public final static ODBType ENUM = new ODBType(false,ENUM_ID, Enum.class.getName(), 1);

	public final static ODBType COLLECTION = new ODBType(false,COLLECTION_ID, Collection.class.getName(), 0, Collection.class, ArrayList.class);

	public final static ODBType ARRAY = new ODBType(false,ARRAY_ID, "array", 0);
    
    //public final static ODBType MAP = new ODBType(false,MAP_ID, "java.util.AbstractMap", 0, AbstractMap.class);
	public final static ODBType MAP = new ODBType(false,MAP_ID, Map.class.getName(), 0, Map.class,HashMap.class);
	public final static ODBType OID= new ODBType(false,OID_ID, org.neodatis.odb.OID.class.getName(), 0, org.neodatis.odb.OID.class);
	public final static ODBType OBJECT_OID= new ODBType(false,OBJECT_OID_ID, OdbObjectOID.class.getName(), 0, OdbObjectOID.class);
	public final static ODBType CLASS_OID= new ODBType(false,CLASS_OID_ID, OdbClassOID.class.getName(), 0, OdbClassOID.class);

	public final static ODBType NON_NATIVE = new ODBType(false,NON_NATIVE_ID, "non native", 0);

	private static final Map<Integer,ODBType> typesById = new OdbHashMap<Integer, ODBType>();

	private static final Map<String,ODBType> typesByName = new OdbHashMap<String, ODBType>();
	/** This cache is used to cache non default types. Instead or always testing if a class is an array or a collection or any other, we put the odbtype in this cache*/
	private static final Map<String,ODBType> cacheOfTypesByName = new OdbHashMap<String, ODBType>();
	public static final String DEFAULT_COLLECTION_CLASS_NAME = ArrayList.class.getName();
	public static final String DEFAULT_MAP_CLASS_NAME = HashMap.class.getName();
	public static final String DEFAULT_ARRAY_COMPONENT_CLASS_NAME = Object.class.getName();

	static final public int SIZE_OF_INT = ODBType.INTEGER.getSize();
	static final public int SIZE_OF_LONG = ODBType.LONG.getSize();
	public static final int SIZE_OF_BOOL = ODBType.BOOLEAN.getSize();
	public static final int SIZE_OF_BYTE = ODBType.BYTE.getSize();

	
	
	
	static {
	    IOdbList<ODBType> allTypes = new OdbArrayList<ODBType>(100);
        
        //// DO NOT FORGET DO ADD THE TYPE IN THIS LIST WHEN CREATING A NEW ONE!!!
        
	    allTypes.add(NULL);
	    allTypes.add(NATIVE_BOOLEAN);
		allTypes.add(NATIVE_BYTE);
		allTypes.add(NATIVE_CHAR);
		allTypes.add(NATIVE_SHORT);
		allTypes.add(NATIVE_INT);
		allTypes.add(NATIVE_LONG);
		allTypes.add(NATIVE_FLOAT);
		allTypes.add(NATIVE_DOUBLE);

		allTypes.add(BYTE);
		allTypes.add(SHORT);
		allTypes.add(INTEGER);
		allTypes.add(LONG);
		allTypes.add(FLOAT);
		allTypes.add(DOUBLE);
		allTypes.add(BIG_DECIMAL);
		allTypes.add(BIG_INTEGER);
		allTypes.add(CHARACTER);
		allTypes.add(BOOLEAN);
		allTypes.add(DATE);
		allTypes.add(DATE_SQL);
		allTypes.add(DATE_TIMESTAMP);
		// Not sure if we must manage calendar as native objects as they have time zone too
		//allTypes.add(DATE_CALENDAR);
		//allTypes.add(DATE_GREGORIAN_CALENDAR);
		allTypes.add(STRING);
		allTypes.add(ENUM);
		allTypes.add(COLLECTION);
		allTypes.add(ARRAY);
        allTypes.add(MAP);
        allTypes.add(OID);
        allTypes.add(OBJECT_OID);
        allTypes.add(CLASS_OID);
		allTypes.add(NON_NATIVE);

		
        ODBType type = null;
        for (int i = 0; i < allTypes.size(); i++) {
            type = allTypes.get(i);
			typesByName.put(type.getName(), type);
            typesById.put(new Integer(type.getId()),type);
		}
	}

	protected ODBType(boolean isPrimitive,int id, String name, int size) {
		this.isPrimitive = isPrimitive;
		this.id = id;
		this.name = name;
		this.size = size;
	}

	protected ODBType(boolean isPrimitive,int id, String name, int size, Class superclass) {
		this.isPrimitive  =isPrimitive;
		this.id = id;
		this.name = name;
		this.size = size;
		this.superClass = superclass;
	}
	protected ODBType(boolean isPrimitive,int id, String name, int size, Class superclass, Class defaultClass) {
		this(isPrimitive,id,name,size,superclass);
		this.defaultInstanciationClass = defaultClass;
	}

	private synchronized void initClassPool() {
		ODBType.classPool = OdbConfiguration.getCoreProvider().getClassPool();
		
	}
	public ODBType copy(){
		ODBType newType = new ODBType(isPrimitive,id,name,size);
		if(subType!=null){
			newType.subType = getSubType().copy();
		}
		return newType;
	}
	public static ODBType getFromId(int id) {
		ODBType odbType = typesById.get(new Integer(id));
		
		if(odbType==null){
			throw new ODBRuntimeException(NeoDatisError.ODB_TYPE_ID_DOES_NOT_EXIST.addParameter(id));
		}
		return odbType;
	}

	public static String getNameFromId(int id) {
		return getFromId(id).getName();
	}

	public static ODBType getFromName(String fullName) {
		ODBType tc = typesByName.get(fullName);
		if (tc != null) {
			return tc;
		}
		ODBType nonNative = new ODBType(ODBType.NON_NATIVE.isPrimitive,NON_NATIVE_ID,fullName,0);
		return nonNative;
		
	}

	public static ODBType getFromClass(Class clazz) {
		
		String className = clazz.getName();
		if(OdbClassUtil.isEnum(clazz)){
			ODBType type = new ODBType(ODBType.ENUM.isPrimitive,ODBType.ENUM_ID,ODBType.ENUM.getName(),0);
			type.setName(clazz.getName());
			return type;
		}

		// First check if it is a 'default type'
		ODBType tc = typesByName.get(className);
		if (tc != null) {
			return tc;
		}

		// Then check if it is a 'non default type'
		tc = cacheOfTypesByName.get(className);
		if (tc != null) {
			return tc;
		}

		if (isArray(clazz)) {
			ODBType type = new ODBType(ODBType.ARRAY.isPrimitive,ODBType.ARRAY_ID,ODBType.ARRAY.getName(),0);
			type.subType = getFromClass(clazz.getComponentType());
			cacheOfTypesByName.put(className, type);
			return type;
		}

        if (isMap(clazz)) {
        	cacheOfTypesByName.put(className, MAP);
            return MAP;
        }
        
        // check if it is a list
		if (isCollection(clazz)) {
			cacheOfTypesByName.put(className, COLLECTION);
			return COLLECTION;
		}
		nb++;
		ODBType nonNative = new ODBType(ODBType.NON_NATIVE.isPrimitive,NON_NATIVE_ID,clazz.getName(),0);
		cacheOfTypesByName.put(className, nonNative);
		return nonNative;
	}
	public static boolean isArray(Class clazz){
		return clazz.isArray();
	}
	public static boolean isMap(Class clazz){
		return MAP.superClass.isAssignableFrom(clazz);
	}
	public static boolean isCollection(Class clazz){
		return COLLECTION.superClass.isAssignableFrom(clazz);
	}
	public static boolean isNative(Class clazz) {
		ODBType tc = typesByName.get(clazz.getName());
		if (tc != null) {
			return true;
		}
		
		if (clazz.isArray()) {
			//ODBType type = new ODBType(ODBType.ARRAY.isPrimitive,ODBType.ARRAY_ID,ODBType.ARRAY.getName(),0);
			//type.subType = getFromClass(clazz.getComponentType());
			return true;
		}

        if (MAP.superClass.isAssignableFrom(clazz)) {
            return true;
        }
        
        // check if it is a list
		if (COLLECTION.superClass.isAssignableFrom(clazz)) {
			return true;
		}
		return false;

	}

	/*
	public static int getIdFromName(String name) {
		return getFromName(name).getId();
	}*/

	public static boolean exist(String name) {
		return typesByName.get(name) != null;
	}

	public final int getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public final int getSize() {
		return size;
	}

	public boolean isCollection() {
		return id == COLLECTION_ID;
	}
	public static boolean isCollection(int odbTypeId) {
		return odbTypeId == COLLECTION_ID;
	}

	public boolean isArray() {
		return id == ARRAY_ID;
	}
	public static boolean isArray(int odbTypeId) {
		return odbTypeId == ARRAY_ID;
	}

    public boolean isMap() {
        return id == MAP_ID;
    }
    public static boolean isMap(int odbTypeId) {
        return odbTypeId == MAP_ID;
    }

    public boolean isArrayOrCollection() {
		return isArray() || isCollection();
	}

    public static boolean isNative(int odbtypeId) {
		return odbtypeId != NON_NATIVE_ID;
	}

    public boolean isNative() {
		return id != NON_NATIVE_ID;
	}

	public ODBType getSubType() {
		return subType;
	}

	public Class getSuperClass() {
		return superClass;
	}

	public void setSuperClass(Class superClass) {
		this.superClass = superClass;
	}
	
	public String toString() {
		return id + " - " + name;
	}

	public void setSubType(ODBType subType) {
		this.subType = subType;
	}

	public boolean equals(Object obj) {
		if(obj==null || obj.getClass()!=ODBType.class){
			return false;
		}
		ODBType type = (ODBType) obj;
		
		return getId() == type.getId();
	}

	public Class getNativeClass() {
		switch (id) {
			case NATIVE_BOOLEAN_ID:return Boolean.TYPE;
			case NATIVE_BYTE_ID:return Byte.TYPE;
			case NATIVE_CHAR_ID:return Character.TYPE;
			case NATIVE_DOUBLE_ID:return Double.TYPE;
			case NATIVE_FLOAT_ID:return Float.TYPE;
			case NATIVE_INT_ID:return Integer.TYPE;
			case NATIVE_LONG_ID:return Long.TYPE;
			case NATIVE_SHORT_ID:return Short.TYPE;
			case OBJECT_OID_ID:return OdbObjectOID.class;
			case CLASS_OID_ID:return OdbClassOID.class;
			case OID_ID:return org.neodatis.odb.OID.class;
		}
		if(classPool==null){
			initClassPool();
		}
		return classPool.getClass(getName());
	}
	
	

	public boolean isNonNative(){
		return id==NON_NATIVE_ID;
	}
	public static boolean isNonNative(int odbtypeId){
		return odbtypeId==NON_NATIVE_ID;
	}
    
    public boolean isNull(){
        return id==NULL_ID;
    }
    public static boolean isNull(int odbTypeId){
        return odbTypeId==NULL_ID;
    }
    public boolean hasFixSize(){
        return hasFixSize(id);
    }
    public static boolean hasFixSize(int odbId){
        return odbId>0 && odbId<=NATIVE_FIX_SIZE_MAX_ID;
    	//return odbId != BIG_INTEGER_ID && odbId != BIG_DECIMAL_ID && odbId != STRING_ID && odbId != COLLECTION_ID && odbId!=ARRAY_ID && odbId!= MAP_ID && odbId!=NON_NATIVE_ID;
    }
    
    public boolean isStringOrBigDicemalOrBigInteger(){
        return isStringOrBigDicemalOrBigInteger(id);
    }
    public static boolean isStringOrBigDicemalOrBigInteger(int odbTypeId){
        return odbTypeId == STRING_ID || odbTypeId == BIG_DECIMAL_ID || odbTypeId == BIG_INTEGER_ID;
    }
    public static boolean isAtomicNative(int odbTypeId){
        return (odbTypeId>0 && odbTypeId<=NATIVE_MAX_ID) ;
    }
    public boolean isAtomicNative(){
        return isAtomicNative(id);
    }

    public static boolean isEnum(int odbTypeId){
    	return odbTypeId == ENUM_ID;
    }

    public boolean isEnum(){
    	return isEnum(id);
    }

    public static boolean isPrimitive(int odbTypeId){
    	return ODBType.getFromId(odbTypeId).isPrimitive;    	
    }

	public static boolean typesAreCompatible(ODBType type1, ODBType type2) {
		if(type1.isArray() && type2.isArray()){
			return typesAreCompatible(type1.getSubType(), type2.getSubType());
		}

		if(type1.getName().equals(type2.getName())){
			return true;
		}
		if(type1.isNative() && type2.isNative()){
			if(type1.isEquivalent(type2)){
				return true;
			}
			return false;
		}
		if(type1.isNonNative() && type2.isNonNative()){
			return (type1.getNativeClass() == type2.getNativeClass()) || (type1.getNativeClass().isAssignableFrom(type2.getNativeClass()));
		}
		return false;
		
	}
	public boolean isBoolean(){
		return id==BOOLEAN_ID||id==NATIVE_BOOLEAN_ID;
	}

	private boolean isEquivalent(ODBType type2) {
		return (id==INTEGER_ID&&type2.id==NATIVE_INT_ID) || (type2.id==INTEGER_ID&&id==NATIVE_INT_ID); 
	}

	public Class getDefaultInstanciationClass() {
		return defaultInstanciationClass;
	}

	public void init2() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public boolean isDate() {
		return id==DATE_ID || id==DATE_SQL_ID || id ==DATE_TIMESTAMP_ID;
	}
	
	
}
