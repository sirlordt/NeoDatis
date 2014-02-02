/**
 * 
 */
package org.neodatis.odb.test.fromusers.gyowanny_queiroz;

import java.util.Calendar;

/**
 * @author olivier
 *
 */
public class ClassWithCalendar {
	private String name;
	private Calendar calendar;
	public ClassWithCalendar(String name, Calendar calendar) {
		super();
		this.name = name;
		this.calendar = calendar;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	

}
