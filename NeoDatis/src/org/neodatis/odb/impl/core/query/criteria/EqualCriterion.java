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
package org.neodatis.odb.impl.core.query.criteria;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.criteria.AbstractCriterion;
import org.neodatis.odb.impl.core.layers.layer2.meta.compare.AttributeValueComparator;
import org.neodatis.tool.wrappers.OdbString;

/**
 * A criterion to match equality
 * 
 * @author olivier s
 * 
 */

public class EqualCriterion extends AbstractCriterion {
	private Object criterionValue;
	private boolean isCaseSensitive;

	/**
	 * For criteria query on objects, we use the oid of the object instead of
	 * the object itself. So comparison will be done with OID It is faster and
	 * avoid the need of the object (class) having to implement Serializable in
	 * client server mode
	 * 
	 */
	private OID oid;
	private boolean objectIsNative;

	public EqualCriterion(String attributeName, int value) {
		super(attributeName);
		init(new Integer(value));
	}

	public EqualCriterion(String attributeName, short value) {
		super(attributeName);
		init(new Short(value));
	}

	public EqualCriterion(String attributeName, byte value) {
		super(attributeName);
		init(new Byte(value));
	}

	public EqualCriterion(String attributeName, float value) {
		super(attributeName);
		init(new Float(value));
	}

	public EqualCriterion(String attributeName, double value) {
		super(attributeName);
		init(new Double(value));
	}

	public EqualCriterion(String attributeName, long value) {
		super(attributeName);
		init(new Long(value));
	}

	/**
	 * 
	 * @param attributeName
	 * @param value
	 */
	public EqualCriterion(String attributeName, Object value) {
		super(attributeName);
		init(value);
	}

	protected void init(Object value) {
		criterionValue = value;
		isCaseSensitive = true;
		if (criterionValue == null) {
			this.objectIsNative = true;
		} else {
			this.objectIsNative = ODBType.isNative(criterionValue.getClass());
		}
		
		if(criterionValue!=null && criterionValue.getClass().isEnum()){
			Enum enumObject = (Enum) criterionValue;
			criterionValue = enumObject.name();
			objectIsNative = true;
		}
	}

	/**
	 * 
	 * @param attributeName
	 * @param value
	 * @param isCaseSensitive
	 */
	public EqualCriterion(String attributeName, Object value, boolean isCaseSensitive) {
		super(attributeName);
		this.criterionValue = value;
		this.isCaseSensitive = isCaseSensitive;
	}

	public EqualCriterion(String attributeName, String value, boolean isCaseSensitive) {
		super(attributeName);
		this.criterionValue = value;
		this.isCaseSensitive = isCaseSensitive;
	}

	public EqualCriterion(String attributeName, boolean value) {
		super(attributeName);
		init(value ? Boolean.TRUE : Boolean.FALSE);
	}

	public boolean match(Object valueToMatch) {
		// If it is a AttributeValuesMap, then gets the real value from the map
		// AttributeValuesMap is used to optimize Criteria Query
		// (reading only values of the object that the query needs to be
		// evaluated instead of reading the entire object)
		if (valueToMatch instanceof AttributeValuesMap) {
			AttributeValuesMap attributeValues = (AttributeValuesMap) valueToMatch;
			valueToMatch = attributeValues.getAttributeValue(attributeName);
		}

		if (valueToMatch == null && criterionValue == null && oid == null) {
			return true;
		}
		// if case sensitive (default value), just call the equals on the
		// objects
		if (isCaseSensitive) {
			if (objectIsNative) {
				return valueToMatch != null && AttributeValueComparator.equals(valueToMatch, criterionValue);
			}

			OID objectOid = null;
			
			
			try{
				objectOid = (OID) valueToMatch;
			}catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.EQUAL_CRITERIA_ON_OBJECT_WITH_WRONG_ATTRIBUTE_TYPE
						.addParameter(this.toString())
						.addParameter(attributeName)
						.addParameter(valueToMatch.getClass().getName())
						.addParameter(OdbString.exceptionToString(e, true)));
			}
			if (oid == null) {
				// TODO Should we return false or thrown exception?
				// See junit TestCriteriaQuery6.test1
				return false;
				// throw new
				// ODBRuntimeException(NeoDatisError.CRITERIA_QUERY_ON_UNKNOWN_OBJECT);
			}
			return oid.equals(objectOid);

			// && valueToMatch.equals(criterionValue);
		}
		// Case insensitive (iequal) only works on String or Character!
		boolean canUseCaseInsensitive = (criterionValue.getClass() == String.class && valueToMatch.getClass() == String.class)
				|| (criterionValue.getClass() == Character.class && valueToMatch.getClass() == Character.class);

		if (!canUseCaseInsensitive) {
			throw new ODBRuntimeException(NeoDatisError.QUERY_ATTRIBUTE_TYPE_NOT_SUPPORTED_IN_IEQUAL_EXPRESSION.addParameter(valueToMatch.getClass().getName()));
		}
		// Cast to string to make the right comparison using the
		// equalsIgnoreCase
		String s1 = (String) valueToMatch;
		String s2 = (String) criterionValue;
		return OdbString.equalsIgnoreCase(s1, s2);

	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if(oid!=null){
			buffer.append(attributeName).append(" = (Object Id ").append(oid).append(")");
		}else{
			buffer.append(attributeName).append(" = ").append(criterionValue);
		}
		return buffer.toString();
	}

	public AttributeValuesMap getValues() {
		AttributeValuesMap map = new AttributeValuesMap();
		if (criterionValue == null && oid != null) {
			map.setOid(oid);
		} else {
			map.put(attributeName, criterionValue);
		}
		return map;
	}

	public boolean canUseIndex() {
		return true;
	}

	public void ready() {
		if (!objectIsNative) {
			if (getQuery() == null) {
				throw new ODBRuntimeException(NeoDatisError.CONTAINS_QUERY_WITH_NO_QUERY);
			}
			IStorageEngine engine = getQuery().getStorageEngine();
			if (engine == null) {
				throw new ODBRuntimeException(NeoDatisError.CONTAINS_QUERY_WITH_NO_STORAGE_ENGINE);
			}
			// For non native object, we just need the oid of it
			oid = engine.getObjectId(criterionValue, false);
			this.criterionValue = null;
		}
	}
}
