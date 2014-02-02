
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

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.criteria.AbstractCriterion;
import org.neodatis.tool.wrappers.OdbReflection;


/**
 * A criterio to test collection or array size
 * @author olivier s
 *
 */
public class CollectionSizeCriterion extends AbstractCriterion {
	public static final int SIZE_EQ = 1;
	public static final int SIZE_NE = 2;
	public static final int SIZE_GT = 3;
	public static final int SIZE_GE = 4;
	public static final int SIZE_LT = 5;
	public static final int SIZE_LE = 6;
	
	// The size that the collection must have
	private int size;
	private int sizeType;
	public CollectionSizeCriterion(String attributeName,int size,int sizeType) {
		super(attributeName);
		this.size = size;
		this.sizeType = sizeType;
	}

	public boolean match(Object valueToMatch) {
		
		// If it is a AttributeValuesMap, then gets the real value from the map
		if(valueToMatch instanceof AttributeValuesMap){
			AttributeValuesMap attributeValues = (AttributeValuesMap) valueToMatch;
			valueToMatch = attributeValues.getAttributeValue(attributeName);
		}
		if(valueToMatch==null){
			// Null list are considered 0-sized list
			if(sizeType==SIZE_EQ && size==0){
				return true;
			}
			if( (sizeType==SIZE_LE && size>=0)||(sizeType==SIZE_LT && size>0)){
				return true;
			}
			if( sizeType==SIZE_NE && size!=0){
				return true;
			}
			return false;
		}
		if(valueToMatch instanceof Collection){
			Collection c = (Collection) valueToMatch;
			return matchSize(c.size(),size,sizeType);
		}
		Class clazz = valueToMatch.getClass();
		if(clazz.isArray()){
			int arrayLength = OdbReflection.getArrayLength(valueToMatch);
			return matchSize(arrayLength,size,sizeType);

		}
		throw new ODBRuntimeException(NeoDatisError.QUERY_BAD_CRITERIA.addParameter(valueToMatch.getClass().getName()));
	}
	private boolean matchSize(int collectionSize, int requestedSize, int sizeType ){
		switch (sizeType) {
		case SIZE_EQ:
			return collectionSize == requestedSize;
		case SIZE_NE:
			return collectionSize != requestedSize;
		case SIZE_GT:
			return collectionSize > requestedSize;
		case SIZE_GE:
			return collectionSize >= requestedSize;
		case SIZE_LT:
			return collectionSize < requestedSize;
		case SIZE_LE:
			return collectionSize <= requestedSize;
		}
		
		throw new ODBRuntimeException(NeoDatisError.QUERY_COLLECTION_SIZE_CRITERIA_NOT_SUPPORTED.addParameter(sizeType));
	}
	public AttributeValuesMap getValues() {
		return new AttributeValuesMap();
	}

	public void ready() {
	}

}
