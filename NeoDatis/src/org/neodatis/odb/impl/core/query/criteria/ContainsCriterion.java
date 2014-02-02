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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.AbstractCriterion;
import org.neodatis.tool.wrappers.OdbReflection;

public class ContainsCriterion extends AbstractCriterion {
	private Object criterionValue;
	/**
	 * For criteria query on objects, we use the oid of the object instead of
	 * the object itself. So comparison will be done with OID It is faster and
	 * avoid the need of the object (class) having to implement Serializable in
	 * client server mode
	 * 
	 */
	private OID oid;
	private boolean objectIsNative;

	public ContainsCriterion(String attributeName, String criterionValue) {
		super(attributeName);
		init(criterionValue);
	}

	public ContainsCriterion(String attributeName, int value) {
		super(attributeName);
		init(new Integer(value));
	}

	public ContainsCriterion(String attributeName, short value) {
		super(attributeName);
		init(new Short(value));
	}

	public ContainsCriterion(String attributeName, byte value) {
		super(attributeName);
		init(new Byte(value));
	}

	public ContainsCriterion(String attributeName, float value) {
		super(attributeName);
		init(new Float(value));
	}

	public ContainsCriterion(String attributeName, double value) {
		super(attributeName);
		init(new Double(value));
	}

	public ContainsCriterion(String attributeName, long value) {
		super(attributeName);
		init(new Long(value));
	}

	protected void init(Object value) {
		this.criterionValue = value;
		if (criterionValue == null) {
			this.objectIsNative = true;
		} else {
			this.objectIsNative = ODBType.isNative(criterionValue.getClass());
		}

	}

	public ContainsCriterion(String attributeName, Object value) {
		super(attributeName);
		init(value);
	}

	public ContainsCriterion(String attributeName, boolean value) {
		super(attributeName);
		init(value ? Boolean.TRUE : Boolean.FALSE);
	}

	public boolean match(Object valueToMatch) {

		if (valueToMatch == null && criterionValue == null && oid == null) {
			return true;
		}
		if (valueToMatch == null) {
			return false;
		}
		Map m = null;

		if (valueToMatch instanceof Map) {
			// The value in the map, just take the object with the attributeName
			m = (Map) valueToMatch;
			valueToMatch = m.get(attributeName);
			// The value valueToMatch was redefined, so we need to re-make some
			// tests
			if (valueToMatch == null && criterionValue == null && oid == null) {
				return true;
			}
			if (valueToMatch == null) {
				return false;
			}
		}
		if (valueToMatch instanceof Collection) {
			Collection c = (Collection) valueToMatch;
			return checkIfCollectionContainsValue(c);
		}

		Class clazz = valueToMatch.getClass();

		if (clazz.isArray()) {
			return checkIfArrayContainsValue(valueToMatch);
		}
		throw new ODBRuntimeException(NeoDatisError.QUERY_CONTAINS_CRITERION_TYPE_NOT_SUPPORTED.addParameter(valueToMatch.getClass().getName()));

	}

	private boolean checkIfCollectionContainsValue(Collection c) {

		IStorageEngine engine = getQuery().getStorageEngine();
		if (engine == null) {
			throw new ODBRuntimeException(NeoDatisError.QUERY_ENGINE_NOT_SET);
		}
		AbstractObjectInfo aoi = null;
		Iterator iterator = c.iterator();

		// If the object to compared is native
		if (objectIsNative) {
			while (iterator.hasNext()) {
				aoi = (AbstractObjectInfo) iterator.next();
				if (aoi == null && criterionValue == null) {
					return true;
				}
				if (aoi != null && criterionValue == null) {
					return false;
				}
				if (criterionValue.equals(aoi.getObject())) {
					return true;
				}
			}
			return false;
		}
		// Object is not native

		while (iterator.hasNext()) {
			aoi = (AbstractObjectInfo) iterator.next();
			if (aoi.isNull() && criterionValue == null && oid ==null) {
				return true;
			}
			if (aoi != null & oid !=null) {
				if (aoi.isNonNativeObject()) {
					NonNativeObjectInfo nnoi1 = (NonNativeObjectInfo) aoi;
					boolean isEqual = nnoi1.getOid() != null && oid != null && nnoi1.getOid().equals(oid);
					if (isEqual) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkIfArrayContainsValue(Object valueToMatch) {
		int arrayLength = OdbReflection.getArrayLength(valueToMatch);
		Object element = null;
		AbstractObjectInfo aoi = null;
		for (int i = 0; i < arrayLength; i++) {
			element = OdbReflection.getArrayElement(valueToMatch, i);
			if (element == null && criterionValue == null) {
				return true;
			}
			aoi = (AbstractObjectInfo) element;
			if (aoi != null && aoi.getObject() != null && aoi.getObject().equals(criterionValue)) {
				return true;
			}
		}
		return false;
	}

	public AttributeValuesMap getValues() {
		return new AttributeValuesMap();
	}

	public void setQuery(IQuery query) {
		super.setQuery(query);
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
