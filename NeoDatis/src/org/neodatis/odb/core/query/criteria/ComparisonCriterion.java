
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
package org.neodatis.odb.core.query.criteria;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.impl.core.layers.layer2.meta.compare.AttributeValueComparator;


/**A Criterion for greater than (gt),greater or equal(ge), less than (lt) and less or equal (le)
 * 
 * @author olivier s
 *
 */
public class ComparisonCriterion extends AbstractCriterion {
	public static final int COMPARISON_TYPE_GT = 1;

	public static final int COMPARISON_TYPE_GE = 2;

	public static final int COMPARISON_TYPE_LT = 3;

	public static final int COMPARISON_TYPE_LE = 4;

	private Object criterionValue;

	private int comparisonType;

	public ComparisonCriterion(String attributeName, String criterionValue, int comparisonType) {
		super(attributeName);
		this.criterionValue = criterionValue;
	}

	public ComparisonCriterion(String attributeName, int value, int comparisonType) {
		super(attributeName);
		init(new Integer(value), comparisonType);
	}

	public ComparisonCriterion(String attributeName, short value, int comparisonType) {
		super(attributeName);
		init(new Short(value), comparisonType);
	}

	public ComparisonCriterion(String attributeName, byte value, int comparisonType) {
		super(attributeName);
		init( new Byte(value), comparisonType);
	}

	public ComparisonCriterion(String attributeName, float value, int comparisonType) {
		super(attributeName);
		init(new Float(value), comparisonType);
	}

	public ComparisonCriterion(String attributeName, double value, int comparisonType) {
		super(attributeName);
		init( new Double(value), comparisonType);
	}

	public ComparisonCriterion(String attributeName, long value, int comparisonType) {
		super(attributeName);
		init(new Long(value), comparisonType);
	}

	public ComparisonCriterion(String attributeName, Object value, int comparisonType) {
		super(attributeName);
		init(value,comparisonType);
	}
	protected void init(Object value, int comparisonType){
		this.criterionValue = value;
		this.comparisonType = comparisonType;
	}

	public ComparisonCriterion(String attributeName, boolean value, int comparisonType) {
		super(attributeName);
		init(value ? Boolean.TRUE : Boolean.FALSE, comparisonType);
	}

	public boolean match(Object valueToMatch) {
		if (valueToMatch == null && criterionValue == null) {
			return true;
		}
		AttributeValuesMap attributeValues = null;
		
		// If it is a AttributeValuesMap, then gets the real value from the map 
		if(valueToMatch instanceof AttributeValuesMap){
			attributeValues = (AttributeValuesMap) valueToMatch;
			valueToMatch = attributeValues.getAttributeValue(attributeName);
		}

		if(valueToMatch==null){
			return false;
		}
		if (!(valueToMatch instanceof Comparable)) {
			throw new ODBRuntimeException(NeoDatisError.QUERY_COMPARABLE_CRITERIA_APPLIED_ON_NON_COMPARABLE.addParameter(valueToMatch.getClass().getName()));
		}
		Comparable comparable1 = (Comparable) valueToMatch;
		Comparable comparable2 = (Comparable) criterionValue;

		switch (comparisonType) {
		case COMPARISON_TYPE_GT:
			return valueToMatch != null && AttributeValueComparator.compare(comparable1,comparable2) > 0;
		case COMPARISON_TYPE_GE:
			return valueToMatch != null && AttributeValueComparator.compare(comparable1,comparable2) >= 0;
		case COMPARISON_TYPE_LT:
			return valueToMatch != null && AttributeValueComparator.compare(comparable1,comparable2) < 0;
		case COMPARISON_TYPE_LE:
			return valueToMatch != null && AttributeValueComparator.compare(comparable1,comparable2) <= 0;

		}

		throw new ODBRuntimeException(NeoDatisError.QUERY_UNKNOWN_OPERATOR.addParameter(comparisonType));

	}
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(attributeName).append(" ").append(getOperator()).append(" ").append(criterionValue);
		return buffer.toString();
	}

	private String getOperator(){
		switch (comparisonType) {
		case COMPARISON_TYPE_GT:
			return ">";
		case COMPARISON_TYPE_GE:
			return ">=";
		case COMPARISON_TYPE_LT:
			return "<";
		case COMPARISON_TYPE_LE:
			return "<=";

		}
		return "?";
	}

	public AttributeValuesMap getValues() {
		AttributeValuesMap map = new AttributeValuesMap();
		map.put(attributeName,criterionValue);
		return map;
	}

	public void setQuery(IQuery query) {
		super.setQuery(query);
		getQuery().setOptimizeObjectComparison(false);
	}
	public void ready() {
	}

}
