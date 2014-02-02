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

import java.util.List;
import java.util.Map;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;

/**
 * @author olivier
 * An interface for all criteria
 *
 */
public interface ISingleCriterion extends ICriterion{
	/** Returns a list of attributes names that are involved in the query
	 * @return The attribute names*/
	List getAttributeNames();
	String getAttributeName();
	
	boolean match(AbstractObjectInfo aoi);
	boolean match(Map map);

}
