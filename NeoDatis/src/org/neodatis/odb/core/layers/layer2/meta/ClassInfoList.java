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
import java.util.Collection;
import java.util.Map;

import org.neodatis.tool.wrappers.map.OdbHashMap;


/**
 * A simple list to contain some class infos.
 * 
 * <pre>
 * It used by ClassIntropector.introspect to return all the class info detected by introspecting a class.
 * 
 * For example, if we have a class Class1 that has a field of type Class2. And Class2 has a field of type Class3. 
 * Introspecting Class1 return a ClassInfoList with the classes Class1, Class2, Class3. Class1 being the main class info
 * 
 * 
 * </pre>
 * 
 * @author osmadja
 * 
 */
public class ClassInfoList implements Serializable{
	/** key=ClassInfoName,value=ClassInfo*/
	private Map<String,ClassInfo> classInfos;
	private ClassInfo mainClassInfo;

	public ClassInfoList() {
	}
	
	public ClassInfoList(ClassInfo mainClassInfo) {
		this.classInfos = new OdbHashMap<String, ClassInfo>();
		this.classInfos.put(mainClassInfo.getFullClassName(), mainClassInfo);
		this.mainClassInfo = mainClassInfo;
	}
	
	public ClassInfo getMainClassInfo(){
		return mainClassInfo;
	}
	
	public void addClassInfo(ClassInfo classInfo){
		classInfos.put(classInfo.getFullClassName(),classInfo);
	}
	
	public Collection<ClassInfo> getClassInfos(){
		return classInfos.values();
	}
	public boolean hasClassInfos(){
		return classInfos.size()!=0;
	}

	/**
	 * 
	 * @param name
	 * @return null if it does not exist
	 */
	public ClassInfo getClassInfoWithName(String name) {
		return classInfos.get(name);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(classInfos.size()).append(" - ").append(classInfos.keySet());
		return buffer.toString();
	}

	public void setMainClassInfo(ClassInfo classInfo) {
		this.mainClassInfo = classInfo;
	}
	
}
