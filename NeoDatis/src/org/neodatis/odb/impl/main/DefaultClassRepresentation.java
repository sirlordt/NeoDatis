
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
package org.neodatis.odb.impl.main;

import java.util.List;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer2.instance.FullInstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.InstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.ParameterHelper;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoHelper;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;

public class DefaultClassRepresentation implements ClassRepresentation{
	private IStorageEngine storageEngine;
	private ClassInfo classInfo;
	private IClassIntrospector classIntrospector;
	
	public DefaultClassRepresentation(IStorageEngine storageEngine, ClassInfo classInfo) {
		super();
		this.storageEngine = storageEngine;
		this.classInfo = classInfo;
		this.classIntrospector = OdbConfiguration.getCoreProvider().getClassIntrospector();;
	}

	public void addUniqueIndexOn(String name, String[] indexFields, boolean verbose)  {
		storageEngine.addIndexOn(classInfo.getFullClassName(), name, indexFields, verbose, false);
	}
	
	public void addIndexOn(String name, String[] indexFields, boolean verbose)  {
		storageEngine.addIndexOn(classInfo.getFullClassName(), name, indexFields, verbose, true);
	}

	public void addInstantiationHelper(InstantiationHelper instantiationHelper) {
		classIntrospector.addInstantiationHelper(classInfo.getFullClassName(), instantiationHelper);
	}

	public void addFullInstantiationHelper(FullInstantiationHelper instantiationHelper) {
		classIntrospector.addFullInstantiationHelper(classInfo.getFullClassName(), instantiationHelper);
	}

	public void addParameterHelper(ParameterHelper parameterHelper) {
		classIntrospector.addParameterHelper(classInfo.getFullClassName(), parameterHelper);
	}

	public void removeInstantiationHelper() {
		classIntrospector.removeInstantiationHelper(classInfo.getFullClassName());		
	}

	public void removeFullInstantiationHelper() {
		classIntrospector.removeInstantiationHelper(classInfo.getFullClassName());		
	}

	public void removeParameterHelper() {
		classIntrospector.removeParameterHelper(classInfo.getFullClassName());
	}

	public boolean existIndex(String indexName) {
		return classInfo.hasIndex(indexName);
	}
	/** Used to rebuild an index*/
	public void rebuildIndex( String indexName, boolean verbose){
		storageEngine.rebuildIndex(classInfo.getFullClassName(), indexName, verbose);
	}
	/**
	 * 
	 */
	public void deleteIndex( String indexName, boolean verbose){
		storageEngine.deleteIndex(classInfo.getFullClassName(), indexName, verbose);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.ClassRepresentation#getIndexesDescription()
	 */
	public List<String> getIndexDescriptions() {
		return ClassInfoHelper.getIndexDescriptions(classInfo);
	}

	public void doNotPersistAttribute(String attributeName) {
		classIntrospector.persistFieldOfClass(classInfo.getFullClassName(), attributeName, false);
	}

	public void persistAttribute(String attributeName) {
		classIntrospector.persistFieldOfClass(classInfo.getFullClassName(), attributeName, true);		
	}
	
	
}
