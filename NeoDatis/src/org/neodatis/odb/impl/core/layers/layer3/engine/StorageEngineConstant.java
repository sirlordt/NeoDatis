
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
package org.neodatis.odb.impl.core.layers.layer3.engine;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
/** Some Storage engine constants about offset position for object writing/reading.
 * 
 */
public class StorageEngineConstant {

	

	/** Used to make an attribute reference a null object - setting its id to zero*/
	public static final OID NULL_OBJECT_ID = null;
	public static final long NULL_OBJECT_ID_ID= 0;
	// TODO Something is wrong here : two constant with the same value!!*/
	public static final long DELETED_OBJECT_POSITION = 0;
	public static final long NULL_OBJECT_POSITION = 0;
	public static final long OBJECT_IS_NOT_IN_CACHE = -1;
	public static final long POSITION_NOT_INITIALIZED = -1;
	public static final long OBJECT_DOES_NOT_EXIST = -2;
	/** this occurs when a class has been refactored adding a field. Old objects do not the new field*/
	public static final long FIELD_DOES_NOT_EXIST = -1;
	
	public final static byte VERSION_2 = 2;
	public final static byte VERSION_3 = 3;
	public final static byte VERSION_4 = 4;
	public final static byte VERSION_5 = 5;
	public final static byte VERSION_6 = 6;
    public final static byte VERSION_7 = 7;
    public final static int VERSION_8 = 8;
    /** 1.9 file format */
    public final static int VERSION_9 = 9;
	public static final int CURRENT_FILE_FORMAT_VERSION = VERSION_9;
	
	// ********************************************************
	// DATABASE HEADER
	// ********************************************************
	
	/** Use Encryption : 1 byte)**/
	public final static int DATABASE_HEADER_USE_ENCRYPTION_POSITION = 0;
	
	/** File format version : 1 int (4 bytes)*/
	public final static int DATABASE_HEADER_VERSION_POSITION = ODBType.BYTE.getSize();
	
	/** Future flag , may be to keep programming language that created the database: 1 byte*/
	public final static int DATABASE_HEADER_LANGUAGE_ID_POSITION = ODBType.INTEGER.getSize();
	
	/** The Database ID : 4 Long (4*8 bytes)*/
	public final static int DATABASE_HEADER_DATABASE_ID_POSITION = DATABASE_HEADER_LANGUAGE_ID_POSITION+ODBType.BYTE.getSize();
	
	/** To indicate if database uses replication : 1 byte)*/
	public final static int DATABASE_HEADER_USE_REPLICATION_POSITION = DATABASE_HEADER_DATABASE_ID_POSITION+4*ODBType.LONG.getSize();
	
	/** The last Transaction ID 2 long (2*4*8 bytes) */
	public final static int DATABASE_HEADER_LAST_TRANSACTION_ID = DATABASE_HEADER_USE_REPLICATION_POSITION+ODBType.BYTE.getSize();

	
	/** The number of classes in the meta model 1 long (4*8 bytes) */
	public final static int DATABASE_HEADER_NUMBER_OF_CLASSES_POSITION = DATABASE_HEADER_LAST_TRANSACTION_ID+2*ODBType.LONG.getSize();

	/** The first class OID : 1 Long (8 bytes)*/
    public final static int DATABASE_HEADER_FIRST_CLASS_OID = DATABASE_HEADER_NUMBER_OF_CLASSES_POSITION+ODBType.LONG.getSize();    
    
    /** The last ODB close status. Used to detect if the transaction is ok : 1 byte*/
	public final static int DATABASE_HEADER_LAST_CLOSE_STATUS_POSITION = DATABASE_HEADER_FIRST_CLASS_OID +ODBType.LONG.getSize();
	
	/** The Database character encoding : 50 bytes*/
	public final static int DATABASE_HEADER_DATABASE_CHARACTER_ENCODING_POSITION = DATABASE_HEADER_LAST_CLOSE_STATUS_POSITION +ODBType.BYTE.getSize();
	
	/** To indicate if database is password protected : 1 byte*/
	public final static int DATABASE_HEADER_DATABASE_IS_USER_PROTECTED = DATABASE_HEADER_DATABASE_CHARACTER_ENCODING_POSITION +58*ODBType.BYTE.getSize();
	
