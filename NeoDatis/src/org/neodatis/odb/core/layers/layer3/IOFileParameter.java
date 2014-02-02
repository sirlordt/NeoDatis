
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
package org.neodatis.odb.core.layers.layer3;

import org.neodatis.tool.IOUtil;
import org.neodatis.tool.wrappers.io.OdbFile;

/**
 * Database Parameters for local database access
 * @author osmadja
 *
 */
public class IOFileParameter implements IBaseIdentification{
	private String fileName;
	private boolean canWrite;
	private String user;
	private String password;
	
	
	public IOFileParameter(String name, boolean write, String user, String password) {
		super();
		fileName = name;
		canWrite = write;
		this.user = user;
		this.password = password;
	}
	public boolean canWrite() {
		return canWrite;
	}
	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String toString() {
		return fileName;
	}
	public String getDirectory(){
		return new OdbFile(fileName).getDirectory();
	}
	public String getCleanFileName(){
		return new OdbFile(fileName).getCleanFileName();
	}
	public String getIdentification() {
		return getCleanFileName();
	}

	public boolean isNew(){
		return !IOUtil.existFile(fileName);
	}
	
	public boolean isLocal() {
		return true;
	}
	public String getUserName() {
		return user;
	}
	public void setUserName(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
