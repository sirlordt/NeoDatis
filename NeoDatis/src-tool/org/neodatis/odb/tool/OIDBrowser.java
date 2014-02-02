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
package org.neodatis.odb.tool;

import java.util.List;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.tool.DisplayUtility;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OIDBrowser {

	public OIDBrowser() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// LogUtil.allOn(true);
		OdbConfiguration.setCheckModelCompatibility(false);
		String fileName = "array1.odb";
		String user = "root";
		String password = "root";
		IBaseIdentification parameter = new IOFileParameter(fileName, false, null, null);
		// IStorageEngine engine =
		// StorageEngineFactory.get(parameter,user,password);
		IStorageEngine engine = OdbConfiguration.getCoreProvider().getClientStorageEngine(parameter);

		List l = engine.getAllObjectIdInfos(null, true);// "br.com.ccr.sct.dav.vo.RelFunctionProfile",true);
		DisplayUtility.display("All ids", l);
		engine.close();
	}
}
