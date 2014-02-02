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
package org.neodatis.odb;

import org.neodatis.odb.core.IError;
import org.neodatis.odb.core.Release;
import org.neodatis.tool.wrappers.OdbThread;

/**
 * Generic ODB Runtime exception : Used to report all problems.
 * 
 * @author osmadja
 * 
 */
public class ODBRuntimeException extends RuntimeException {
	private static final String url = "https://sourceforge.net/tracker/?func=add&group_id=179124&atid=887885";
	private static final String message1 = String.format("\nNeoDatis has thrown an Exception, please help us filling a bug report at %s with the following error message",url);
	

	// FIXME add a submit a bug link to SF
	public ODBRuntimeException(IError error, Throwable t) {
		super(String.format("%s\nVersion=%s , Build=%s, Date=%s, Thread=%s\nNeoDatisError:%s\nStackTrace:" ,message1, Release.RELEASE_NUMBER , Release.RELEASE_BUILD, Release.RELEASE_DATE, OdbThread.getCurrentThreadName() , error.toString()), t);
	}

	public ODBRuntimeException(IError error) {
		super(String.format("%s\nVersion=%s , Build=%s, Date=%s, Thread=%s\nNeoDatisError:%s\nStackTrace:" ,message1, Release.RELEASE_NUMBER , Release.RELEASE_BUILD, Release.RELEASE_DATE, OdbThread.getCurrentThreadName() , error.toString()));
	}

	public ODBRuntimeException(IError error, String message) {
		super(String.format("%s\nVersion=%s , Build=%s, Date=%s, Thread=%s\nNeoDatisError:%s\nMessage:\n%s" ,message1, Release.RELEASE_NUMBER , Release.RELEASE_BUILD, Release.RELEASE_DATE, OdbThread.getCurrentThreadName() , error.toString(),message));
	}

	public void addMessageHeader(String string) {
		// TODO Auto-generated method stub

	}

	public ODBRuntimeException(Exception e, String message) {
		super(String.format("%s\nVersion=%s , Build=%s, Date=%s, Thread=%s\nMessage:%s\nStackTrace:" ,message1, Release.RELEASE_NUMBER , Release.RELEASE_BUILD, Release.RELEASE_DATE, OdbThread.getCurrentThreadName() , message), e);
	}
}
