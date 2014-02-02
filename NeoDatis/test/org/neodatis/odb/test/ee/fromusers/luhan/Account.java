/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.luhan;

/**
 * @author olivier
 *
 */
public class Account {

	protected long id;
	protected double currentDeposit;
	protected double fixedDeposit;
	/**
	 * @param i
	 */
	public Account(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getCurrentDeposit() {
		return currentDeposit;
	}
	public void setCurrentDeposit(double currentDeposit) {
		this.currentDeposit = currentDeposit;
	}
	public double getFixedDeposit() {
		return fixedDeposit;
	}
	public void setFixedDeposit(double fixedDeposit) {
		this.fixedDeposit = fixedDeposit;
	}


}
