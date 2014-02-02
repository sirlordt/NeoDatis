/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.cocowala;

/**
 * @author olivier
 *
 */
public class ReportDetailItem implements IReportDetailItem {
	private String attribute;
	private String before;
	private String after;
	
	
	public ReportDetailItem()
	{
	}

	public ReportDetailItem(String attribute, String before, String after)
	{
		this.attribute = attribute;
		this.before = before;
		this.after = after;
	}
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}
	
	
	
	
}
