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
package org.neodatis.odb.core.query;

import org.neodatis.tool.wrappers.OdbComparable;


/**
 * A composed key : an object that contains various values used for indexing query result
 * <p>
 * This is an implementation that allows compare keys to contain more than one single value to be compared
 * </p>
 *
 */
public class ComposedCompareKey extends CompareKey{
    private Comparable [] keys;
    
    public ComposedCompareKey(OdbComparable [] keys){
        this.keys = keys;
    }

    public int compareTo(Object o) {
        if(o==null || o.getClass()!=ComposedCompareKey.class){
            return -1;
        }
        ComposedCompareKey ckey = (ComposedCompareKey) o;
        int result = 0;
        for(int i=0;i<keys.length;i++){
            result = keys[i].compareTo(ckey.keys[i]);
            if(result!=0){
                return result;
            }
        }
        return 0;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	if(keys==null){
    		return "no keys defined";
    	}
    	StringBuffer buffer  = new StringBuffer();
    	for(int i=0;i<keys.length;i++){
    		if(i!=0){
    			buffer.append("|");
    		}
    		buffer.append(keys[i]);
    	}
    	return buffer.toString();
    }
    

}
