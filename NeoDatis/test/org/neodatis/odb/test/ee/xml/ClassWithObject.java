/**
 * 
 */
package org.neodatis.odb.test.ee.xml;

/**
 * @author olivier
 *
 */
public class ClassWithObject {
	protected String name;
	protected Object value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public ClassWithObject(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	
}
