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
package org.neodatis.odb.core.layers.layer1.introspector;

import java.util.Map;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;


/**
 * Interface for ObjectInstropector. It has local and Client/Server implementation.
 * @author osmadja
 *
 */
public interface IObjectIntrospector {

	/**
	 * retrieve object data
	 * 
	 * @param object The object to get meta representation
	 * @param ci The ClassInfo 
	 * @param recursive To indicate that introspection must be recursive
	 * @param alreadyReadObjects A map with already read object, to avoid cyclic reference problem
	 * @return The object info
	 */
	public abstract AbstractObjectInfo getMetaRepresentation(Object object, ClassInfo ci, boolean recursive, Map<Object,NonNativeObjectInfo> alreadyReadObjects, IIntrospectionCallback callback);

	public abstract NonNativeObjectInfo buildNnoi(Object object, ClassInfo classInfo, AbstractObjectInfo[] values,
			long[] attributesIdentification, int[] attributeIds, Map<Object,NonNativeObjectInfo> alreadyReadObjects);

	public abstract void clear();
	public IClassIntrospector getClassIntrospector();
}