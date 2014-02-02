/*
 NeoDatis ODB : Native Object Database (odb.info@1neodatis.org)
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
package org.neodatis.odb.core;

import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * All NeoDatis ODB Errors. Errors can be user errors or Internal errors. All @1
 * in error description will be replaced by parameters
 * 
 * @1author olivier s
 * 
 */
public class NeoDatisError implements IError {
	private int code;

	private String description;

	private IOdbList<Object> parameters;

	// Internal errors
	public static final NeoDatisError NULL_NEXT_OBJECT_OID = new NeoDatisError(100,
			"ODB has detected an inconsistency while reading instance(of @1) #@2 over @3 with oid @4 which has a null 'next object oid'");
	public static final NeoDatisError INSTANCE_POSITION_OUT_OF_FILE = new NeoDatisError(101,
			"ODB is trying to read an instance at position @1 which is out of the file - File size is @2");
	public static final NeoDatisError INSTANCE_POSITION_IS_NEGATIVE = new NeoDatisError(102,
			"ODB is trying to read an instance at a negative position @1 , oid=@2 : @3");
	public static final NeoDatisError WRONG_TYPE_FOR_BLOCK_TYPE = new NeoDatisError(201,
			"Block type of wrong type : expected @1, Found @2 at position @3");
	public static final NeoDatisError WRONG_BLOCK_SIZE = new NeoDatisError(202, "Wrong Block size : expected @1, Found @2 at position @3");
	public static final NeoDatisError WRONG_OID_AT_POSITION = new NeoDatisError(203,
			"Reading object with oid @1 at position @2, but found oid @3");
	public static final NeoDatisError BLOCK_NUMBER_DOES_EXIST = new NeoDatisError(205, "Block(of ids) with number @1 does not exist");
	public static final NeoDatisError FOUND_POINTER = new NeoDatisError(204, "Found a pointer for oid @1 at position @2");
	public static final NeoDatisError OBJECT_IS_MARKED_AS_DELETED_FOR_OID = new NeoDatisError(206,
			"Object with oid @1 is marked as deleted");
	public static final NeoDatisError OBJECT_IS_MARKED_AS_DELETED_FOR_POSITION = new NeoDatisError(207,
			"Object with position @1 is marked as deleted");
	public static final NeoDatisError NATIVE_TYPE_NOT_SUPPORTED = new NeoDatisError(208, "Native type not supported @1 @2");
	public static final NeoDatisError NATIVE_TYPE_DIVERGENCE = new NeoDatisError(209,
			"Native type informed(@1) is different from the one informed (@2)");
	public static final NeoDatisError NEGATIVE_CLASS_NUMBER_IN_HEADER = new NeoDatisError(210,
			"number of classes is negative while reading database header : @1 at position @2");
	public static final NeoDatisError UNKNOWN_BLOCK_TYPE = new NeoDatisError(211, "Unknown block type @1 at @2");
	public static final NeoDatisError UNSUPPORTED_IO_TYPE = new NeoDatisError(212, "Unsupported IO Type : @1");
	public static final NeoDatisError OBJECT_DOES_NOT_EXIST_IN_CACHE = new NeoDatisError(213, "Object does not exist in cache");
	public static final NeoDatisError OBJECT_WITH_OID_DOES_NOT_EXIST_IN_CACHE = new NeoDatisError(213,
			"Object with oid @1 does not exist in cache");
	public static final NeoDatisError OBJECT_INFO_NOT_IN_TEMP_CACHE = new NeoDatisError(214,
			"ObjectInfo does not exist in temporary cache oid=@1 and position=@2");
	public static final NeoDatisError CAN_NOT_DELETE_FILE = new NeoDatisError(215, "Can not delete file @1");
	public static final NeoDatisError GO_TO_POSITION = new NeoDatisError(216, "Error while going to position @1, length = @2");
	public static final NeoDatisError ERROR_IN_CORE_PROVIDER_INITIALIZATION = new NeoDatisError(217,
			"Error while initializing CoreProvider @1");
	public static final NeoDatisError UNDEFINED_CLASS_INFO = new NeoDatisError(218, "Undefined class info for @1");
	public static final NeoDatisError ABSTRACT_OBJECT_INFO_TYPE_NOT_SUPPORTED = new NeoDatisError(219,
			"Abstract Object Info type not supported : @1");
	public static final NeoDatisError NEGATIVE_BLOCK_SIZE = new NeoDatisError(220, "Negative block size at @1 : size = @2, object=@3");
	public static final NeoDatisError INPLACE_UPDATE_NOT_POSSIBLE_FOR_ARRAY = new NeoDatisError(221,
			"Array in place update with array smaller than element array index to update : array size=@1, element index=@2");
	public static final NeoDatisError OPERATION_NOT_IMPLEMENTED = new NeoDatisError(222, "Operation not supported : @1");
	public static final NeoDatisError INSTANCE_BUILDER_WRONG_OBJECT_TYPE = new NeoDatisError(223,
			"Wrong type of object: expecting @1 and received @2");
	public static final NeoDatisError INSTANCE_BUILDER_WRONG_OBJECT_CONTAINER_TYPE = new NeoDatisError(224,
			"Building instance of @1 : can not put a @2 into a @3");
	public static final NeoDatisError INSTANCE_BUILDER_NATIVE_TYPE_IN_COLLECTION_NOT_SUPPORTED = new NeoDatisError(225,
			"Native @1 in Collection(List,array,Map) not supported");
	public static final NeoDatisError OBJECT_INTROSPECTOR_NO_FIELD_WITH_NAME = new NeoDatisError(226,
			"Class/Interface @1 does not have attribute '@2'");
	public static final NeoDatisError OBJECT_INTROSPECTOR_CLASS_NOT_FOUND = new NeoDatisError(227, "Class not found : @1");
	public static final NeoDatisError CLASS_POOL_CREATE_CLASS = new NeoDatisError(228, "Error while creating (reflection) class @1");
	public static final NeoDatisError BUFFER_TOO_SMALL = new NeoDatisError(229,
			"Buffer too small: buffer size = @1 and data size = @2 - should not happen");
	public static final NeoDatisError FILE_INTERFACE_WRITE_BYTES_NOT_IMPLEMENTED_FOR_TRANSACTION = new NeoDatisError(230,
			"writeBytes not implemented for transactions");
	public static final NeoDatisError FILE_INTERFACE_READ_ERROR = new NeoDatisError(231,
			"Error reading @1 bytes at @2 : read @3 bytes instead");
	public static final NeoDatisError POINTER_TO_SELF = new NeoDatisError(232,
			"Error while creating a pointer : a pointer to itself : @1 -> @2 for oid @3");
	public static final NeoDatisError INDEX_NOT_FOUND = new NeoDatisError(233, "No index defined on class @1 at index position @2");
	public static final NeoDatisError NOT_YET_IMPLEMENTED = new NeoDatisError(234, "Not yet implemented : @1");
	public static final NeoDatisError META_MODEL_CLASS_NAME_DOES_NOT_EXIST = new NeoDatisError(235, "Class @1 does not exist in meta-model");
	public static final NeoDatisError META_MODEL_CLASS_WITH_OID_DOES_NOT_EXIST = new NeoDatisError(236,
			"Class with oid @1 does not exist in meta-model");
	public static final NeoDatisError META_MODEL_CLASS_WITH_POSITION_DOES_NOT_EXIST = new NeoDatisError(237,
			"Class with position @1 does not exist in meta-model");
	public static final NeoDatisError CLASS_INFO_DO_NOT_HAVE_THE_ATTRIBUTE = new NeoDatisError(238,
			"Class @1 does not have attribute with name @2 in the database meta-model");
	public static final NeoDatisError ODB_TYPE_ID_DOES_NOT_EXIST = new NeoDatisError(239, "ODBtype with id @1 does not exist");
	public static final NeoDatisError ODB_TYPE_NATIVE_TYPE_WITH_ID_DOES_NOT_EXIST = new NeoDatisError(240,
			"Native type with id @1 does not exist");
	public static final NeoDatisError QUERY_ENGINE_NOT_SET = new NeoDatisError(241, "Storage engine not set on query");
	public static final NeoDatisError QUERY_TYPE_NOT_IMPLEMENTED = new NeoDatisError(242, "Query type @1 not implemented");
	public static final NeoDatisError CRYPTO_ALGORITH_NOT_FOUND = new NeoDatisError(243,
			"Could not get the MD5 algorithm to encrypt the password");
	public static final NeoDatisError XML_HEADER = new NeoDatisError(244, "Error while creating XML Header");
	public static final NeoDatisError XML_RESERVING_IDS = new NeoDatisError(245, "Error while reserving @1 ids");
	public static final NeoDatisError XML_SETTING_META_MODEL = new NeoDatisError(246, "Error while setting meta model");
	public static final NeoDatisError SERIALIZATION_FROM_STRING = new NeoDatisError(247,
			"Error while deserializing: expecting classId @1 and received @2");
	public static final NeoDatisError SERIALIZATION_COLLECTION = new NeoDatisError(248,
			"Error while deserializing collection: sizes are not consistent : expected @1, found @2");
	public static final NeoDatisError METAMODEL_READING_LAST_OBJECT = new NeoDatisError(249,
			"Error while reading last object of type @1 at with OID @2");
	public static final NeoDatisError CACHE_NEGATIVE_OID = new NeoDatisError(250, "Negative oid set in cache @1");
	public static final NeoDatisError CLIENT_SERVER_SYNCHRONIZE_IDS = new NeoDatisError(251,
			"Error while synchronizing oids,length are <>, local=@1, client=@2");
	public static final NeoDatisError CLIENT_SERVER_CAN_NOT_OPEN_ODB_SERVER_ON_PORT = new NeoDatisError(252,
			"Can not start ODB server on port @1");
	public static final NeoDatisError CLIENT_SERVER_CAN_NOT_ASSOCIATE_OIDS = new NeoDatisError(253,
			"Can not associate server and client oids : server oid=@1 and client oid=@2");
	public static final NeoDatisError SESSION_DOES_NOT_EXIST_FOR_CONNECTION = new NeoDatisError(254,
			"Connection @1 for base @2 does not have any associated session");
	public static final NeoDatisError SESSION_DOES_NOT_EXIST_FOR_CONNECTION_ID = new NeoDatisError(255,
			"Connection ID @1 does not have any associated session");
	public static final NeoDatisError CLIENT_SERVER_ERROR = new NeoDatisError(256, "ServerSide Error : @1");
	public static final NeoDatisError OBJECT_READER_DIRECT_CALL = new NeoDatisError(257,
			"Generic readObjectInfo called for non native object info");
	public static final NeoDatisError CACHE_OBJECT_INFO_HEADER_WITHOUT_CLASS_ID = new NeoDatisError(258,
			"Object Info Header without class id ; oih.oid=@1");
	public static final NeoDatisError NON_NATIVE_ATTRIBUTE_STORED_BY_POSITION_INSTEAD_OF_OID = new NeoDatisError(259,
			"Non native attribute (@1) of class @2 stored by position @3 instead of oid");
	public static final NeoDatisError CACHE_NULL_OID = new NeoDatisError(260, "Null OID");
	public static final NeoDatisError NEGATIVE_POSITION = new NeoDatisError(261, "Negative position : @1");
	public static final NeoDatisError UNEXPECTED_SITUATION = new NeoDatisError(262, "Unexpected situation: @1");
	public static final NeoDatisError IMPORT_ERROR = new NeoDatisError(263, "Import error: @1");
	public static final NeoDatisError CLIENT_SERVER_CAN_NOT_CREATE_CLASS_INFO = new NeoDatisError(264,
			"ServerSide Error : Can not create class info @1");
	public static final NeoDatisError CLIENT_SERVER_META_MODEL_INCONSISTENCY = new NeoDatisError(265,
			"ServerSide Error : Meta model on server and client are inconsistent : class @1 exist on server and does not exist on the client!");
	public static final NeoDatisError CLIENT_SERVER_META_MODEL_INCONSISTENCY_DIFFERENT_OID = new NeoDatisError(266,
			"ServerSide Error : Meta model on server and client are inconsistent : class @1 have different OIDs on server (@2) and client(@3)!");
	public static final NeoDatisError METHOD_SHOULD_NOT_BE_CALLED = new NeoDatisError(267, "Method @1 should not be called on @2");
	public static final NeoDatisError CACHE_NEGATIVE_POSITION = new NeoDatisError(268,
			"Caching an ObjectInfoHeader with negative position @1");
	public static final NeoDatisError ERROR_WHILE_GETTING_OBJECT_FROM_LIST_AT_INDEX = new NeoDatisError(269,
			"Error while getting object from list at index @1");
	public static final NeoDatisError CLASS_INFO_DOES_NOT_EXIST_IN_META_MODEL = new NeoDatisError(270,
			"Class Info @1 does not exist in MetaModel");
	public static final NeoDatisError BTREE_SIZE_DIFFERS_FROM_CLASS_ELEMENT_NUMBER = new NeoDatisError(271,
			"The Index has @1 element(s) whereas the Class has @2 objects. The two values should be equal");
	public static final NeoDatisError CLIENT_SERVER_CONNECTION_IS_NULL = new NeoDatisError(272,
			"The connection ID @1 does not exist in connection manager (@2)");
	public static final NeoDatisError CLIENT_SERVER_PORT_IS_BUSY = new NeoDatisError(273,
			"Can not start ODB server on port @1: The port is busy. Check if another server is not already running of this port");
	public static final NeoDatisError INSTANCE_BUILDER_NATIVE_TYPE = new NeoDatisError(274,
			"Native object of type @1 can not be instanciated");
	public static final NeoDatisError CLASS_INTROSPECTION_ERROR = new NeoDatisError(275, "Class Introspectpr error for class @1");
	public static final IError END_OF_FILE_REACHED = new NeoDatisError(276, "End Of File reached - position = @1 : Length = @2");
	public static final IError MAP_INSTANCIATION_ERROR = new NeoDatisError(277, "Error while creating instance of MAP of class @1");
	public static final IError COLLECTION_INSTANCIATION_ERROR = new NeoDatisError(278,
			"Error while creating instance of Collection of class @1");
	public static final IError INSTANCIATION_ERROR = new NeoDatisError(279, "Error while creating instance of type @1");
	public static final IError SERVER_SIDE_ERROR = new NeoDatisError(280, "Server side error @1 : @2");
	public static final IError NET_SERIALISATION_ERROR = new NeoDatisError(281, "Net Serialization Error : @1 \n@2");
	public static final IError CLIENT_NET_ERROR = new NeoDatisError(282, "Client Net Error");
	public static final IError SERVER_NET_ERROR = new NeoDatisError(283, "Server Net Error");
	public static final IError ERROR_WHILE_GETTING_CONSTRCTORS_OF_CLASS = new NeoDatisError(284, "Error while getting constructor of @1");
	public static final IError UNKNOWN_HOST = new NeoDatisError(285, "Unknown host");
	public static final NeoDatisError CACHE_NULL_OBJECT = new NeoDatisError(286, "Null Object : @1");
	public static final NeoDatisError LOOKUP_KEY_NOT_FOUND = new NeoDatisError(287, "Lookup key not found : @1");
	public static final NeoDatisError SERVER_ERROR = new NeoDatisError(288, "Server error : @1");
	public static final NeoDatisError REFLECTION_ERROR_WHILE_GETTING_FIELD = new NeoDatisError(289,
			"Error while getting field @1 on class @2");
	public static final NeoDatisError NOT_YET_SUPPORTED = new NeoDatisError(290, "Not Yet Supported : @1");
	public static final NeoDatisError FILE_NOT_FOUND = new NeoDatisError(291, "File not found: @1");
	public static final NeoDatisError INDEX_IS_CORRUPTED = new NeoDatisError(292,
			"Index '@1' of class '@2' is corrupted: class has @3 objects, index has @4 entries, try to rebuild the index");
	public static final NeoDatisError ERROR_WHILE_CREATING_MESSAGE_STREAMER = new NeoDatisError(293,
			"Error while creating message streamer '@1'");
	public static final NeoDatisError CLIENT_SERVER_UNKNOWN_COMMAND = new NeoDatisError(294, "Unknown server command : @1");
	public static final NeoDatisError CONTAINS_QUERY_WITH_NO_QUERY = new NeoDatisError(295, "Contains criteria with no query!");
	public static final NeoDatisError CONTAINS_QUERY_WITH_NO_STORAGE_ENGINE = new NeoDatisError(296, "Contains criteria with no engine!");
	public static final NeoDatisError CROSS_SESSION_CACHE_NULL_OID_FOR_OBJECT = new NeoDatisError(297, "Cross session cache does not know the object @1");
	public static final NeoDatisError ERROR_WHILE_GETTING_IP_ADDRESS = new NeoDatisError(298, "Error while getting IP address of @1");
	public static final NeoDatisError ERROR_WHILE_ACQUIRING_MUTEX = new NeoDatisError(299, "Error while acquiring mutex @1");
	public static final NeoDatisError TIMEOUT_WHILE_ACQUIRING_MUTEX = new NeoDatisError(300, "Timeout acquiring mutex @1");
	
