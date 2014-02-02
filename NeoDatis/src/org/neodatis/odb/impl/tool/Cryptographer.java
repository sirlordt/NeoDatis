
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;

/**
 * @sharpen.ignore
 * A simple cypher. Used to cypher password of the NeoDatis ODB Database.
 * @author osmadja
 *
 */
public class Cryptographer {

	private static MessageDigest md = null;
	public Cryptographer() {
		super();
	}
	
	private synchronized static void checkInit(){
		if(md==null){
			try {
				md = MessageDigest.getInstance( "MD5" );
			} catch (NoSuchAlgorithmException e) {
				throw new ODBRuntimeException(NeoDatisError.CRYPTO_ALGORITH_NOT_FOUND);
			}
		}
	}
	public static String encrypt(String string) {
		if(string==null){
			return null;
		}
		String encoding = OdbConfiguration.getDatabaseCharacterEncoding(); 
		checkInit();
        try {
			md.update( string.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return new String(md.digest(),encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(md.digest());

	}

}
