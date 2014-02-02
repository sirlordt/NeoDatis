/**
 * 
 */
package org.neodatis.odb.test.enumeration;

/**
 * @author olivier
 *
 */
public class ClassWithEnum {
	private String name;
	private MyEnum myEnum;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MyEnum getMyEnum() {
		return myEnum;
	}
	public void setMyEnum(MyEnum myEnum) {
		this.myEnum = myEnum;
	}
	public ClassWithEnum(String name, MyEnum myEnum) {
		super();
		this.name = name;
		this.myEnum = myEnum;
	}
	

}
