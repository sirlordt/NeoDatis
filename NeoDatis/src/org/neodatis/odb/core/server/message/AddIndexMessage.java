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

import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;


public class AddIndexMessage extends Message {
	protected String className;
	protected String indexName;
	protected String [] indexFieldNames;
	protected boolean acceptMultipleValuesForSameKey;
	protected boolean verbose;
	
	public AddIndexMessage(String baseId, String connectionId, String className, String indexName, String [] fieldNames, boolean acceptMultipleValuesForSameKey, boolean verbose){
		super(Command.ADD_UNIQUE_INDEX, baseId,connectionId);
		this.className = className;
		this.indexFieldNames = fieldNames;
		this.indexName = indexName;
		this.acceptMultipleValuesForSameKey = acceptMultipleValuesForSameKey;
		this.verbose = verbose;
	}

	public String[] getIndexFieldNames() {
		return indexFieldNames;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getClassName() {
		return className;
	}
	public String toString() {
		return "AddIndex";
	}

	public boolean acceptMultipleValuesForSameKey() {
		return acceptMultipleValuesForSameKey;
	}

	public void setAcceptMultipleValuesForSameKey(boolean acceptMultipleValuesForSameKey) {
		this.acceptMultipleValuesForSameKey = acceptMultipleValuesForSameKey;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	
}
