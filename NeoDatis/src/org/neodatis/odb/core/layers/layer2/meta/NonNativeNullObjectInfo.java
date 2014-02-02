
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



/** To keep info about a non native null instance
 * 
 * @author olivier s
 *
 */
public class NonNativeNullObjectInfo  extends NonNativeObjectInfo{
    public NonNativeNullObjectInfo() {
        super(null);
    }

    public NonNativeNullObjectInfo(ClassInfo classInfo) {
		super(classInfo);
	}
	
	public String toString() {
		return "null non native object ";
	}
    public boolean hasChanged(AbstractObjectInfo aoi) {
        return aoi.getClass()!=NonNativeNullObjectInfo.class;
    }
	public boolean isNonNativeNullObject() {
		return true;
	}

    public boolean isNull() {
        return true;
    }
    
    
    
    
 
}
