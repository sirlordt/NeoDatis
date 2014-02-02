/**
 * 
 */
package org.neodatis.odb.test.ee.insert;

/**
 * @author olivier
 *
 */
public class ClassWithClass {
	private String name;
	private Class clazz;
	public ClassWithClass(String name, Class clazz) {
		super();
		this.name = name;
		this.clazz = clazz;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	};
	
	
}
