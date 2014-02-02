
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

package org.neodatis.tool.wrappers;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbString {
	public static String[] split(String source, String separators){
		return source.split(separators);
	}
	
	/** Replace a string within a string
	@param source The String to modify
	@param tokenToReplace The Token to replace
	@param newToken The new Token
	@return String The new String
	@exception RuntimeException where trying to replace by a new token and this new token contains the token to be replaced
	*/
	static public String replaceToken( String source , String tokenToReplace , String newToken  )
	{
	    // Default is to replace all -> -1
	    return replaceToken( source , tokenToReplace , newToken , -1 );
	}


	/** Replace a string within a string
	@param source The String to modify
	@param tokenToReplace The Token to replace
	@param newToken The new Token
	@param numberOfTimes The number of time, the replace operation must be done. -1 means replace all
	@return String The new String
	@exception RuntimeException where trying to replace by a new token and this new token contains the token to be replaced
	*/
	static public String replaceToken( String source , String tokenToReplace , String newToken , int numberOfTimes )
	{
		int index = 0;
		boolean hasToken = true;
		StringBuffer result = new StringBuffer( source );
		String tmp = result.toString();
		int oldTokenLength = tokenToReplace.length();
		int times = 0;

		// To prevent from replacing the token with a token containing the Token to replace
		if( numberOfTimes == -1 && newToken.indexOf(tokenToReplace) != -1 )
		{
		  throw new RuntimeException("Can not replace by this new token because it contains token to be replaced");
		}

		while(hasToken)
		{
			index = tmp.indexOf( tokenToReplace , index );

			hasToken = (index != -1);

			if( hasToken )
			{
				// Control number of times
				if( numberOfTimes != -1 )
				{
				    if( times < numberOfTimes )
				    {
					times ++;
				    }
				    else
				    {
					// If we already replaced the number of times asked then exit
					break;
				    }
				}

				result.replace( index , index + oldTokenLength , newToken );
				tmp = result.toString();
			}

			index = 0;

		}

		return result.toString();
	}

	/**
	 * If escape==true, then remove $.
	 * @param e
	 * @param escape
	 * @return
	 */
	public static String exceptionToString(Throwable e, boolean escape) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String s = sw.getBuffer().toString();
		if(escape){
			s = s.replaceAll("\\$", "-");
		}
		return s;
	}
	public static String substring(String s, int beginIndex){
		return substring(s, beginIndex, s.length());
	}
	
	public static String substring(String s, int beginIndex, int endIndex){
		if (beginIndex < 0) {
		    throw new StringIndexOutOfBoundsException(beginIndex);
		}
		if (endIndex > s.length()) {
		    throw new StringIndexOutOfBoundsException(endIndex);
		}
		if (beginIndex > endIndex) {
		    throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
		}
		if(beginIndex == 0 && endIndex == s.length()){
			return s;
		}
		StringBuffer buffer = new StringBuffer();
		for(int i=beginIndex;i<endIndex;i++){
			buffer.append(s.charAt(i));
		}
		return buffer.toString();
	}
	public static boolean equalsIgnoreCase(String s1, String s2){
		return s1.equalsIgnoreCase(s2);
	}
	public static boolean matches(String regExp, String value){
		return value.matches(regExp);
	}

}
