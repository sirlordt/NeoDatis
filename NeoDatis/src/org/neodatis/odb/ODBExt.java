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

/**
 * An interface to provider extended access to ODB.
 * 
 * @author osmadja
 * 
 */
public interface ODBExt {
	/**
	 * Gets the external OID of an Object. The external OID contains the ID of
	 * the database + the oid of the object. The External OID can be used to
	 * identify objects outside the ODB database as it should be unique across
	 * databases. It can be used for example to implement a replication process.
	 * 
	 * @param object
	 * @return
	 */
	ExternalOID getObjectExternalOID(Object object);

	/**
	 * Get the Database ID
	 * 
	 * @return
	 */
	DatabaseId getDatabaseId();

	/**
	 * Convert an OID to External OID
	 * 
	 * @param oid
	 * @return The external OID
	 */
	ExternalOID convertToExternalOID(OID oid);

	/**
	 * Gets the current transaction Id
	 * 
	 * @return The current transaction Id
	 */

	TransactionId getCurrentTransactionId();

	/**Returns the object version of the object that has the specified OID
	 * 
	 * @param oid
	 * @param useCache if false, force a disk read. else use the version that has already been loaded in the cache
	 * @return
	 */
	int getObjectVersion(OID oid, boolean useCache);

	/**
	 * Returns the object creation date in ms since 1/1/1970
	 * 
	 * @param oid
	 * @return The creation date
	 */
	public long getObjectCreationDate(OID oid);

	/**
	 * Returns the object last update date in ms since 1/1/1970
	 * 
	 * @param oid
	 * @param useCache if false, force a disk read. else use the date that has already been loaded in the cache
	 * @return The last update date
	 */
	public long getObjectUpdateDate(OID oid, boolean useCache);
	
	/** Replace the object with the specific OID by the object passed as a parameter
	 * 
	 * @param oid
	 * @param o
	 * @return
	 */
	public OID replace(OID oid, Object o);
}