	/** The database user name : 50 bytes*/
	public final static int DATABASE_HEADER_DATABASE_USER_NAME = DATABASE_HEADER_DATABASE_IS_USER_PROTECTED +ODBType.BYTE.getSize();
	
	/** The database password : 50 bytes */
	public final static int DATABASE_HEADER_DATABASE_PASSWORD = DATABASE_HEADER_DATABASE_USER_NAME +58*ODBType.BYTE.getSize();
	
	/** The position of the current id block: 1 long*/
	public final static int DATABASE_HEADER_CURRENT_ID_BLOCK_POSITION = DATABASE_HEADER_DATABASE_PASSWORD +58*ODBType.BYTE.getSize();
	
	/** First ID Block position*/
	public final static int DATABASE_HEADER_FIRST_ID_BLOCK_POSITION = DATABASE_HEADER_CURRENT_ID_BLOCK_POSITION + ODBType.LONG.getSize();
	
	public final static int DATABASE_HEADER_PROTECTED_ZONE_SIZE = DATABASE_HEADER_CURRENT_ID_BLOCK_POSITION;
	
	public final static int[]DATABASE_HEADER_POSITIONS = {DATABASE_HEADER_USE_ENCRYPTION_POSITION,DATABASE_HEADER_VERSION_POSITION,DATABASE_HEADER_LANGUAGE_ID_POSITION,DATABASE_HEADER_DATABASE_ID_POSITION,DATABASE_HEADER_USE_REPLICATION_POSITION,DATABASE_HEADER_LAST_TRANSACTION_ID,DATABASE_HEADER_NUMBER_OF_CLASSES_POSITION,DATABASE_HEADER_FIRST_CLASS_OID,DATABASE_HEADER_LAST_CLOSE_STATUS_POSITION,DATABASE_HEADER_DATABASE_CHARACTER_ENCODING_POSITION,DATABASE_HEADER_DATABASE_IS_USER_PROTECTED,DATABASE_HEADER_DATABASE_PASSWORD,DATABASE_HEADER_CURRENT_ID_BLOCK_POSITION};
	// **********************************************************
	// END OF DATABASE HEADER
	// *********************************************************
	
	// CLASS OFFSETS
	public final static long CLASS_OFFSET_BLOCK_SIZE = 0;
	public final static long CLASS_OFFSET_BLOCK_TYPE = CLASS_OFFSET_BLOCK_SIZE + ODBType.INTEGER.getSize();
	public final static long CLASS_OFFSET_CATEGORY = CLASS_OFFSET_BLOCK_TYPE + ODBType.BYTE.getSize();
    public final static long CLASS_OFFSET_ID = CLASS_OFFSET_CATEGORY + ODBType.BYTE.getSize();
	public final static long CLASS_OFFSET_PREVIOUS_CLASS_POSITION = CLASS_OFFSET_ID+ ODBType.LONG.getSize();
	public final static long CLASS_OFFSET_NEXT_CLASS_POSITION = CLASS_OFFSET_PREVIOUS_CLASS_POSITION + ODBType.LONG.getSize();
	public final static long CLASS_OFFSET_CLASS_NB_OBJECTS = CLASS_OFFSET_NEXT_CLASS_POSITION + ODBType.LONG.getSize();
	public final static long CLASS_OFFSET_CLASS_FIRST_OBJECT_POSITION = CLASS_OFFSET_CLASS_NB_OBJECTS + ODBType.LONG.getSize();
	public final static long CLASS_OFFSET_CLASS_LAST_OBJECT_POSITION = CLASS_OFFSET_CLASS_FIRST_OBJECT_POSITION + ODBType.LONG.getSize();
	public final static long CLASS_OFFSET_FULL_CLASS_NAME_SIZE  = CLASS_OFFSET_NEXT_CLASS_POSITION + ODBType.LONG.getSize();
	
