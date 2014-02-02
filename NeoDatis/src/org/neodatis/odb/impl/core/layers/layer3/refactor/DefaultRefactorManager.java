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

package org.neodatis.odb.impl.core.layers.layer3.refactor;

import java.io.IOException;

import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;

public class DefaultRefactorManager implements IRefactorManager {
    protected IStorageEngine storageEngine;
    
    public DefaultRefactorManager(IStorageEngine storageEngine){
        this.storageEngine = storageEngine;
    }

    public void addField(String className, Class fieldType, String fieldName) {
    	 MetaModel metaModel = storageEngine.getSession(true).getMetaModel();
         ClassInfo ci = metaModel.getClassInfo(className,true);
         // The real attribute id (-1) will be set in the ci.addAttribute
         ClassAttributeInfo cai = new ClassAttributeInfo(-1,fieldName,fieldType.getName(),ci);
         ci.addAttribute(cai);
         storageEngine.getObjectWriter().updateClassInfo(ci, true);
    }

    public void changeFieldType(String className, String attributeName, Class newType) {
        // TODO Auto-generated method stub

    }

    public void removeClass(String className) {
        // TODO Auto-generated method stub

    }

    public void removeField(String className, String attributeName) throws IOException {
        MetaModel metaModel = storageEngine.getSession(true).getMetaModel();
        ClassInfo ci = metaModel.getClassInfo(className,true);
        ClassAttributeInfo cai2 = ci.getAttributeInfoFromName(attributeName);
        ci.removeAttribute(cai2);
        storageEngine.getObjectWriter().updateClassInfo(ci, true);
    }

    public void renameClass(String fullClassName, String newFullClassName) throws IOException {
        MetaModel metaModel = storageEngine.getSession(true).getMetaModel();
        ClassInfo ci = metaModel.getClassInfo(fullClassName,true);
        ci.setFullClassName(newFullClassName);
        storageEngine.getObjectWriter().updateClassInfo(ci, true);
    }

    public void renameField(String className, String attributeName, String newAttributeName) throws IOException {
        MetaModel metaModel = storageEngine.getSession(true).getMetaModel();
        ClassInfo ci = metaModel.getClassInfo(className,true);
        ClassAttributeInfo cai2 = ci.getAttributeInfoFromName(attributeName);
        cai2.setName(newAttributeName);
        storageEngine.getObjectWriter().updateClassInfo(ci, true);

    }

}
