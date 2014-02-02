/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class ThreadOdbGetWithSameConnectionForEver extends Thread {
	private ODB odb;
	private String id;
	private boolean goOn;
	private int maxSize;

	public ThreadOdbGetWithSameConnectionForEver(ODB odb) {
		this.odb = odb;
		this.goOn = true;
	}

	public void run() {
		while(goOn){
			Objects<Function> functions = odb.getObjects(Function.class);
			maxSize = functions.size();
			System.out.println(String.format("%d functions in thread %s", functions.size(),Thread.currentThread().getName()));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void end() {
		goOn = false;
	}

	public int getMaxSize() {
		return maxSize;
	}

}
