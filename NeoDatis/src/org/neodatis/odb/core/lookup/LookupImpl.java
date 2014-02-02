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
package org.neodatis.odb.core.lookup;

import java.util.Map;

import org.neodatis.tool.wrappers.map.OdbHashMap;

/** A simple class to enable direct object lookup by object id
 * @author olivier
 *
 */
public class LookupImpl implements ILookup {
	private Map<String, Object> objects;
	public LookupImpl(){
		objects = new OdbHashMap<String, Object>();
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.test.lookup.ILookup#get(java.lang.String)
	 */
	public Object get(String objectId){
		return objects.get(objectId);
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.test.lookup.ILookup#set(java.lang.String, java.lang.Object)
	 */
	public void set(String objectId, Object object){
		objects.put(objectId, object);
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.test.lookup.ILookup#size()
	 */
	public int size(){
		return objects.size();
	}
}
