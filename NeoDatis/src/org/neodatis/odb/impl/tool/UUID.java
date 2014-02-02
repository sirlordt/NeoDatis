
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

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.impl.core.oid.DatabaseIdImpl;
import org.neodatis.tool.wrappers.OdbRandom;
import org.neodatis.tool.wrappers.OdbTime;


/**
 * Unique ID generator
 * @author osmadja
 *
 */
public class UUID {

	public static synchronized long getUniqueId(String simpleSeed){
		long id = OdbTime.getCurrentTimeInMs()-(long) (OdbRandom.getRandomDouble()*simpleSeed.hashCode());
		return id;
	}
	public static synchronized long getRandomLongId(){
		long id = (long) (OdbRandom.getRandomDouble()*Long.MAX_VALUE); 
		return id;
	}
	
	/** Returns a block marker , 5 longs
	 * 
	 * @param position
	 * @return A 4 long array 
	 */
	synchronized static public long [] getBlockMarker(long position){
		long l1 = 0xFFEFCFBF;
		long [] id = {l1, l1, l1 , position , l1};
		return id;
	}

	/** Returns a database id : 4 longs
	 * 
	 * @param creationDate
	 * @return a 4 long array
	 */
	synchronized static public DatabaseId getDatabaseId(long creationDate){
		long [] id = {creationDate, getRandomLongId(), getRandomLongId() , getRandomLongId()};
		// FIXME do  not instanciate directly
		DatabaseId databaseId = new DatabaseIdImpl(id);
		return databaseId;
	}
}
