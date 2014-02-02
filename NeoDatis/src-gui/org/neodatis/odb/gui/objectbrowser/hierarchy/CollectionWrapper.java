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
package org.neodatis.odb.gui.objectbrowser.hierarchy;

import java.util.Collection;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public class CollectionWrapper implements Wrapper {
	private CollectionObjectInfo coi;
	private String name;
	private NonNativeObjectInfo parent;

	public CollectionWrapper(NonNativeObjectInfo nnoi, String name, CollectionObjectInfo collectionOI) {
		this.parent = nnoi;
		this.coi = collectionOI;
		this.name = name;
	}

	public String toString() {
		if (!coi.isNull()) {
			return "Collection [" + coi.getCollection().size() + "] : " + name;
		}
		return "NULL Collection";

	}

	public Collection getCollection() {
		return coi.getCollection();
	}

	public AbstractObjectInfo getObject() {
		return coi;
	}

	/**
	 * @return the parent
	 */
	public NonNativeObjectInfo getParent() {
		return parent;
	}

}
