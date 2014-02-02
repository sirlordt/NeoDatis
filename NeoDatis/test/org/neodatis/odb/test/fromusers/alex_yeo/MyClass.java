/**
 * 
 */
package org.neodatis.odb.test.fromusers.alex_yeo;

/**
 * @author olivier
 *
 */
public class MyClass {
	String name;
	IDevice<?> device;
	public MyClass(String name, IDevice<?> device) {
		super();
		this.name = name;
		this.device = device;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public IDevice<?> getDevice() {
		return device;
	}
	public void setDevice(IDevice<?> device) {
		this.device = device;
	}
	
	

}
