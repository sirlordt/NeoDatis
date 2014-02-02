
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
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.criteria.AbstractCriterion;
import org.neodatis.tool.wrappers.OdbString;

public class LikeCriterion extends AbstractCriterion {
	private String criterionValue;

	private boolean isCaseSensitive;

	public LikeCriterion(String attributeName, String criterionValue, boolean isCaseSensiive) {
		super(attributeName);
		this.criterionValue = criterionValue;
		this.isCaseSensitive = isCaseSensiive;
	}

	public boolean match(Object valueToMatch) {
		String regExp = null;
		if(valueToMatch==null){
			return false;
		}
		// If it is a AttributeValuesMap, then gets the real value from the map
		if(valueToMatch instanceof AttributeValuesMap){
			AttributeValuesMap attributeValues = (AttributeValuesMap) valueToMatch;
			valueToMatch = attributeValues.get(attributeName);
		}

		if(valueToMatch==null){
			return false;
		}
	
		// Like operator only work with String
		if (!(valueToMatch instanceof String)) {
			throw new ODBRuntimeException(NeoDatisError.QUERY_ATTRIBUTE_TYPE_NOT_SUPPORTED_IN_LIKE_EXPRESSION.addParameter(valueToMatch.getClass().getName()));
		}
		String value = (String) valueToMatch;
		if (criterionValue.indexOf("%") != -1) {
			regExp = OdbString.replaceToken(criterionValue,"%", "(.)*");
			if(isCaseSensitive){
				return value != null && OdbString.matches(regExp,value);
			}
			return value != null && OdbString.matches(regExp.toLowerCase(), value.toLowerCase());			
		}
		if(isCaseSensitive){
			regExp = String.format("(.)*%s(.)*",criterionValue);
			return value != null &&  OdbString.matches(regExp, value); 
		}
		regExp = String.format("(.)*%s(.)*",criterionValue.toLowerCase());
		return value != null && OdbString.matches(regExp, value.toLowerCase());
	}
	public AttributeValuesMap getValues() {
		return new AttributeValuesMap();
	}

	public void ready() {
	}

}
