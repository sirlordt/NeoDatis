/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

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
public class ThreadOdbGet extends Thread {
	private ODB odb;
	private String id;
	private boolean goOn;
	
	public ThreadOdbGet(ODBServer server, String baseId){
		this.odb = server.openClient(baseId);
		this.goOn = true;
	}
	
	
	public void run(){
		while(goOn){
			MetaModel m = new SessionMetaModel();
			Dummy.getEngine(odb).getObjectReader().readMetaModel(m, true);
			Objects<Function> functions = odb.getObjects(Function.class);
			System.out.println(String.format("%d functions", functions.size()));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void end(){
		goOn = false;
	}

}
