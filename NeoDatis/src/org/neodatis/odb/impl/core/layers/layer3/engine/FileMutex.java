
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
package org.neodatis.odb.impl.core.layers.layer3.engine;

import java.util.Map;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/** A mutex to logically lock ODB database file
 * 
 * @author osmadja
 *
 */
public class FileMutex {
	private static FileMutex instance = new FileMutex();
	private Map<String,String> openFiles;
	
	private FileMutex(){
		openFiles = new OdbHashMap<String, String>();
	}
	

	public static synchronized FileMutex getInstance() {
		return instance;
	}

	public void releaseFile(String fileName) {
		synchronized (openFiles) {
			openFiles.remove(fileName);
		}
	}

	public void lockFile(String fileName) {
		synchronized (openFiles) {
			openFiles.put(fileName, fileName);
		}
	}

	private boolean canOpenFile(String fileName) {
		synchronized (openFiles) {
			boolean canOpen = openFiles.get(fileName) == null;
			if(canOpen){
				lockFile(fileName);
			}
			return canOpen;
		}
	}

	public boolean openFile(String fileName) {
		boolean canOpenfile = canOpenFile(fileName);

		if (!canOpenfile) {
			if (OdbConfiguration.retryIfFileIsLocked()) {
				int nbRetry = 0;
				while (!canOpenFile(fileName) && nbRetry < OdbConfiguration.getNumberOfRetryToOpenFile()) {
					try {
						OdbThread.sleep(OdbConfiguration.getRetryTimeout());
					} catch (InterruptedException e) {
						// nothing to do
					}
					nbRetry++;
				}
				if(nbRetry<OdbConfiguration.getNumberOfRetryToOpenFile()){
					return true;
				}
			}
			
			return false;
		}
		return true;
	}
}
