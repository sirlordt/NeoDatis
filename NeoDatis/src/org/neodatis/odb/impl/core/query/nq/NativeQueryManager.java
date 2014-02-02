
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
package org.neodatis.odb.impl.core.query.nq;

import java.lang.reflect.Method;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.nq.NativeQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.tool.wrappers.OdbReflection;
import org.neodatis.tool.wrappers.map.OdbHashMap;


public class NativeQueryManager {
    
    private static String MATCH_METHOD_NAME = "match";
    
    private Map<IQuery,Method> methodsCache;
    
    public NativeQueryManager(){
    	methodsCache = new OdbHashMap<IQuery, Method>();
    }
    
    public String getFullClassName(IQuery query){
    	if(query instanceof NativeQuery){
    		return getFullClassName((NativeQuery) query);
    	}else{
    		return getFullClassName((SimpleNativeQuery) query);
    	}
    }
    protected String getFullClassName(NativeQuery query){
        return query.getObjectType().getName();
    }
  
    protected String getFullClassName(SimpleNativeQuery query){
        Class clazz = null;
        Method[] methods = OdbReflection.getMethods(query.getClass());
        
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];            
            Class[] attributes = OdbReflection.getAttributeTypes(method);
            if (method.getName().equals(MATCH_METHOD_NAME) && attributes.length==1){
                clazz = attributes[0];
                method.setAccessible(true);
                methodsCache.put(query,method);                    
                return clazz.getName();
            }
        }
        throw new ODBRuntimeException(NeoDatisError.QUERY_NQ_MATCH_METHOD_NOT_IMPLEMENTED.addParameter(query.getClass().getName()));
    }
    
    protected boolean match(IQuery query,Object object){
    	if(query instanceof NativeQuery){
    		return match((NativeQuery) query, object);
    	}else{
    		return match((SimpleNativeQuery) query,object);
    	}
    }

    protected boolean match(NativeQuery query,Object object){
        return query.match(object);
    }

    protected boolean match(SimpleNativeQuery query,Object object){
        Method method = methodsCache.get(query);

        Object[] params = {object};
        Object result;
        try {
            result = method.invoke(query, params);
        } catch (Exception e) {
            throw new ODBRuntimeException(NeoDatisError.QUERY_NQ_EXCEPTION_RAISED_BY_NATIVE_QUERY_EXECUTION.addParameter(query.getClass().getName()),e);
        }
        return ((Boolean)result).booleanValue();
    }

}
