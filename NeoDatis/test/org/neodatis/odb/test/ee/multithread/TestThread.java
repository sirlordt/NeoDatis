/**
 * 
 */
package org.neodatis.odb.test.ee.multithread;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 *
 */
public class TestThread extends Thread{

	
	private ODB odb;

	public TestThread(ODB odb){
		this.odb = odb;
	}
	
	public void run() {
		String tname = Thread.currentThread().getName();
		System.out.println(tname + " - starting thread ");
		odb.store(new Function(Thread.currentThread().getName()));
		System.out.println(tname + " - object stored");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(tname + " - commiting thread ");
		odb.commit();
		System.out.println(tname + " - ending thread");
	
	}

}
