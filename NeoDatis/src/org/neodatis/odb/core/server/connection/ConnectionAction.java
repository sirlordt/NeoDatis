
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
package org.neodatis.odb.core.server.connection;


/** A simple class with some constants to describe what a connection is doing
 * 
 * @author osmadja
 *
 */
public class ConnectionAction {
	public static final int ACTION_NO_ACTION = -1;
	public static final String ACTION_NO_ACTION_LABEL = "-";
	public static final int ACTION_CONNECT = 0;
	public static final String ACTION_CONNECT_LABEL = "connect";
	public static final int ACTION_INSERT = 1;
	public static final String ACTION_INSERT_LABEL = "insert";
	public static final int ACTION_UPDATE = 2;
	public static final String ACTION_UPDATE_LABEL = "update";
	public static final int ACTION_DELETE = 3;
	public static final String ACTION_DELETE_LABEL = "delete";
	public static final int ACTION_SELECT = 4;
	public static final String ACTION_SELECT_LABEL = "select";
	public static final int ACTION_COMMIT = 5;
	public static final String ACTION_COMMIT_LABEL = "commit";
	public static final int ACTION_CLOSE = 6;
	public static final String ACTION_CLOSE_LABEL = "close";
	public static final int ACTION_ROLLBACK = 7;
	public static final String ACTION_ROLLBACK_LABEL = "rollback";
	
	protected static final String[] ACTION_LABELS = {ACTION_CONNECT_LABEL,ACTION_INSERT_LABEL,ACTION_UPDATE_LABEL,ACTION_DELETE_LABEL,ACTION_SELECT_LABEL, ACTION_COMMIT_LABEL,ACTION_CLOSE_LABEL,ACTION_ROLLBACK_LABEL};
	
	public static int getNumberOfActions(){
		return ACTION_LABELS.length;
	}
	public static String getActionLabel(int action){
		if(action==ACTION_NO_ACTION){
			return ACTION_NO_ACTION_LABEL;
		}
		return ACTION_LABELS[action];
	}

}
