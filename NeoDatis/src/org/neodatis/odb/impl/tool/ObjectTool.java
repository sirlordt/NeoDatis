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
package org.neodatis.odb.impl.tool;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.tool.wrappers.NeoDatisNumber;
import org.neodatis.tool.wrappers.OdbDateFormat;

/**
 * Basic native Object formatter. Used in ODBExplorer and XML import/export.
 * 
 * @author osmadja
 * 
 */
public class ObjectTool {

	public static OdbDateFormat format = new OdbDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

	public static int ID_CALLER_IS_ODB_EXPLORER = 1;
	public static int ID_CALLER_IS_XML = 2;
	public static int ID_CALLER_IS_SERIALIZER = 2;

	/**
	 * Convert a string representation to the right object
	 * 
	 * <pre>
	 *   If it is a representation of an int, it will return an Integer.
	 * 
	 * </pre>
	 * 
	 * @param odbTypeId
	 *            The native object type
	 * @param value
	 *            The real value
	 * @param caller
	 *            The caller type , can be one of the constants
	 *            ObjectTool.CALLER_IS_*
	 * @return The right object
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	public static Object stringToObject(int odbTypeId, String value, int caller) throws NumberFormatException, ParseException {

		Object theObject = null;
		if (value == null || value.equals("null")) {
			return new NullNativeObjectInfo(odbTypeId);
		}

		switch (odbTypeId) {
		case ODBType.NATIVE_BYTE_ID:
			theObject = new Byte(value);
			break;
		case ODBType.NATIVE_BOOLEAN_ID:
			theObject = value.equals("true") ? Boolean.TRUE : Boolean.FALSE;
			break;
		case ODBType.NATIVE_CHAR_ID:
			theObject = new Character(value.charAt(0));
			break;
		case ODBType.NATIVE_FLOAT_ID:
			theObject = new Float(value);
			break;
		case ODBType.NATIVE_DOUBLE_ID:
			theObject = new Double(value);
			break;
		case ODBType.NATIVE_INT_ID:
			theObject = new Integer(value);
			break;
		case ODBType.NATIVE_LONG_ID:
			theObject = new Long(value);
			break;
		case ODBType.NATIVE_SHORT_ID:
			theObject = new Short(value);
			break;
		case ODBType.BIG_DECIMAL_ID:
			theObject = NeoDatisNumber.createDecimalFromString(value);
			break;
		case ODBType.BIG_INTEGER_ID:
			theObject = NeoDatisNumber.createBigIntegerFromString(value);
			break;
		case ODBType.BOOLEAN_ID:
			theObject = value.equals("true") ? Boolean.TRUE : Boolean.FALSE;
			break;
		case ODBType.CHARACTER_ID:
			theObject = new Character(value.charAt(0));
			break;
		case ODBType.DATE_ID:
		case ODBType.DATE_SQL_ID:
		case ODBType.DATE_TIMESTAMP_ID:
			if (ObjectTool.callerIsOdbExplorer(caller)) {
				theObject = format.parse(value);
			}
			if (ObjectTool.callerIsXml(caller) || ObjectTool.callerIsSerializer(caller)) {
				theObject = new Date(Long.parseLong(value));
			}
			Date date = (Date) theObject;
			if (odbTypeId == ODBType.DATE_SQL_ID) {
				theObject = new java.sql.Date(date.getTime());
			}
			if (odbTypeId == ODBType.DATE_TIMESTAMP_ID) {
				theObject = new java.sql.Timestamp(date.getTime());
			}

			break;
		case ODBType.DATE_CALENDAR_ID:
		case ODBType.DATE_GREGORIAN_CALENDAR_ID:
			Date dateCalendar = null;
			if (ObjectTool.callerIsOdbExplorer(caller)) {
				dateCalendar = format.parse(value);
			}
			if (ObjectTool.callerIsXml(caller) || ObjectTool.callerIsSerializer(caller)) {
				dateCalendar = new Date(Long.parseLong(value));
			}
			Calendar c = Calendar.getInstance();
			c.setTime(dateCalendar);
			theObject = c;
			break;
		case ODBType.FLOAT_ID:
			theObject = new Float(value);
			break;
		case ODBType.DOUBLE_ID:
			theObject = new Double(value);
			break;
		case ODBType.INTEGER_ID:
			theObject = new Integer(value);
			break;
		case ODBType.LONG_ID:
			theObject = new Long(value);
			break;
		case ODBType.STRING_ID:
			theObject = value;
			break;
		case ODBType.OID_ID:
			theObject = OIDFactory.buildObjectOID(Long.parseLong(value));
			break;
		case ODBType.OBJECT_OID_ID:
			theObject = OIDFactory.buildObjectOID(Long.parseLong(value));
			break;
		case ODBType.CLASS_OID_ID:
			theObject = OIDFactory.buildClassOID(Long.parseLong(value));
			break;

		}
		if (theObject == null) {
			throw new ODBRuntimeException(NeoDatisError.NATIVE_TYPE_NOT_SUPPORTED.addParameter(ODBType.getNameFromId(odbTypeId)));
		}
		return theObject;
	}

	/**
	 * 
	 * @param odbTypeId
	 *            The native object type
	 * @param value
	 *            The real value
	 * @param caller
	 *            The caller type , can be one of the constants
	 *            ObjectTool.CALLER_IS_*
	 * 
	 * @param ci
	 *            The ClassInfo. It is only used for enum where we need the enum
	 *            class info. In other cases, it is null
	 * 
	 * @return The NativeObjectInfo that represents the specific value
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	public static NativeObjectInfo stringToObjectInfo(int odbTypeId, String value, int caller, ClassInfo ci) throws NumberFormatException, ParseException {

		if (ODBType.isAtomicNative(odbTypeId)) {
			Object theObject = stringToObject(odbTypeId, value, caller);
			return new AtomicNativeObjectInfo(theObject, odbTypeId);
		}

		if (ODBType.isEnum(odbTypeId)) {
			return new EnumNativeObjectInfo(ci, value);
		}

		return NullNativeObjectInfo.getInstance();
	}

	public static String atomicNativeObjectToString(AtomicNativeObjectInfo anoi, int caller) {
		if (anoi == null || anoi.isNull()) {
			return "null";
		}
		if (anoi.getObject() instanceof Date) {
			if (ObjectTool.callerIsOdbExplorer(caller)) {
				return format.format((Date) anoi.getObject());
			}
			return String.valueOf(((Date) anoi.getObject()).getTime());
		}
		return anoi.getObject().toString();
	}

	public static boolean callerIsOdbExplorer(int caller) {
		return caller == ID_CALLER_IS_ODB_EXPLORER;
	}

	public static boolean callerIsXml(int caller) {
		return caller == ID_CALLER_IS_XML;
	}

	public static boolean callerIsSerializer(int caller) {
		return caller == ID_CALLER_IS_SERIALIZER;
	}
}
