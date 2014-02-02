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
package org.neodatis.odb;

import java.util.Collection;
import java.util.Iterator;

import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * The main interface of all query results of NeoDatis ODB. Objects interface
 * extends the Collection interface so it provides a standard collection
 * behavior.
 * @author osmadja
 * 
 */
public interface Objects<E> extends Collection<E> {
	/**
	 * Inform if the internal Iterator has more objects
	 * 
	 * @return
	 */
	public boolean hasNext();

	/**
	 * Returns the next object of the internal iterator of the collection
	 * 
	 * @return
	 */
	public E next();

	/**
	 * Return the first object of the collection, if exist
	 * 
	 * @return
	 */
	public E getFirst();

	/**
	 * Reset the internal iterator of the collection
	 */
	public void reset();

	/**
	 * Add an object into the collection using a specific ordering key
	 * 
	 * @param key
	 * @param object
	 *            The object can be an OID, can o NNOI (NonNativeObjectInfo) or
	 *            the object
	 * @return
	 */
	public boolean addWithKey(OdbComparable key, E o);
	
	/**
	 * Add an object into the collection using a specific ordering key
	 * 
	 * @param key
	 * @param object
	 * @return
	 */
	public boolean addWithKey(int key, E o);

	/**
	 * Returns the collection iterator throughout the order by
	 * {@link OrderByConstants}
	 * 
	 * @param orderByType
	 * @return
	 */
	public Iterator<E> iterator(OrderByConstants orderByType);
	
	/**
	 * Removes an object by its key
	 * 
	 * @param key
	 * @param value The object to be removed
	 * @return true if object has been removed
	 */
	public boolean removeByKey(OdbComparable key, Object value);
}
