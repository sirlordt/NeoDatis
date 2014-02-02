/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.cocowala;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author olivier
 *
 */
public class Report implements IReport{
	private Long id;
	private Date startTime;
	private Date endTime;
	private ISubject subject;
	private Map<String, IReportItem> items;
	
	public Report()
	{
		this.id = new Long(1);
		this.startTime = new Date();
		this.items = new HashMap<String, IReportItem>();
	}

	public Report(ISubject subject){
		this();
		this.subject = subject;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public ISubject getSubject() {
		return subject;
	}
	public void setSubject(ISubject subject) {
		this.subject = subject;
	}
	public Map<String, IReportItem> getItems() {
		return items;
	}
	public void setItems(Map<String, IReportItem> items) {
		this.items = items;
	}
	
	
}
