/**
 * 
 */
package org.neodatis.odb.test.vo.company;


/**
 * @author olivier
 *
 */
public class Company {
	protected String name;
	protected Address address;
	
	public Company(String name, Address address){
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	
}
