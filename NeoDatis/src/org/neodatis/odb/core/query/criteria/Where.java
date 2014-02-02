
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
import org.neodatis.odb.impl.core.query.criteria.CollectionSizeCriterion;
import org.neodatis.odb.impl.core.query.criteria.ContainsCriterion;
import org.neodatis.odb.impl.core.query.criteria.EqualCriterion;
import org.neodatis.odb.impl.core.query.criteria.IsNotNullCriterion;
import org.neodatis.odb.impl.core.query.criteria.IsNullCriterion;
import org.neodatis.odb.impl.core.query.criteria.LikeCriterion;

/**A simple factory to build all Criterion and Expression
 * 
 * @author olivier s
 *
 */
public class Where {
	Where(){}
	
	/********************************************************
	 * EQUALS
	 ********************************************************/
	
	/**
	 * @param attributeName The attribute name
	 * @param value The boolean value
	 * @return The criteria
	 * 
	 */
	public static ICriterion equal(String attributeName,boolean value){
		return new EqualCriterion(attributeName,value?Boolean.TRUE:Boolean.FALSE);
	}
    public static ICriterion equal(String attributeName,int value){
        return new EqualCriterion(attributeName,new Integer(value));
    }
    public static ICriterion equal(String attributeName,short value){
        return new EqualCriterion(attributeName,new Short(value));
    }
    public static ICriterion equal(String attributeName,byte value){
        return new EqualCriterion(attributeName,new Byte(value));
    }
    public static ICriterion equal(String attributeName,float value){
        return new EqualCriterion(attributeName, new Float(value));
    }
    public static ICriterion equal(String attributeName,double value){
        return new EqualCriterion(attributeName,new Double(value));
    }
    public static ICriterion equal(String attributeName,long value){
        return new EqualCriterion(attributeName,new Long(value));
    }
    public static ICriterion equal(String attributeName,char value){
        return new EqualCriterion(attributeName,new Character(value));
    }
    
    public static ICriterion equal(String attributeName,Object value){
		return new EqualCriterion(attributeName,value);
	}

    public static ICriterion iequal(String attributeName,char value){
        return new EqualCriterion(attributeName,new Character(value),false);
    }
    
    public static ICriterion iequal(String attributeName,Object value){
		return new EqualCriterion(attributeName,value,false);
	}

    /***********************************************************
     * LIKE
     * @param attributeName The attribute name 
     * @param value The string value
     * @return The criterio
     ***********************************************************/
    public static ICriterion like(String attributeName,String value){
        return new LikeCriterion(attributeName,value,true);
    }
    public static ICriterion ilike(String attributeName,String value){
        return new LikeCriterion(attributeName,value,false);
    }

