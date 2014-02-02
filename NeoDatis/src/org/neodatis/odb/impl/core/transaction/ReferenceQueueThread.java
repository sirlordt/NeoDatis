package org.neodatis.odb.impl.core.transaction;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbRunnable;
/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class ReferenceQueueThread implements OdbRunnable {
	private ReferenceQueue referenceQueue;
	private long timeBetweenEachCheck;
	private boolean on;
	public static int nbObjects;
	
	public ReferenceQueueThread(CrossSessionCache cache, long timeBetweenEachCheck){
		//this.referenceQueue = cache.getQueue();
		this.timeBetweenEachCheck = timeBetweenEachCheck;
		this.on = true;
	}
	
	public void run(){
		Reference ref = null;
		
		while(on){
			try {
				ref = referenceQueue.remove();
				/*
				if(ref!=null){
					Object o = ref.get();
					if(o!=null){
						//DLogger.debug(String.format("Object %s of type %s with hc %d has been queued",ref,ref.getClass().getName(),System.identityHashCode(ref)));
						DLogger.debug(String.format("Object %s of type %s with hc %d has been queued",o.toString(),o.getClass().getName(),System.identityHashCode(o)));
					}else{
						DLogger.debug("o is null");
					}
				}
				*/
				nbObjects++;
				//Thread.sleep(timeBetweenEachCheck);
				//System.out.println(".");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
