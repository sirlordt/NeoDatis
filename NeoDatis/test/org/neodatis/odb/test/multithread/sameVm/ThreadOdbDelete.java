/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.vo.login.Function;



/**
 * @author olivier
 *
 */
public class ThreadOdbDelete extends Thread {
	private ODB odb;
	private boolean done;
	private String id;
	public int nbDeleted;
	
	/**
	 * @param server
	 */
	public ThreadOdbDelete(ODBServer server,String baseName) {
		odb = server.openClient(baseName);
	}


	public void run(){
		while(!done){
			if(id!=null){
				Objects<Function> functions = odb.getObjects(new CriteriaQuery(Function.class,Where.equal("name", "f"+id)));
				if(!functions.isEmpty()){
					Function f = functions.getFirst();
					String name = f.getName();
					OID oid = odb.delete(f);
					odb.commit();
					System.out.println(String.format("Deleting function with with oid %s name %s in thread %s",oid.toString(), name,Thread.currentThread().getName()));
					id = null;
					nbDeleted++;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
	}
	public void end(){
		done=true;
	}
	public void setid(String id){
		System.out.println("Triggering delete for id "+id);
		this.id= id;
	}

}
