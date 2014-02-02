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

import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;

/**
 * A message to get object values
 * @author osmadja
 *
 */
public class GetObjectValuesMessage extends Message {
	private IValuesQuery query;
	private int startIndex;
	private int endIndex;
	
	public GetObjectValuesMessage(String baseId, String connectionId, IValuesQuery query, int startIndex, int endIndex){
		super(Command.GET_OBJECT_VALUES, baseId,connectionId);
		this.query = query;		
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public GetObjectValuesMessage(String baseId, String connectionId, IValuesQuery query){
		this(baseId,connectionId,query,-1,-1);
	}

	public IValuesQuery getQuery() {
		return query;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public String toString() {
		return "GetObjectValues";
	}
}
