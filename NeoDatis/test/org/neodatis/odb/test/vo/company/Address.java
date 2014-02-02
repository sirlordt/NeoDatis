/**
 * 
 */
package org.neodatis.odb.test.vo.company;

/**
 * @author olivier
 *
 */
public class Address {
	protected City city;

	public Address(City city) {
		super();
		this.city = city;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
	

}
