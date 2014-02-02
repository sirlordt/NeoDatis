/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.test.vo.login.Function;



/**
 * @author olivier
 *
 */
public class ThreadOdbStore extends Thread {
	private ODB odb;
	private String id;
	private boolean done;
	
	public ThreadOdbStore(ODB odb, String id){
		this.odb = odb;
		this.id = id;
	}
	
	
	public void run(){
		OID oid = odb.store(new Function(id));
		odb.commit();
		System.out.println(String.format("Inserting function with id %s in thread %s and OID=%s",id,Thread.currentThread().getName(),oid.toString()));
		done = true;
	}
	public boolean isDone(){
		return done;
	}

}
