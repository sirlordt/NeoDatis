
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

import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
/**
 * A response to a GetObjectHeaderFromIdMessage comamnd
 * @author olivier s
 *
 */


public class GetObjectHeaderFromIdMessageResponse extends Message {
	/**  header of meta representation of the object*/
	private ObjectInfoHeader oih;
	
	public GetObjectHeaderFromIdMessageResponse(String baseId, String connectionId, String error){
		super(Command.GET_OBJECT_HEADER_FROM_ID, baseId,connectionId);
		setError(error);
	}

	public GetObjectHeaderFromIdMessageResponse(String baseId, String connectionId, ObjectInfoHeader oih){
		super(Command.GET_OBJECT_HEADER_FROM_ID, baseId,connectionId);
		this.oih = oih;
	}

	public ObjectInfoHeader getObjectInfoHeader() {
		return oih;
	}
}

