/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.cocowala;

import java.util.Date;

/**
 * @author olivier
 *
 */
public class Subject implements ISubject{
	private Long id;
	private String name;
	private String displayName;
	private int code;
	private Date lastModifiedTime;
	
	public Subject()
	{
		this.id = new Long(1);
		this.lastModifiedTime = new Date();
	}

	public Subject(String name, String displayName, int code)
	{
		this();
		this.name = name;
		this.displayName = displayName;
		this.code = code;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	
}
