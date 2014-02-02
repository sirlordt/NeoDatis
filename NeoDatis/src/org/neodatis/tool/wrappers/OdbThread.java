package org.neodatis.tool.wrappers;


/**
 * To wrap java.lang.Thread
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbThread extends Thread {

	public OdbThread() {
		super();
	}

	public OdbThread(OdbRunnable target) {
		super(target);
	}
	
	public static String getCurrentThreadName(){
		return Thread.currentThread().getName();
	}
	public void interrupt(){
		super.interrupt();
	}
}
