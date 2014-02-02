/*
 * Created on Oct 24, 2004
 *
 * neodatis 2004 - www.neodatis.com
 */

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

import java.io.Serializable;

import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * @author olivier
 * An interface for all criteria
 *
 */
public interface ICriterion extends Serializable{
	/** To check if an object matches this criterion
	 * @param object 
	 * @return true if object matches the criteria
	 * TODO create a math(AttributeValuesMap)
	 * 
	 * */ 
	boolean match(Object object);

	/** to be able to optimize query execution. Get only the field involved in the query instead of getting all the object
	 * @return All involved fields in criteria, List of String*/
	IOdbList<String> getAllInvolvedFields();
	AttributeValuesMap getValues();
	/** Gets thes whole query
	 * @return The owner query*/
	IQuery getQuery();
	void setQuery(IQuery query);
	
	boolean canUseIndex();

	/**
	 * a method to explicitly indicate that the criteria is ready. 
	 */
	void ready();
}
