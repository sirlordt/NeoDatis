
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
package org.neodatis.odb.core.server.layers.layer3.engine;

import java.io.Serializable;

public class Command implements Serializable{
	
	public static final int CONNECT = 1;
	public static final int GET = 2;
	public static final int GET_OBJECT_FROM_ID = 3;
	public static final int STORE = 4;
	public static final int DELETE_OBJECT = 5;
	public static final int CLOSE = 6;
	public static final int COMMIT = 7;
	public static final int ROLLBACK = 8;
	public static final int DELETE_BASE = 9;
	public static final int GET_SESSIONS = 10;
	public static final int ADD_UNIQUE_INDEX = 11;
	public static final int ADD_CLASS_INFO_LIST = 12;
	public static final int COUNT = 13;
	public static final int GET_OBJECT_VALUES = 14;
	public static final int GET_OBJECT_HEADER_FROM_ID = 15;
	public static final int REBUILD_INDEX = 16;
	public static final int DELETE_INDEX = 17;
	public static final int CHECK_META_MODEL_COMPATIBILITY = 18;
	
	
	public static final int [] commands = {CONNECT,GET,GET_OBJECT_FROM_ID,STORE,DELETE_OBJECT,CLOSE,COMMIT,ROLLBACK,DELETE_BASE,GET_SESSIONS,ADD_UNIQUE_INDEX,ADD_CLASS_INFO_LIST,COUNT,GET_OBJECT_VALUES,GET_OBJECT_HEADER_FROM_ID,REBUILD_INDEX,DELETE_INDEX,CHECK_META_MODEL_COMPATIBILITY};

	
}
