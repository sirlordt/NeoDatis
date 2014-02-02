/**
 * 
 */
package org.neodatis.odb.test.vo.company;

/**
 * @author olivier
 *
 */
public class City {
	protected String name;
	protected Code code;
	
	
	
	public City(String name, Code code) {
		super();
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
	}

}
