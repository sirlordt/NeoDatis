/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.cocowala;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author olivier
 *
 */
public class ReportItem implements IReportItem{
	private String name;
	private Collection<IReportDetailItem> detail;
	
	public ReportItem()
	{
		this.detail = new ArrayList<IReportDetailItem>();
	}

	public ReportItem(String name)
	{
		this();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Collection<IReportDetailItem> getDetail() {
		return detail;
	}
	public void setDetail(Collection<IReportDetailItem> detail) {
		this.detail = detail;
	}
	
	

}
