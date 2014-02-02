/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.vo.login.Function;



/**
 * @author olivier
 *
 */
public class ThreadOdbUpdate extends Thread {
	private ODB odb;
	private boolean done;
	
	public ThreadOdbUpdate(ODB odb){
		this.odb = odb;
	}
	
	
	public void run(){
		Objects<Function> functions = odb.getObjects(new CriteriaQuery(Function.class,Where.not(Where.equal("name", "updated"))));
		Function f = functions.getFirst();
		String name = f.getName();
		f.setName("updated");
		odb.store(f);
		odb.commit();
		System.out.println(String.format("Updating function with name %s in thread %s",name,Thread.currentThread().getName()));
		done = true;
	}
	public boolean isDone(){
		return done;
	}

}