	// *********************************************
	// User errors
	// *********************************************
	public static final NeoDatisError CRITERIA_QUERY_UNKNOWN_ATTRIBUTE = new NeoDatisError(1000,
			"Attribute @1 used in criteria queria does not exist on class @2");
	public static final NeoDatisError RUNTIME_INCOMPATIBLE_VERSION = new NeoDatisError(1001,
			"Incompatible ODB Version : ODB file version is @1 and Runtime version is @2");
	public static final NeoDatisError INCOMPATIBLE_METAMODEL = new NeoDatisError(1002, "Incompatible meta-model : @1");
	public static final NeoDatisError INCOMPATIBLE_JAVA_VM = new NeoDatisError(1003,
			"Incompatible java virtual Machine, 1.5 or greater is required, you are using : @1");
	public static final NeoDatisError ODB_IS_CLOSED = new NeoDatisError(1004, "ODB session has already been closed (@1)");
	public static final NeoDatisError ODB_HAS_BEEN_ROLLBACKED = new NeoDatisError(1005, "ODB session has been rollbacked (@1)");
	public static final NeoDatisError ODB_CAN_NOT_STORE_NULL_OBJECT = new NeoDatisError(1006, "ODB can not store null object");
	public static final NeoDatisError ODB_CAN_NOT_STORE_ARRAY_DIRECTLY = new NeoDatisError(1007, "ODB can not store array directly : @1");
	public static final NeoDatisError ODB_CAN_NOT_STORE_NATIVE_OBJECT_DIRECTLY = new NeoDatisError(1008,
			"NeoDats ODB can not store native object direclty : @1 which is or seems to be a @2. Workaround: Wrap class @3 into another class");
	public static final NeoDatisError OBJECT_DOES_NOT_EXIST_IN_CACHE_FOR_DELETE = new NeoDatisError(1009,
			"The object being deleted does not exist in cache. Make sure the object has been loaded before deleting : type=@1 object=[@2]");
	public static final NeoDatisError TRANSACTION_IS_PENDING = new NeoDatisError(1010,
			"There are pending work associated to current transaction, a commit or rollback should be executed : session id = @1");
	public static final NeoDatisError UNKNOWN_OBJECT_TO_GET_OID = new NeoDatisError(1011, "Unknown object @1");
	public static final NeoDatisError ODB_CAN_NOT_RETURN_OID_OF_NULL_OBJECT = new NeoDatisError(1012,
			"Can not return the oid of a null object");
	public static final NeoDatisError ODB_FILE_IS_LOCKED_BY_CURRENT_VIRTUAL_MACHINE = new NeoDatisError(
			1013,
			"@1 file is locked by the current Virtual machine - check if the database has not been opened in the current VM! : thread = @2 - using multi thread ? @3");
	public static final NeoDatisError ODB_FILE_IS_LOCKED_BY_EXTERNAL_PROGRAM = new NeoDatisError(1014,
			"@1 file is locked - check if the database file is not opened in another program! : thread = @2 - using multi thread ? @3");
	public static final NeoDatisError USER_NAME_TOO_LONG = new NeoDatisError(1015,
			"User name @1 is too long, should be lesser than 20 characters");
	public static final NeoDatisError PASSWORD_TOO_LONG = new NeoDatisError(1016,
			"Password is too long, it must be less than 20 character long");
	public static final NeoDatisError TRANSACTION_ALREADY_COMMITED_OR_ROLLBACKED = new NeoDatisError(1017,
			"Transaction have already been 'committed' or 'rollbacked'");
	public static final NeoDatisError DIFFERENT_SIZE_IN_WRITE_ACTION = new NeoDatisError(1018,
			"Size difference in WriteAction.persist :(calculated,stored)=(@1,@2)");
	public static final NeoDatisError CLASS_WITHOUT_CONSTRUCTOR = new NeoDatisError(1019, "Class without any constructor : @1");
	public static final NeoDatisError NO_NULLABLE_CONSTRUCTOR = new NeoDatisError(
			1020,
			"Constructor @1 of class @2 was called with null values because it does not have default constructor and it seems the constructor is not prepared for this!");
	public static final NeoDatisError QUERY_BAD_CRITERIA = new NeoDatisError(1021,
			"CollectionSizeCriteria only work with Collection or Array, and you passed a @1 instead");
	public static final NeoDatisError QUERY_COLLECTION_SIZE_CRITERIA_NOT_SUPPORTED = new NeoDatisError(1022,
			"CollectionSizeCriterion sizeType @1 not yet implemented");
	public static final NeoDatisError QUERY_COMPARABLE_CRITERIA_APPLIED_ON_NON_COMPARABLE = new NeoDatisError(1023,
			"ComparisonCriteria with greater than only work with Comparable, and you passed a @1 instead");
	public static final NeoDatisError QUERY_UNKNOWN_OPERATOR = new NeoDatisError(1024, "Unknow operator @1");
	public static final NeoDatisError QUERY_CONTAINS_CRITERION_TYPE_NOT_SUPPORTED = new NeoDatisError(1025,
			"Where.contain can not be used with a @1, only collections and arrays are supported");
	public static final NeoDatisError QUERY_ATTRIBUTE_TYPE_NOT_SUPPORTED_IN_LIKE_EXPRESSION = new NeoDatisError(1026,
			"LikeCriteria with like expression(%) only work with String, and you passed a @1 instead");
	public static final NeoDatisError INDEX_KEYS_MUST_IMPLEMENT_COMPARABLE = new NeoDatisError(1027,
			"Unable to build index key for attribute that does not implement 'Comparable/IComparable' : Index=@1, attribute = @2 , type = @3");
	public static final NeoDatisError QUERY_NQ_MATCH_METHOD_NOT_IMPLEMENTED = new NeoDatisError(1029,
			"ISimpleNativeQuery implementing classes must implement method: boolean match(?Object obj), class @1 does not");
	public static final NeoDatisError QUERY_NQ_EXCEPTION_RAISED_BY_NATIVE_QUERY_EXECUTION = new NeoDatisError(1030,
			"Exception raised by the native query @1 match method");
	public static final NeoDatisError ODB_CAN_NOT_RETURN_OID_OF_UNKNOWN_OBJECT = new NeoDatisError(1031,
			"Can not return the oid of a not previously loaded object : @1");
	public static final NeoDatisError ERROR_WHILE_ADDING_OBJECT_TO_HASHMAP = new NeoDatisError(1032,
			"Internal error in user object of class @1 in equals or hashCode method : @2");
	public static final NeoDatisError ATTRIBUTE_REFERENCES_A_DELETED_OBJECT = new NeoDatisError(1033,
			"Object of type @1 with oid @2 has the attribute '@3' that references a deleted object");
	public static final NeoDatisError BEFORE_DELETE_TRIGGER_HAS_THROWN_EXCEPTION = new NeoDatisError(1034,
			"Before Delete Trigger @1 has thrown exception. ODB has ignored it \n<user exception>\n@2</user exception>");
	public static final NeoDatisError AFTER_DELETE_TRIGGER_HAS_THROWN_EXCEPTION = new NeoDatisError(1035,
			"After Delete Trigger @1 has thrown exception. ODB has ignored it\n<user exception>\n@2</user exception>");
	public static final NeoDatisError BEFORE_UPDATE_TRIGGER_HAS_THROWN_EXCEPTION = new NeoDatisError(1036,
			"Before Update Trigger @1 has thrown exception. ODB has ignored it\n<user exception>\n@2</user exception>");
	public static final NeoDatisError AFTER_UPDATE_TRIGGER_HAS_THROWN_EXCEPTION = new NeoDatisError(1037,
			"After Update Trigger @1 has thrown exception. ODB has ignored it\n<user exception>\n@2</user exception>");
	public static final NeoDatisError BEFORE_INSERT_TRIGGER_HAS_THROWN_EXCEPTION = new NeoDatisError(1038,
			"Before Insert Trigger @1 has thrown exception. ODB has ignored it\n<user exception>\n@2</user exception>");
	public static final NeoDatisError AFTER_INSERT_TRIGGER_HAS_THROWN_EXCEPTION = new NeoDatisError(1039,
			"After Insert Trigger @1 has thrown exception. ODB has ignored it\n<user exception>\n@2</user exception>");
	public static final NeoDatisError NO_MORE_OBJECTS_IN_COLLECTION = new NeoDatisError(1040, "No more objects in collection");
	public static final NeoDatisError INDEX_ALREADY_EXIST = new NeoDatisError(1041, "Index @1 already exist on class @2");
	public static final NeoDatisError INDEX_DOES_NOT_EXIST = new NeoDatisError(1042, "Index @1 does not exist on class @2");
	public static final NeoDatisError QUERY_ATTRIBUTE_TYPE_NOT_SUPPORTED_IN_IEQUAL_EXPRESSION = new NeoDatisError(1043,
			"EqualCriteria with case insensitive expression only work with String, and you passed a @1 instead");
	public static final NeoDatisError VALUES_QUERY_ALIAS_DOES_NOT_EXIST = new NeoDatisError(1044,
			"Alias @1 does not exist in query result. Existing alias are @2");
	public static final NeoDatisError VALUES_QUERY_NOT_CONSISTENT = new NeoDatisError(1045,
			"Single row actions (like sum,count,min,max) are declared together with multi row actions : @1");
	public static final NeoDatisError VALUES_QUERY_ERROR_WHILE_CLONING_CUSTUM_QFA = new NeoDatisError(1046,
			"Error while cloning Query Field Action @1");
	public static final NeoDatisError EXECUTION_PLAN_IS_NULL_QUERY_HAS_NOT_BEEN_EXECUTED = new NeoDatisError(1047,
			"The query has not been executed yet so there is no execution plan available");
	public static final NeoDatisError OBJECT_WITH_OID_DOES_NOT_EXIST = new NeoDatisError(1048,
			"Object with OID @1 does not exist in the database");
	public static final NeoDatisError PARAM_HELPER_WRONG_NO_OF_PARAMS = new NeoDatisError(1049,
			"The ParameterHelper for the class @1 didn't provide the correct number of parameters for the constructor @2");
	public static final NeoDatisError CACHE_IS_FULL = new NeoDatisError(
			1050,
			"Cache is full! ( it has @1 object(s). The maximum size is @2. Please increase the size of the cache using Configuration.setMaxNumberOfObjectInCache, or call the Configuration.setAutomaticallyIncreaseCacheSize(true)");
	public static final NeoDatisError UNREGISTERED_BASE_ON_SERVER = new NeoDatisError(1051,
			"Base @1 must be added on server before configuring it");
	public static final IError UNSUPPORTED_ENCODING = new NeoDatisError(1052, "Unsupported encoding @1");;
	public static final IError RECONNECT_ONLY_WITH_BYTE_CODE_AGENT_CONFIGURED = new NeoDatisError(1053,
			"Reconnect object only available when Byte code instrumentation is on");
	public static final IError RECONNECT_ONLY_FOR_PREVIOUSLY_LOADED_OBJECT = new NeoDatisError(1054,
			"Reconnect object only available for objets previously loaded in an ODB Session");
	public static final IError RECONNECT_CAN_RECONNECT_NULL_OBJECT = new NeoDatisError(1055, "Can not reconnect null object");
	public static final IError CAN_NOT_GET_OBJECT_FROM_NULL_OID = new NeoDatisError(1056, "Can not get object from null OID");
	public static final IError INVALID_OID_REPRESENTATION = new NeoDatisError(1057, "Invalid OID representation : @1");
	public static final IError DUPLICATED_KEY_IN_INDEX = new NeoDatisError(1058, "Duplicate key on index @1 : Values of index key @2");

