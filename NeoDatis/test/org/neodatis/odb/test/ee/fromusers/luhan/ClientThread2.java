/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.luhan;

/**
 * @author olivier
 *
 */

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.tool.wrappers.OdbThread;

public class ClientThread2 extends Thread {

	private int consto;

	public ClientThread2(int consto) {
		this.consto = consto;
	}

	public void run() {
		
		if (consto == 1) {
			ODB odb = ODBFactory.openClient("localhost", 8989, "base1");
			Account account = new Account(200621639);
			account.setCurrentDeposit(1000);
			account.setFixedDeposit(0);
			odb.store(account);
			odb.close();
			
		} else {
			System.out.println(consto);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		for (int i = 0; i < 10; i++) {
			ODB odb = ODBFactory.openClient("localhost", 8989, "base1");
			Objects objects = odb.getObjects(Account.class);
			System.out.println("(" + consto + ") found " + objects.size() + " accounts");
			while (objects.hasNext()) {
				Account a = (Account) objects.next();
				System.out.println("(" + consto + ") current deposit =" + a.getCurrentDeposit());
				a.setCurrentDeposit(a.getCurrentDeposit() + consto);
				odb.store(a);
			}
			odb.close();
		}
	}
}