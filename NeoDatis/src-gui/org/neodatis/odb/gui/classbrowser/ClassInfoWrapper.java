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
package org.neodatis.odb.gui.classbrowser;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.tool.wrappers.OdbClassUtil;

public class ClassInfoWrapper implements Comparable {
	private ClassInfo ci;

	public ClassInfoWrapper(ClassInfo ci) {
		this.ci = ci;
	}

	public String toString() {
		if (ci.isSystemClass()) {
			return "(ODB) - " + ci.getFullClassName() + " (" + ci.getNumberOfObjects() + " )";
		}
		return OdbClassUtil.getClassName(ci.getFullClassName()) + " (" + ci.getNumberOfObjects() + ")";
	}

	/**
	 * @return Returns the ci.
	 */
	public ClassInfo getCi() {
		return ci;
	}

	public int compareTo(Object o) {
		if (o == null || !(o instanceof ClassInfoWrapper)) {
			return -1000;
		}
		ClassInfoWrapper ciw = (ClassInfoWrapper) o;
		return ci.getFullClassName().compareTo(ciw.getCi().getFullClassName());
	}

}
