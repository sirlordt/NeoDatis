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
package org.neodatis.odb.core.server.message;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.tool.wrappers.list.IOdbList;


/**
 * A NewClassInfoMessageResponse is used by the Client/Server mode to answer a NewClassInfoMessage, 
 * it returns all the class infos of the new server model
 * 
 * @author olivier s
 * 
 */
public class NewClassInfoListMessageResponse extends Message {
	private IOdbList<ClassInfo> classInfos;

	public NewClassInfoListMessageResponse(String baseId, String connectionId, String error) {
		super(Command.ADD_CLASS_INFO_LIST, baseId,connectionId);
		setError(error);
	}
	public NewClassInfoListMessageResponse(String baseId, String connectionId, IOdbList<ClassInfo> classInfos) {
		super(Command.ADD_CLASS_INFO_LIST, baseId,connectionId);
		this.classInfos = classInfos;
	}
	public IOdbList<ClassInfo> getClassInfos() {
		return classInfos;
	}
}