	//OBJECT OFFSETS - update this section when modifying the odb file format 
	public final static long OBJECT_OFFSET_BLOCK_SIZE = 0;
	public final static long OBJECT_OFFSET_BLOCK_TYPE = OBJECT_OFFSET_BLOCK_SIZE+ODBType.INTEGER.getSize();
	public final static long OBJECT_OFFSET_OBJECT_ID = OBJECT_OFFSET_BLOCK_TYPE+ODBType.BYTE.getSize();
	public final static long OBJECT_OFFSET_CLASS_INFO_ID = OBJECT_OFFSET_OBJECT_ID+ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_PREVIOUS_OBJECT_OID = OBJECT_OFFSET_CLASS_INFO_ID+ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_NEXT_OBJECT_OID = OBJECT_OFFSET_PREVIOUS_OBJECT_OID+ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_CREATION_DATE = OBJECT_OFFSET_NEXT_OBJECT_OID +ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_UPDATE_DATE = OBJECT_OFFSET_CREATION_DATE +ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_VERSION = OBJECT_OFFSET_UPDATE_DATE +ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_REFERENCE_POINTER = OBJECT_OFFSET_VERSION +ODBType.INTEGER.getSize();
	public final static long OBJECT_OFFSET_IS_EXTERNALLY_SYNCHRONIZED = OBJECT_OFFSET_REFERENCE_POINTER +ODBType.LONG.getSize();
	public final static long OBJECT_OFFSET_NB_ATTRIBUTES = OBJECT_OFFSET_IS_EXTERNALLY_SYNCHRONIZED+ODBType.NATIVE_BOOLEAN.getSize();

	
	/** <pre>
     ID Block Header : 
         Block size             : 1 int
         Block type             : 1 byte
         Block status           : 1 byte
         Prev block position    : 1 long
         Next block position    : 1 long
         Block number           : 1 int
         Max id                 : 1 long
         
         Total size = 34
     * </pre>
     * 
	 */
	public final static long BLOCK_ID_OFFSET_FOR_BLOCK_STATUS = ODBType.INTEGER.getSize() + ODBType.BYTE.getSize(); 
	public final static long BLOCK_ID_OFFSET_FOR_PREV_BLOCK = BLOCK_ID_OFFSET_FOR_BLOCK_STATUS + ODBType.BYTE.getSize();
	public final static long BLOCK_ID_OFFSET_FOR_NEXT_BLOCK = BLOCK_ID_OFFSET_FOR_PREV_BLOCK + ODBType.LONG.getSize();
    public final static long BLOCK_ID_OFFSET_FOR_BLOCK_NUMBER = BLOCK_ID_OFFSET_FOR_NEXT_BLOCK + ODBType.LONG.getSize();
    public final static long BLOCK_ID_OFFSET_FOR_MAX_ID = BLOCK_ID_OFFSET_FOR_BLOCK_NUMBER + ODBType.INTEGER.getSize();
	public final static long BLOCK_ID_OFFSET_FOR_START_OF_REPETITION = BLOCK_ID_OFFSET_FOR_MAX_ID + ODBType.LONG.getSize();
	
	/** pull id type (byte),id(long),*/
	
	public final static long BLOCK_ID_REPETITION_ID_TYPE = 0;
	public final static long BLOCK_ID_REPETITION_ID = BLOCK_ID_REPETITION_ID_TYPE + ODBType.BYTE.getSize();
	public final static long BLOCK_ID_REPETITION_ID_STATUS = BLOCK_ID_REPETITION_ID + ODBType.LONG.getSize(); 
	public final static long BLOCK_ID_REPETITION_OBJECT_POSITION = BLOCK_ID_REPETITION_ID_STATUS + ODBType.BYTE.getSize();
		
		
    
    public final static long NATIVE_OBJECT_OFFSET_BLOCK_SIZE = 0;
    public final static long NATIVE_OBJECT_OFFSET_BLOCK_TYPE = NATIVE_OBJECT_OFFSET_BLOCK_SIZE + ODBType.INTEGER.getSize();
    public final static long NATIVE_OBJECT_OFFSET_ODB_TYPE_ID = NATIVE_OBJECT_OFFSET_BLOCK_TYPE + ODBType.BYTE.getSize();
    public final static long NATIVE_OBJECT_OFFSET_OBJECT_IS_NULL = NATIVE_OBJECT_OFFSET_ODB_TYPE_ID + ODBType.INTEGER.getSize();
    public final static long NATIVE_OBJECT_OFFSET_DATA_AREA = NATIVE_OBJECT_OFFSET_OBJECT_IS_NULL + ODBType.BOOLEAN.getSize();
	
    // Encryption flag
    public static final byte NO_ENCRYPTION = 0;
    public static final byte WITH_ENCRYPTION = 1;
    
    // Replication flag
    public static final byte NO_REPLICATION = 0;
    public static final byte WITH_REPLICATION = 1;
	public static final String NO_ENCODING = "no-encoding";
	
	
	
}
