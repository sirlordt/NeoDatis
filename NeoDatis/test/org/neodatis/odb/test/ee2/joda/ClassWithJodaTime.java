/**
 * 
 */
package org.neodatis.odb.test.ee2.joda;

import org.joda.time.DateTime;

/**
 * @author olivier
 *
 */
public class ClassWithJodaTime {
	protected DateTime dateTime;
	protected String name;
	public DateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ClassWithJodaTime(DateTime dateTime, String name) {
		super();
		this.dateTime = dateTime;
		this.name = name;
	}
	public String toString() {
		return name + " - " + dateTime.toString();
	}
	

}
