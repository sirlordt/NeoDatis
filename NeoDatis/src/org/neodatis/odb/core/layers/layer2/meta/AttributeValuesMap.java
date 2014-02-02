
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
package org.neodatis.odb.core.layers.layer2.meta;

import org.neodatis.odb.OID;
import org.neodatis.tool.wrappers.map.OdbHashMap;


/** A Map to contain values of attributes of an object.
 * It is used to optimize a criteria query execution where ODB , while reading an instance data, tries to retrieve only values
 * of attributes involved in the query instead of reading the entire object.  
 * 
 * @author olivier s
 *
 */
public class AttributeValuesMap extends OdbHashMap<String,Object> {
	/** The Object Info Header of the object being represented*/
	private ObjectInfoHeader objectInfoHeader;
	
	/** The oid of the object. This is used when some criteria (example is equalCriterion) is on an object,
	 * in this case the comparison is done on the oid of the object and not on the object itself. 
	 */
	private OID oid; 

	
	public ObjectInfoHeader getObjectInfoHeader() {
		return objectInfoHeader;
	}

	public void setObjectInfoHeader(ObjectInfoHeader objectInfoHeader) {
		this.objectInfoHeader = objectInfoHeader;
	}

	public Object getAttributeValue(String attributeName){
		return get(attributeName);
	}
	
	public Comparable getComparable(String attributeName){
		return (Comparable) getAttributeValue(attributeName);
	}
	
	public boolean hasOid(){
		return oid!=null;
	}

	public OID getOid() {
		return oid;
	}

	public void setOid(OID oid) {
		this.oid = oid;
	}

}
