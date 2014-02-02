
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

import org.neodatis.tool.wrappers.OdbClassUtil;

/**
 * to keep informations about an attribute of a class :
 * 
 * <pre>
 *   - Its type
 *   - its name
 *   - If it is an index
 * </pre>
 * 
 * @author olivier s
 * 
 */
public class ClassAttributeInfo implements Serializable{

	private int id;
	private ClassInfo classInfo;

	private String className;

	private String packageName;

	private String name;

	private boolean isIndex;
	
	private String fullClassName;
	
	private ODBType attributeType;
	/** can be null*/
	private transient Class nativeClass;

	public ClassAttributeInfo() {
	}
	public ClassAttributeInfo(int attributeId , String name, String fullClassName, ClassInfo info) {
		this(attributeId,name,null,fullClassName,info);
	}

	public ClassAttributeInfo(int attributeId , String name, Class nativeClass, String fullClassName, ClassInfo info) {
		super();
		this.id = attributeId;
		this.name = name;
		this.nativeClass = nativeClass;
		setFullClassName(fullClassName);
		if(nativeClass!=null){
			attributeType = ODBType.getFromClass(nativeClass);
		}else{
			if(fullClassName!=null){
				attributeType = ODBType.getFromName(fullClassName);
			}
		}
		classInfo = info;
		isIndex = false;
	}

	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
	}

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNative() {
		return attributeType.isNative();
	}
    public boolean isNonNative() {
        return !attributeType.isNative();
    }

	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
		setClassName(OdbClassUtil.getClassName(fullClassName));
		setPackageName(OdbClassUtil.getPackageName(fullClassName));
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("id=").append(id).append(" name=").append(name).append(" | is Native=").append(isNative()).append(" | type=").append(getFullClassname()).append(" | isIndex=").append(isIndex);
		return buffer.toString();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFullClassname(){
		if(fullClassName!=null){
			return fullClassName;
		}
		
		if(packageName==null || packageName.length()==0){
			fullClassName = className;
			return className;
		}
		fullClassName = packageName+"."+className;
		return fullClassName;
	}

	public void setAttributeType(ODBType attributeType) {
		this.attributeType = attributeType;
	}
	public ODBType getAttributeType() {
		return attributeType;
	}
	public Class getNativeClass() {
		return nativeClass;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