	public static final IError OPERATION_NOT_ALLOWED_IN_TRIGGER = new NeoDatisError(1056, "Operation not allowed in trigger");
	public static final IError CAN_NOT_ASSOCIATE_SERVER_TRIGGER_TO_LOCAL_OR_CLIENT_ODB = new NeoDatisError(1057,
			"Can not associate server trigger @1 to local or client ODB");
	public static final IError TRIGGER_CALLED_ON_NULL_OBJECT = new NeoDatisError(1058,
			"Trigger has been called on class @1 on a null object so it cannot retrieve the value of the '@2' attribute");
	public static final IError CRITERIA_QUERY_ON_UNKNOWN_OBJECT = new NeoDatisError(1059,
			"When the right side of a Criteria query is an object, this object must have been previously loaded by NeoDatis");
	public static final IError RECONNECT_CAN_NOT_RECONNECT_OBJECT = new NeoDatisError(1060, "Can not reconnect object");
	public static final NeoDatisError ODB_CAN_NOT_DELETE_NULL_OBJECT = new NeoDatisError(1061, "NeoDatis can not delete null object");
	public static final NeoDatisError FORMT_INVALID_DATE_FORMAT = new NeoDatisError(1062, "Invalid date format:@1, expecting something like @2");
	public static final NeoDatisError EQUAL_CRITERIA_ON_OBJECT_WITH_WRONG_ATTRIBUTE_TYPE = new NeoDatisError(1063, "A where.equal (@1) has been created on an object, but the attribute '@2' seems to be a @3, Exception is\n@4");
	//@neodatisee
	public static final IError NULL_ENCODING = new NeoDatisError(1064, "Null encoding are not allowed");;
	
