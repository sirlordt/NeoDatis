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
package org.neodatis.btree;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.core.IError;
import org.neodatis.tool.wrappers.OdbString;

/**
 * ODB BTree Errors All @ in error description will be replaced by parameters
 * 
 * @author olivier s
 * 
 */
public class BTreeError implements IError {
	private int code;

	private String description;

	private List parameters;

	public static final BTreeError MERGE_WITH_TWO_MORE_KEYS = new BTreeError(500,
			"Trying to merge two node with more keys than allowed! @1 // @2");
	public static final BTreeError LAZY_LOADING_NODE = new BTreeError(501, "Error while loading node lazily with oid @1");
	public static final BTreeError NODE_WITHOUT_ID = new BTreeError(502, "Node with id -1");
	public static final BTreeError NULL_PERSISTER_FOUND = new BTreeError(503, "Null persister for PersistentBTree");
	public static final BTreeError INVALID_ID_FOR_BTREE = new BTreeError(504, "Invalid id for Btree : id=@1");
	public static final BTreeError INVALID_NODE_TYPE = new BTreeError(505, "Node should be a PersistentNode but is a @1");
	public static final BTreeError INTERNAL_ERROR = new BTreeError(506, "Internal error: @1");

	public BTreeError(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public IError addParameter(Object o) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(o != null ? o.toString() : "null");
		return this;
	}

	public IError addParameter(String s) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(s);
		return this;
	}

	public IError addParameter(int i) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(new Integer(i));
		return this;
	}

	public IError addParameter(byte i) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(new Byte(i));
		return this;
	}

	public IError addParameter(long l) {
		if (parameters == null) {
			parameters = new ArrayList();
		}
		parameters.add(new Long(l));
		return this;
	}

	/**
	 * replace the @1,@2,... by their real values.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(code).append(":").append(description);
		String s = buffer.toString();

		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++) {
				String parameterName = "@" + (i + 1);
				String parameterValue = parameters.get(i).toString();
				int parameterIndex = s.indexOf(parameterName);
				if (parameterIndex != -1) {
					s = OdbString.replaceToken(s, parameterName, parameterValue, 1);
				}
			}
		}
		return s;
	}
}
