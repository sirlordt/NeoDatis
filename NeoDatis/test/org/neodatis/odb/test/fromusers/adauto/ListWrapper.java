/**
 * 
 */
package org.neodatis.odb.test.fromusers.adauto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author olivier
 *
 */
public class ListWrapper {
	private String type;
	private List<String> list;
	public ListWrapper(String type) {
		super();
		this.type = type;
		this.list = new ArrayList<String>();
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	
	

}
