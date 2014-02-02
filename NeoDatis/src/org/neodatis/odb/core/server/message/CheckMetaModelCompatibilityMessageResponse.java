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

import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer3.engine.CheckMetaModelResult;
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;

public class CheckMetaModelCompatibilityMessageResponse extends Message{
	private CheckMetaModelResult result;
	private MetaModel updatedMetaModel;
	public CheckMetaModelCompatibilityMessageResponse(String baseId, String sessionId, CheckMetaModelResult result, MetaModel metaModel) {
		super(Command.CHECK_META_MODEL_COMPATIBILITY,baseId,sessionId);
		this.result = result;
		this.updatedMetaModel = metaModel;
	}
	public CheckMetaModelCompatibilityMessageResponse(String baseId,String sessionId,  String error) {
		super(Command.CHECK_META_MODEL_COMPATIBILITY,baseId,sessionId);
		setError(error);
	}
	public String toString() {
		return "CheckMetaModelCompatibility";
	}
	public CheckMetaModelResult getResult() {
		return result;
	}
	public void setResult(CheckMetaModelResult result) {
		this.result = result;
	}
	public MetaModel getUpdatedMetaModel() {
		return updatedMetaModel;
	}
	public void setUpdatedMetaModel(MetaModel updatedMetaModel) {
		this.updatedMetaModel = updatedMetaModel;
	}
	
}