	public static final NeoDatisError INTERNAL_ERROR = new NeoDatisError(10, "Internal error : @1 ");

	public static final IError ERROR_IN_RETURN_VALUE_PROCESSOR = new NeoDatisError(1065, "Error in Return Value Processor @1 managing return value @2"); 

	public static final IError ERROR_WHILE_COMPUTING_KEY_FOR_INDEX_NULL_FIELD = new NeoDatisError(1066, "Attribute @1 is null : error while computing @2 key for object @3");
	public static final IError ERROR_WHILE_MANAGING_INDEX = new NeoDatisError(1067, "Error while managing index @1");
	

	public NeoDatisError(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public IError addParameter(Object o) {
		if (parameters == null) {
			parameters = new OdbArrayList<Object>();
		}
		parameters.add(String.valueOf(o));
		return this;
	}

	public IError addParameter(String s) {
		if (parameters == null) {
			parameters = new OdbArrayList<Object>();
		}
		if (s == null) {
			parameters.add("[null object]");
		} else {
			parameters.add(s);
		}
		return this;
	}

	public IError addParameter(int i) {
		if (parameters == null) {
			parameters = new OdbArrayList<Object>();
		}
		parameters.add(new Integer(i));
		return this;
	}

	public IError addParameter(byte i) {
		if (parameters == null) {
			parameters = new OdbArrayList<Object>();
		}
		parameters.add(new Byte(i));
		return this;
	}

	public IError addParameter(long l) {
		if (parameters == null) {
			parameters = new OdbArrayList<Object>();
		}
		parameters.add(new Long(l));
		return this;
	}

	/**
	 * replace the @1,@2,... by their real values.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(code).append(":").append(description);
		String s = buffer.toString();

		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++) {
				String parameterName = "@" + (i + 1);
				String parameterValue = parameters.get(i).toString();
				int parameterIndex = s.indexOf(parameterName);
				if (parameterIndex != -1) {
					s = OdbString.replaceToken(s, parameterName, parameterValue, 1);
				}
			}
		}
		return s;
	}
}
