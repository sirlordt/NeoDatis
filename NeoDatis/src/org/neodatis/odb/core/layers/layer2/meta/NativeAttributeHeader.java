
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
package org.neodatis.odb.core.layers.layer2.meta;

/**
 * A class that contain basic information about a native object
 * @author osmadja
 *
 */
public class NativeAttributeHeader {
	private int blockSize;
	private byte blockType;
	private boolean isNative;
	private int odbTypeId;
	private boolean isNull;
	
	public NativeAttributeHeader() {
		super();
	}

	public NativeAttributeHeader(int blockSize, byte blockType, boolean isNative, int odbTypeId, boolean isNull) {
		super();
		this.blockSize = blockSize;
		this.blockType = blockType;
		this.isNative = isNative;
		this.odbTypeId = odbTypeId;
		this.isNull = isNull;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public byte getBlockType() {
		return blockType;
	}

	public void setBlockType(byte blockType) {
		this.blockType = blockType;
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public int getOdbTypeId() {
		return odbTypeId;
	}

	public void setOdbTypeId(int odbTypeId) {
		this.odbTypeId = odbTypeId;
	}

}
