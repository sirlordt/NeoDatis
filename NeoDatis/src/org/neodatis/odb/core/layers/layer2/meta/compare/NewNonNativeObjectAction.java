
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
package org.neodatis.odb.core.layers.layer2.meta.compare;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;


/**
 * Used to store that a new Object was created when comparing to Objects.
 * @author osmadja
 *
 */
public class NewNonNativeObjectAction {
	private long updatePosition;
    private NonNativeObjectInfo nnoi;
	private int recursionLevel;
	private String attributeName;
	public NewNonNativeObjectAction(long position, NonNativeObjectInfo nnoi, int recursionLevel, String attributeName) {
		this.updatePosition = position;
		this.nnoi = nnoi;
		this.recursionLevel = recursionLevel;
		this.attributeName = attributeName;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("field ").append(attributeName).append(" - update reference position=").append(updatePosition).append(" - new nnoi=").append(nnoi).append(" - level=").append(recursionLevel);
		return buffer.toString();
	}

	public NonNativeObjectInfo getNnoi() {
		return nnoi;
	}

    public int getRecursionLevel() {
		return recursionLevel;
	}

	public long getUpdatePosition() {
		return updatePosition;
	}
}
