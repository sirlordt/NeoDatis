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

import java.math.BigInteger;

import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

/**
 * The main ODB public interface: It is what the user sees.
 * 
 * @author osmadja
 * 
 */
public interface ODB {

	/**
	 * Commit all the change of the database @
	 * */
	void commit();

	/**
	 * Undo all uncommitted changes
	 * */
	void rollback();

	/**
	 * Closes the database. Automatically commit uncommitted changes
	 * */
	void close();

	/**
	 * Store a plain java Object in the ODB Database
	 * 
	 * @param object
	 *            A plain Java Object
	 */
	OID store(Object object);

	/**
	 * Get all objects of a specific type
	 * 
	 * @param clazz
	 *            The type of the objects
	 * @return The list of objects
	 */
	<T> Objects<T> getObjects(Class clazz);

	/**
	 * Get all objects of a specific type
	 * 
	 * @param clazz
	 *            The type of the objects
	 * @param inMemory
	 *            if true, preload all objects,if false,load on demand
	 * @return The list of objects
	 */
	<T> Objects<T> getObjects(Class clazz, boolean inMemory);

	/**
	 * 
	 * @param clazz
	 *            The type of the objects
	 * @param inMemory
	 *            if true, preload all objects,if false,load on demand
	 * @param startIndex
	 *            The index of the first object
	 * @param endIndex
	 *            The index of the last object that must be returned
	 * @return A List of objects
	 */
	<T> Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex);

	/**
	 * Delete an object from database
	 * @param object
	 */
	OID delete(Object object);
	
	/**
	 * Delete an object and all its sub objects
	 * @param object
	 * @param cascade
	 * @return
	 */
	OID deleteCascade(Object object);

	/**
	 * Delete an object from the database with the id
	 * 
	 * @param oid
	 *            The object id to be deleted
	 */
	void deleteObjectWithId(OID oid);

	/**
	 * Search for objects that matches the query.
	 * 
	 * @param query
	 * @return The list of values
	 * 
	 */
	Values getValues(IValuesQuery query);

	/**
	 * Search for objects that matches the query.
	 * 
	 * @param query
	 * @return The list of objects
	 * 
	 */
	<T> Objects<T> getObjects(IQuery query);

	/**
	 * Search for objects that matches the native query.
	 * 
	 * @param query
	 * @param inMemory
	 * @return The list of objects
	 * 
	 */
	<T> Objects<T> getObjects(IQuery query, boolean inMemory);

	/**
	 * Return a list of objects that matches the query
	 * 
	 * @param query
	 * @param inMemory
	 *            if true, preload all objects,if false,load on demand
	 * @param startIndex
	 *            The index of the first object
	 * @param endIndex
	 *            The index of the last object that must be returned
	 * @return A List of objects, if start index and end index are -1, they are
	 *         ignored. If not, the length of the sublist is endIndex -
	 *         startIndex
	 * 
	 */
	<T> Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex);

	/**
	 * Returns the number of objects that satisfy the query
	 * 
	 * @param query
	 * @return The number of objects that satisfy the query
	 * 
	 */
	BigInteger count(CriteriaQuery query);

	/**
	 * Get the id of an ODB-aware object
	 * 
	 * @param object
	 * @return The ODB internal object id
	 */
	OID getObjectId(Object object);

	/**
	 * Get the object with a specific id *
	 * 
	 * @param id
	 * @return The object with the specific id @
	 */

	Object getObjectFromId(OID id);

	/**
	 * Defragment ODB Database
	 * 
	 * @param newFileName
	 * 
	 */
	void defragmentTo(String newFileName);

	/**
	 * Get an abstract representation of a class
	 * 
	 * @param clazz
	 * @return a public meta-representation of a class
	 * 
	 */
	ClassRepresentation getClassRepresentation(Class clazz);

	/**
	 * Get an abstract representation of a class
	 * 
	 * @param fullClassName
	 * @return a public meta-representation of a class
	 * 
	 */
	ClassRepresentation getClassRepresentation(String fullClassName);
	ClassRepresentation getClassRepresentation(String fullClassName, boolean laodClass);

	/**
	 * Used to add an update trigger callback for the specific class
	 * 
	 * @param trigger
	 */
	void addUpdateTrigger(Class clazz, UpdateTrigger trigger);

	/**
	 * Used to add an insert trigger callback for the specific class
	 * 
	 * @param trigger
	 */
	void addInsertTrigger(Class clazz, InsertTrigger trigger);

	/**
	 * USed to add a delete trigger callback for the specific class
	 * 
	 * @param trigger
	 */
	void addDeleteTrigger(Class clazz, DeleteTrigger trigger);

	/**
	 * Used to add a select trigger callback for the specific class
	 * 
	 * @param trigger
	 */
	void addSelectTrigger(Class clazz, SelectTrigger trigger);

	/** Returns the object used to refactor the database */
	IRefactorManager getRefactorManager();

	/** Get the extension of ODB to get access to advanced functions */
	ODBExt ext();

	/**@deprecated Reconnection is now automatic 
	 * 
	 * Used to reconnect an object to the current session */
	void reconnect(Object object);

	/**
	 * Used to disconnect the object from the current session. The object is
	 * removed from the cache
	 */
	void disconnect(Object object);

	/**
	 * @return
	 */
	boolean isClosed();
	
	CriteriaQuery criteriaQuery(Class clazz, ICriterion criterio);
	CriteriaQuery criteriaQuery(Class clazz);
	
	/**
	 * Return the name of the database
	 * @return the file name in local mode and the base id (alias) in client server mode.
	 */
	String getName();
}