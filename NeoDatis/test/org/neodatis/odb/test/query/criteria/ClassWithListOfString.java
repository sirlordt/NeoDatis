/**
 * 
 */
package org.neodatis.odb.test.query.criteria;

import java.util.List;

/**
 * @author olivier
 *
 */
public class ClassWithListOfString {
	private String name;
	private List<String> strings;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getStrings() {
		return strings;
	}
	public void setStrings(List<String> strings) {
		this.strings = strings;
	}
	public ClassWithListOfString(String name, List<String> strings) {
		super();
		this.name = name;
		this.strings = strings;
	}
	
	

}
