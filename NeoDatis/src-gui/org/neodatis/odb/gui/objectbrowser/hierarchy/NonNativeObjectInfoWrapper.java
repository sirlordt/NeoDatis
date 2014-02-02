
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

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeDeletedObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public class NonNativeObjectInfoWrapper implements Wrapper {
	private NonNativeObjectInfo nnoi;
	private String name;
	private boolean isDeleted;
	private boolean isUpdated;

	public NonNativeObjectInfoWrapper(String name, NonNativeObjectInfo nnai) {
		this.nnoi = nnai;
		this.name = name;
		this.isDeleted = false;
		this.isUpdated = false;
	}

	public NonNativeObjectInfoWrapper(NonNativeObjectInfo nnai) {
		this.nnoi = nnai;
	}

	public String toString() {
		if (nnoi instanceof NonNativeNullObjectInfo) {
			return name == null ? "?" : name + "=null";
		}
		if (isDeleted) {
			return name == null ? "<>" : name + "(deleted)";
		}
		if (isUpdated) {
			return name == null ? "<>" : name + "(updated)";
		}
		if(name==null){
			return "<>";
		}
		if(name.indexOf("oid")!=-1){
			return name; 
		}
		if(nnoi.isDeletedObject()){
			NonNativeDeletedObjectInfo nd = (NonNativeDeletedObjectInfo) nnoi;
			return String.format("%s (oid=%s) - deleted object", name, nnoi.getOid().toString());
		}
		return String.format("%s (oid=%s)", name, nnoi.getOid().toString());
	}

	/**
	 * @return Returns the nnoi.
	 */
	public NonNativeObjectInfo getNnoi() {
		return nnoi;
	}

	/**
	 * @param isDeleted
	 *            The isDeleted to set.
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @param isUpdated
	 *            The isUpdated to set.
	 */
	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	/**
	 * @return Returns the isDeleted.
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @return Returns the isUpdated.
	 */
	public boolean isUpdated() {
		return isUpdated;
	}

	public AbstractObjectInfo getObject() {
		return nnoi;
	}

}