    /***********************************************************
     * GREATER THAN
     * @param attributeName 
     * @param value 
     * @return The criterion
     ***********************************************************/
    public static ICriterion gt(String attributeName,Comparable value){
        return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_GT);
    }
    public static ICriterion gt(String attributeName,int value){
    	return new ComparisonCriterion(attributeName,new Integer(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }
    public static ICriterion gt(String attributeName,short value){
    	return new ComparisonCriterion(attributeName,new Short(value),ComparisonCriterion.COMPARISON_TYPE_GT);    	
    }
    public static ICriterion gt(String attributeName,byte value){
    	return new ComparisonCriterion(attributeName,new Byte(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }
    public static ICriterion gt(String attributeName,float value){
    	return new ComparisonCriterion(attributeName,new Float(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }
    public static ICriterion gt(String attributeName,double value){
    	return new ComparisonCriterion(attributeName,new Double(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }
    public static ICriterion gt(String attributeName,long value){
    	return new ComparisonCriterion(attributeName,new Long(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }

    public static ICriterion gt(String attributeName,char value){
    	return new ComparisonCriterion(attributeName,new Character(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }

    /***********************************************************
     * GREATER OR EQUAL
     * @param attributeName 
     * @param value 
     * @return The criterion
     ***********************************************************/

    public static ICriterion ge(String attributeName,Comparable value){
        return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_GE);
    }
    public static ICriterion ge(String attributeName,int value){
    	return new ComparisonCriterion(attributeName,new Integer(value),ComparisonCriterion.COMPARISON_TYPE_GE);
    }
    public static ICriterion ge(String attributeName,short value){
    	return new ComparisonCriterion(attributeName,new Short(value),ComparisonCriterion.COMPARISON_TYPE_GE);    	
    }
    public static ICriterion ge(String attributeName,byte value){
    	return new ComparisonCriterion(attributeName,new Byte(value),ComparisonCriterion.COMPARISON_TYPE_GE);
    }
    public static ICriterion ge(String attributeName,float value){
    	return new ComparisonCriterion(attributeName,new Float(value),ComparisonCriterion.COMPARISON_TYPE_GE);
    }
    public static ICriterion ge(String attributeName,double value){
    	return new ComparisonCriterion(attributeName,new Double(value),ComparisonCriterion.COMPARISON_TYPE_GE);
    }
    public static ICriterion ge(String attributeName,long value){
    	return new ComparisonCriterion(attributeName,new Long(value),ComparisonCriterion.COMPARISON_TYPE_GE);
    }
    
    public static ICriterion ge(String attributeName,char value){
    	return new ComparisonCriterion(attributeName,new Character(value),ComparisonCriterion.COMPARISON_TYPE_GT);
    }

    /***********************************************************
     * LESS THAN
     * @param attributeName 
     * @param value 
     * @return The criterion
     ***********************************************************/
    public static ICriterion lt(String attributeName,Comparable value){
        return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    public static ICriterion lt(String attributeName,int value){
    	return new ComparisonCriterion(attributeName,new Integer(value),ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    public static ICriterion lt(String attributeName,short value){
    	return new ComparisonCriterion(attributeName,new Short(value),ComparisonCriterion.COMPARISON_TYPE_LT);    	
    }
    public static ICriterion lt(String attributeName,byte value){
    	return new ComparisonCriterion(attributeName,new Byte(value),ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    public static ICriterion lt(String attributeName,float value){
    	return new ComparisonCriterion(attributeName,new Float(value),ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    public static ICriterion lt(String attributeName,double value){
    	return new ComparisonCriterion(attributeName,new Double(value),ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    public static ICriterion lt(String attributeName,long value){
    	return new ComparisonCriterion(attributeName,new Long(value),ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    public static ICriterion lt(String attributeName,char value){
    	return new ComparisonCriterion(attributeName,new Character(value),ComparisonCriterion.COMPARISON_TYPE_LT);
    }
    /***********************************************************
     * LESS OR EQUAL
     * @param attributeName The attribute name
     * @param value The value
     * @return The criterion
     ***********************************************************/

    public static ICriterion le(String attributeName,Comparable value){
        return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_LE);
    }
    public static ICriterion le(String attributeName,int value){
    	return new ComparisonCriterion(attributeName,new Integer(value),ComparisonCriterion.COMPARISON_TYPE_LE);
    }
    public static ICriterion le(String attributeName,short value){
    	return new ComparisonCriterion(attributeName,new Short(value),ComparisonCriterion.COMPARISON_TYPE_LE);    	
    }
    public static ICriterion le(String attributeName,byte value){
    	return new ComparisonCriterion(attributeName,new Byte(value),ComparisonCriterion.COMPARISON_TYPE_LE);
    }
    public static ICriterion le(String attributeName,float value){
    	return new ComparisonCriterion(attributeName,new Float(value),ComparisonCriterion.COMPARISON_TYPE_LE);
    }
    public static ICriterion le(String attributeName,double value){
    	return new ComparisonCriterion(attributeName,new Double(value),ComparisonCriterion.COMPARISON_TYPE_LE);
    }
    public static ICriterion le(String attributeName,long value){
    	return new ComparisonCriterion(attributeName,new Long(value),ComparisonCriterion.COMPARISON_TYPE_LE);
    }
    public static ICriterion le(String attributeName,char value){
    	return new ComparisonCriterion(attributeName,new Character(value),ComparisonCriterion.COMPARISON_TYPE_LE);
    }

    
    
    /***********************************************************
     * CONTAIN
     ***********************************************************/
    
    
    /**
     * The 
     * @param attributeName The attribute name
     * @param value The value
     * @return The criterion
     */
    public static ICriterion contain(String attributeName,boolean value){
    	return new ContainsCriterion(attributeName,value?Boolean.TRUE:Boolean.FALSE);
	}
	
    public static ICriterion contain(String attributeName,int value){
    	return new ContainsCriterion(attributeName,new Integer(value));
    }
    public static ICriterion contain(String attributeName,short value){
    	return new ContainsCriterion(attributeName,new Short(value));
    }
    public static ICriterion contain(String attributeName,byte value){
    	return new ContainsCriterion(attributeName,new Byte(value));
    }
    public static ICriterion contain(String attributeName,float value){
    	return new ContainsCriterion(attributeName, new Float(value));
    }
    public static ICriterion contain(String attributeName,double value){
    	return new ContainsCriterion(attributeName,new Double(value));
    }
    public static ICriterion contain(String attributeName,long value){
    	return new ContainsCriterion(attributeName,new Long(value));
    }
    public static ICriterion contain(String attributeName,char value){
    	return new ContainsCriterion(attributeName,new Character(value));
    }
    public static ICriterion contain(String attributeName,Object value){
		return new ContainsCriterion(attributeName,value);
    }

    public static ICriterion isNull(String attributeName){
    	return new IsNullCriterion(attributeName);
    }
    public static ICriterion isNotNull(String attributeName){
    	return new IsNotNullCriterion(attributeName);
    }
    
    public static ICriterion sizeEq(String attributeName,int size){
    	return new CollectionSizeCriterion(attributeName,size,CollectionSizeCriterion.SIZE_EQ);
    }
    public static ICriterion sizeNe(String attributeName,int size){
    	return new CollectionSizeCriterion(attributeName,size,CollectionSizeCriterion.SIZE_NE);
    }
    public static ICriterion sizeGt(String attributeName,int size){
    	return new CollectionSizeCriterion(attributeName,size,CollectionSizeCriterion.SIZE_GT);
    }
    public static ICriterion sizeGe(String attributeName,int size){
    	return new CollectionSizeCriterion(attributeName,size,CollectionSizeCriterion.SIZE_GE);
    }
    public static ICriterion sizeLt(String attributeName,int size){
    	return new CollectionSizeCriterion(attributeName,size,CollectionSizeCriterion.SIZE_LT);
    }
    public static ICriterion sizeLe(String attributeName,int size){
    	return new CollectionSizeCriterion(attributeName,size,CollectionSizeCriterion.SIZE_LE);
    }
    
    public static Or or(){
    	return new Or();
    }
    public static And and(){
    	return new And();
    }
    public static Not not(ICriterion criterion){
    	return new Not(criterion);
    }

    public static ICriterion get(String attributeName,Operator operator,Object value){
        if(operator == Operator.EQUAL){
            return new EqualCriterion(attributeName,value);
        }
        if(operator == Operator.LIKE){
            return new LikeCriterion(attributeName,value.toString(),true);
        }
        if(operator == Operator.GREATER_OR_EQUAL){
            return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_GE);
        }
        if(operator == Operator.GREATER_THAN){
            return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_GT);
        }
        if(operator == Operator.LESS_THAN){
        	return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_LT);
        }
        if(operator == Operator.LESS_OR_EQUAL){
        	return new ComparisonCriterion(attributeName,value,ComparisonCriterion.COMPARISON_TYPE_LE);
        }
        if(operator == Operator.CONTAIN){
            return new ContainsCriterion(attributeName,value);
        }

        throw new ODBRuntimeException(NeoDatisError.QUERY_UNKNOWN_OPERATOR.addParameter(operator.getName()));
    }
    

}
