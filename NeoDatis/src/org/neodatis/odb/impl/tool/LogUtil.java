
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
package org.neodatis.odb.impl.tool;

import org.neodatis.odb.OdbConfiguration;

/** To manage logging level 
 * 
 * @author osmadja
 *
 */
public class LogUtil {
    public static final String OBJECT_WRITER = "ObjectWriter";
    public static final String OBJECT_READER = "ObjectReader";
    public static final String FILE_SYSTEM_INTERFACE = "FileSystemInterface";
    public static final String ID_MANAGER = "IdManager";
    public static final String TRANSACTION = "Transaction";
    public static final String BUFFERED_IO = "BufferedIO";
    public static final String MULTI_BUFFERED_IO = "MultiBufferedIO";
    public static final String WRITE_ACTION = "WriteAction";
    
    
    public static void objectWriterOn(boolean yes){
        if(yes){
            OdbConfiguration.addLogId(OBJECT_WRITER);
        }else{
            OdbConfiguration.removeLogId(OBJECT_WRITER);
        }
    }
    public static void objectReaderOn(boolean yes){
        if(yes){
            OdbConfiguration.addLogId(OBJECT_READER);
        }else{
            OdbConfiguration.removeLogId(OBJECT_READER);
        }
    }
    public static void fileSystemOn(boolean yes){
        if(yes){
            OdbConfiguration.addLogId(FILE_SYSTEM_INTERFACE);
        }else{
            OdbConfiguration.removeLogId(FILE_SYSTEM_INTERFACE);
        }
    }
    
    public static void idManagerOn(boolean yes){
        if(yes){
            OdbConfiguration.addLogId(ID_MANAGER);
        }else{
            OdbConfiguration.removeLogId(ID_MANAGER);
        }
    }

    public static void transactionOn(boolean yes){
        if(yes){
            OdbConfiguration.addLogId(TRANSACTION);
        }else{
            OdbConfiguration.removeLogId(TRANSACTION);
        }
    }

    public static void logOn(String logId, boolean yes){
    	if(yes){
            OdbConfiguration.addLogId(logId);
        }else{
            OdbConfiguration.removeLogId(logId);
        }
    }
    public static void allOn(boolean yes){
    	OdbConfiguration.setLogAll(yes);
    }
	public static void enable(String logId) {
		OdbConfiguration.addLogId(logId);
		
	}
}
