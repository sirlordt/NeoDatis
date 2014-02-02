/**
 * 
 */
package org.neodatis.odb.test.vo.attribute;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author olivier
 * 
 */
public class ObjectWithDates {
	private String name;
	private Date javaUtilDate;
	private java.sql.Date javaSqlDte;
	private Timestamp timestamp;

	public ObjectWithDates(String name, Date javaUtilDate, java.sql.Date javaSqlDte, Timestamp timestamp) {
		super();
		this.name = name;
		this.javaUtilDate = javaUtilDate;
		this.javaSqlDte = javaSqlDte;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getJavaUtilDate() {
		return javaUtilDate;
	}

	public void setJavaUtilDate(Date javaUtilDate) {
		this.javaUtilDate = javaUtilDate;
	}

	public Date getJavaSqlDte() {
		return javaSqlDte;
	}

	public void setJavaSqlDte(java.sql.Date javaSqlDte) {
		this.javaSqlDte = javaSqlDte;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
