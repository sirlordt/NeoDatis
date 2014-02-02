/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class ThreadOdbGetWithSameConnectionWithNQ extends Thread {
	private ODB odb;
	private String id;
	private boolean goOn;

	public ThreadOdbGetWithSameConnectionWithNQ(ODB odb) {
		this.odb = odb;
		this.goOn = true;
	}

	public void run() {
		IQuery q = new SimpleNativeQuery(){
			public boolean match(Function f){
				return true;
			}
		};
		Objects<Function> functions = odb.getObjects(q);
		System.out.println(String.format("%d functions in thread %s", functions.size(),Thread.currentThread().getName()));
	}

	public void end() {
		goOn = false;
	}

}
