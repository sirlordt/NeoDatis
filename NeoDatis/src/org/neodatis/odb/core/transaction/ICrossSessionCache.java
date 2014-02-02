/**
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

Contributors: 	Marcelo Mayworm - initial API and implementation
 */
package org.neodatis.odb.core.transaction;

import org.neodatis.odb.OID;

/**
 * This interface define the control over objects alive across different
 * sessions. It is a wrapper for all objects put into cross cache. It's primary
 * purpose is to maintain references to the linked objects that has been used.
 * Different strategies can be used on the implementations to support the idea
 * of a cache based on weak reference.
 * 
 * @since 1.9
 */
public interface ICrossSessionCache {

	/**
	 * Associates the specified {@link OID} with the specified object(key) in
	 * this cache
	 * 
	 * @param object
	 *            The key. This parameter can not be <code> null </code>
	 * 
	 * @param {@link OID} The value. It is transformed to use weak reference for
	 *        the keys.
	 */
	public abstract void addObject(Object object, OID oid);

	/**
	 * Removes the mapping for this object from this cache if it is present.
	 * 
	 * 
	 * @param object
	 *            that contains the reference to {@link OID}. This parameter can not be <code> null </code>
	 */
	public abstract void removeObject(Object object);
	
	/**
	 * Mark the object with the oid as deleted.
	 * 
	 * 
	 * @param oid
	 *            that must be marked as deleted. 
	 *            
	 *            <pre>
	 *            When objects are deleted by oid, the cost is too high to search the object by the oid, so we just keep the deleted oid,
	 * and when looking for an object, check if the oid if is the deleted oids
	 *            
	 *            </pre>
	 */
	public abstract void removeOid(OID oid);

	/**
	 * Returns true if this cache maps one key to the specified object.
	 * 
	 * @param object
	 * @return boolean
	 */
	public abstract boolean existObject(Object object);

	/**
	 * Return the specific {@link OID}
	 * 
	 * @param object
	 *            The key on the cache for a {@link OID}. This parameter can not be <code> null </code>
	 * @return {@link OID}. Returns <code> null </code> in case no find key.
	 */
	public abstract OID getOid(Object object);

	/**
	 * Returns true if this map contains no key-value mappings.
	 * 
	 * @return boolean
	 */
	public abstract boolean isEmpty();

	/**
	 * Removes all mappings from this cache.
	 */
	public abstract void clear();

	/**
	 * Returns a String writing down the objects
	 * 
	 * @return String
	 */
	public abstract String toString();

	/**
	 * Returns the number of key-value mappings in this cache.
	 * 
	 * @return int The amount of objects on the cache
	 */
	public abstract int size();

}
