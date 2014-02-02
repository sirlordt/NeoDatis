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

package org.neodatis.odb.core.layers.layer2.instance;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;


/**
 * In some cases, classes does not have empty constructors or constructors that can be called with null parameters. So when NeoDatis cannot create empty instances by Reflection,
 * the user can help NeoDatis registering a full instantiation helper. This Full instantiation Helper will receive the data of the object in a NonNativeObjectInfo (which is a meta representation of an java insatnce that contain all data)
 * The user then needs to get the data from the nnoi (NonNativeObjectInfo) and build the instance to return to NeoDatis 
 * @author olivier
 *
 */
public interface FullInstantiationHelper {
	
	
	/**
	 * 
	 * @param nnoi The NonNativeObjectInfo that contain all the data of the object that must be built
	 * @return The java instance
	 */
	Object instantiate(final NonNativeObjectInfo nnoi);
}
