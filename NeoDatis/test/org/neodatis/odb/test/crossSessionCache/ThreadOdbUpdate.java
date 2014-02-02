/**
 * 
 */
package org.neodatis.odb.test.crossSessionCache;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.vo.login.Function;



/**
 * @author olivier
 *
 */
public class ThreadOdbUpdate extends Thread {
	private ODBServer server;
	private String baseId;
	private boolean goOn;
	private Function function;
	
	public ThreadOdbUpdate(ODBServer server, String baseId, Function f){
		this.server = server;
		this.baseId = baseId;
		this.goOn = true;
		this.function = f;
	}
	
	
	public void run(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ODB odb = server.openClient(baseId);
		function.setName(function.getName()+ "-updated from thread " + Thread.currentThread().getName());
		odb.store(function);
		odb.close();
		System.out.println(Thread.currentThread().getName()+" has saved the object");
		
	}
	public void end(){
		goOn = false;
	}

}
