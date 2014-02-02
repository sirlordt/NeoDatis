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
package org.neodatis.odb.impl.core.layers.layer1.introspector;


/**
 * The ClassIntrospector is used to introspect classes. It uses Reflection to
 * extract class information. It transforms a native Class into a ClassInfo (a
 * meta representation of the class) that contains all informations about the
 * class.
 * 
 * @author osmadja
 * 
 */
public class AndroidClassIntrospector extends AbstractClassIntrospector {
	
	public AndroidClassIntrospector() {
		super();
	}
	/**
	 * NeoDatis uses sun classes to create dynamic empty constructors so it does not work on Android
	 * TODO check how to do this on Android
	 * and stores it the constructor cache.
	 * 
	 * @param clazz
	 * @return
	 */
	protected boolean tryToCreateAnEmptyConstructor(Class clazz) {
		return false;
	}
}
