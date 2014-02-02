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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.neodatis.odb.core.layers.layer2.instance.FullInstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.InstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.ParameterHelper;

/**
 * A class abstraction to give access to class level configuration like adding
 * an index, checking if index exists, rebuilding an index,...
 * 
 * @author osmadja
 * 
 */
public interface ClassRepresentation extends Serializable {

	/**
	 * @param name
	 *            The name of the index
	 * @param indexFields
	 *            The list of fields of the index
	 * @param verbose
	 *            A boolean value to indicate of ODB must describe what it is
	 *            doing
	 * @throws IOException
	 * @throws Exception
	 */
	public void addUniqueIndexOn(String name, String[] indexFields, boolean verbose);

	/**
	 * @param name
	 *            The name of the index
	 * @param indexFields
	 *            The list of fields of the index
	 * @param verbose
	 *            A boolean value to indicate of ODB must describe what it is
	 *            doing
	 * @param
	 * @throws IOException
	 * @throws Exception
	 */
	public void addIndexOn(String name, String[] indexFields, boolean verbose);

	/**
	 * Adds an helper to tell ODB how to create an instance when no default
	 * constructor is available
	 */
	public void addParameterHelper(ParameterHelper parameterHelper);

	/** Remove the parameter helper for this class, if exists */
	public void removeParameterHelper();

	/** Adds an helper to tell ODB what class to call to create an instance */
	public void addInstantiationHelper(InstantiationHelper instantiationHelper);

	/** Remove the instantiation helper of this class, if exists */
	public void removeInstantiationHelper();

	/** Adds an helper to tell ODB what class to call to create an instance */
	public void addFullInstantiationHelper(FullInstantiationHelper instantiationHelper);

	/** Remove the instantiation helper of this class, if exists */
	public void removeFullInstantiationHelper();

	/**
	 * To check if an index exist
	 * 
	 * @param indexName
	 * @return
	 */
	public boolean existIndex(String indexName);

	/**
	 * 
	 * @param className
	 * @param indexName
	 * @param verbose
	 */
	public void rebuildIndex(String indexName, boolean verbose);

	/**
	 * 
	 * @param className
	 * @param indexName
	 * @param verbose
	 */
	public void deleteIndex(String indexName, boolean verbose);

	/**
	 * 
	 */
	public List<String> getIndexDescriptions();
	
	public void persistAttribute(String attributeName);
	public void doNotPersistAttribute(String attributeName);
}
