
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
package org.neodatis.odb.impl.core.layers.layer3.block;

/**All Block Types of the ODB database format.
 * 
 * @author osmadja
 *
 */
public class BlockTypes {

    public final static byte BLOCK_TYPE_CLASS_HEADER = 1;

    public final static byte BLOCK_TYPE_CLASS_BODY = 2;

    public final static byte BLOCK_TYPE_NATIVE_OBJECT = 3;

    public final static byte BLOCK_TYPE_NON_NATIVE_OBJECT = 4;

    public final static byte BLOCK_TYPE_POINTER = 5;

    public final static byte BLOCK_TYPE_DELETED = 6;

    public final static byte BLOCK_TYPE_NON_NATIVE_NULL_OBJECT = 7;
    public final static byte BLOCK_TYPE_NATIVE_NULL_OBJECT = 77;

    public final static byte BLOCK_TYPE_COLLECTION_OBJECT = 8;

    public final static byte BLOCK_TYPE_ARRAY_OBJECT = 9;

    public final static byte BLOCK_TYPE_MAP_OBJECT = 10;
    
    public final static byte BLOCK_TYPE_IDS = 20;

	static public final byte BLOCK_TYPE_INDEX = 21;

    public static boolean isClassHeader(int blockType) {
        return blockType == BLOCK_TYPE_CLASS_HEADER;
    }

    public static boolean isClassBody(int blockType) {
        return blockType == BLOCK_TYPE_CLASS_BODY;
    }

    public static boolean isPointer(int blockType) {
        return blockType == BLOCK_TYPE_POINTER;
    }

    public static boolean isNullNativeObject(int blockType) {
        return blockType == BLOCK_TYPE_NATIVE_NULL_OBJECT;
    }
    public static boolean isNullNonNativeObject(int blockType) {
        return blockType == BLOCK_TYPE_NON_NATIVE_NULL_OBJECT;
    }

    public static boolean isDeletedObject(int blockType) {
        return blockType == BLOCK_TYPE_DELETED;
    }

    public static boolean isNative(int blockType) {
        return blockType == BLOCK_TYPE_ARRAY_OBJECT || blockType == BLOCK_TYPE_COLLECTION_OBJECT || blockType == BLOCK_TYPE_MAP_OBJECT || blockType == BLOCK_TYPE_NATIVE_OBJECT;
    }

    public static boolean isNonNative(int blockType) {
        return blockType == BLOCK_TYPE_NON_NATIVE_OBJECT;
    }

    public static boolean isNull(byte blockType) {
        return blockType == BLOCK_TYPE_NATIVE_NULL_OBJECT || blockType == BLOCK_TYPE_NON_NATIVE_NULL_OBJECT;
    }

	public static boolean isIndex(byte blockType) {
		return blockType == BLOCK_TYPE_INDEX;
	}
}
