
package org.neodatis.tool.wrappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;



/**To Wrap SimpleDatFormat
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbDateFormat {
	private SimpleDateFormat sdf;
	private String pattern;
	
	public OdbDateFormat(String pattern){
		this.pattern = pattern;
		this.sdf = new SimpleDateFormat(pattern);
	}
	
	public String format(Date date){
		return sdf.format(date);
	}
	public Date parse(String text) {
		try {
			return sdf.parse(text);
		} catch (ParseException e) {
			throw new ODBRuntimeException(NeoDatisError.FORMT_INVALID_DATE_FORMAT.addParameter(text).addParameter(pattern));
		}
	}
}
