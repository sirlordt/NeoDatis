
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

import java.io.Serializable;

import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**To keep track of differences between two ClassInfo. Ussed by the MetaModel compatibility checker
 * 
 * @author osmadja
 *
 */
public class ClassInfoCompareResult implements Serializable{
    private String fullClassName;

    private IOdbList<String> incompatibleChanges;

    private IOdbList<String> compatibleChanges;

    public ClassInfoCompareResult(String fullClassName) {
        this.fullClassName = fullClassName;
        incompatibleChanges = new OdbArrayList<String>(5);
        compatibleChanges = new OdbArrayList<String>(5);
    }

    /**
     * @return the compatibleChanges
     */
    public IOdbList<String> getCompatibleChanges() {
        return compatibleChanges;
    }

    /**
     * @param compatibleChanges
     *            the compatibleChanges to set
     */
    public void setCompatibleChanges(IOdbList<String> compatibleChanges) {
        this.compatibleChanges = compatibleChanges;
    }

    /**
     * @return the incompatibleChanges
     */
    public IOdbList<String> getIncompatibleChanges() {
        return incompatibleChanges;
    }

    /**
     * @param incompatibleChanges
     *            the incompatibleChanges to set
     */
    public void setIncompatibleChanges(IOdbList<String> incompatibleChanges) {
        this.incompatibleChanges = incompatibleChanges;
    }

    /**
     * @return the isCompatible
     */
    public boolean isCompatible() {
        return incompatibleChanges.isEmpty();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(fullClassName).append(" is Compatible = ").append(isCompatible()).append("\n");
        buffer.append("Incompatible changes = ").append(incompatibleChanges);
        buffer.append("\nCompatible changes = ").append(compatibleChanges);

        return buffer.toString();
    }

    public void addCompatibleChange(String o) {
        compatibleChanges.add(o);
    }

    public void addIncompatibleChange(String o) {
        incompatibleChanges.add(o);
    }

    public boolean hasCompatibleChanges() {
        return !compatibleChanges.isEmpty();
    }

    /**
     * @return the fullClassName
     */
    public String getFullClassName() {
        return fullClassName;
    }

}
