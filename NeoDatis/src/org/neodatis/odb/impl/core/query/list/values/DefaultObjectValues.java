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
package org.neodatis.odb.impl.core.query.list.values;

import java.util.Iterator;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * 
 * @author osmadja
 *
 */
public class DefaultObjectValues implements ObjectValues {

	private Object [] valuesByIndex;
	/** key=alias,value=value*/
	private OdbHashMap<String, Object> valuesByAlias;
	
	public DefaultObjectValues(int size){
		valuesByIndex = new Object[size];
		valuesByAlias = new OdbHashMap<String, Object>();
	}
	
	public void set(int index, String alias, Object value){
		valuesByIndex[index] = value;
		valuesByAlias.put(alias, value);
	}
	public Object getByAlias(String alias) {
		Object o = valuesByAlias.get(alias);
		if(o==null && !valuesByAlias.containsKey(alias)){
			throw new ODBRuntimeException(NeoDatisError.VALUES_QUERY_ALIAS_DOES_NOT_EXIST.addParameter(alias).addParameter(valuesByAlias.keySet()));
		}
		return o;
	}

	public Object getByIndex(int index) {
		return valuesByIndex[index];
	}

	public Object []getValues() {
		return valuesByIndex;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		Iterator<String> aliases = valuesByAlias.keySet().iterator();
		String alias = null;
		Object object = null;
		while(aliases.hasNext()){
			alias = aliases.next();
			object = valuesByAlias.get(alias);
			buffer.append(alias).append("=").append(object).append(",");
		}
		
		return buffer.toString();
	}


}
