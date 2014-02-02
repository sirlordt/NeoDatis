/*
NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

"This file is part of the NeoDatis ODB open source object database".

NeoDatis ODB is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

NeoDatis ODB is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package org.neodatis.tool.mutex;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * A Simple Mutex for lock operations
 * @author osmadja
 * @sharpen.ignore
 *
 */
public class Mutex {

	/** The name of the mutex*/
	private String name;
	/** The lock status * */
	protected boolean inUse;
	protected int nbOwners;
	private boolean debug;

	protected Mutex(String name){
		this.name = name;
		this.inUse = false;
		this.nbOwners = 0;
	}
	public Mutex acquire(String who) throws InterruptedException {
		if(debug){
			DLogger.info("Thread "+OdbThread.getCurrentThreadName()+" - "+who+" : Trying to acquire mutex " + name  );
			//DLogger.info("From " + StringUtils.exceptionToString(new Exception(), false));
		}
		if (Thread.interrupted())
			throw new InterruptedException();
		synchronized (this) {
			try {
				while (inUse){
					wait();
				}
				if(nbOwners!=0){
					throw new InterruptedException("nb owners != 0 - "+nbOwners);
				}
				inUse = true;
				nbOwners++;
			} catch (InterruptedException ex) {
				notify();
				throw ex;
			}
		}
		if(debug){
			DLogger.info("Thread "+OdbThread.getCurrentThreadName()+" - "+who+" : Mutex " + name+" acquired!"  );
		}
		
		return this;
	}

	public synchronized void release(String who) {
		if(debug){
			DLogger.info("Thread "+OdbThread.getCurrentThreadName()+" - "+who+" : Releasing mutex " + name);
		}
		inUse = false;
		nbOwners--;
		if(nbOwners<0){
			throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("Nb owner is negative in release("+who+")"));
		}
		notify();
	}

	public boolean attempt(long msecs) throws InterruptedException {
		if(debug){
			DLogger.info("Thread "+OdbThread.getCurrentThreadName()+" : Trying to acquire(atempt) mutex " + name  );
			//DLogger.info("From " + StringUtils.exceptionToString(new Exception(), false));
		}
		try{
			if (Thread.interrupted())
				throw new InterruptedException();
			synchronized (this) {
				if (!inUse) {
					inUse = true;
					nbOwners++;
					return true;
				} else if (msecs <= 0)
					return false;
				else {
					long waitTime = msecs;
					long start = OdbTime.getCurrentTimeInMs();
					try {
						for (;;) {
							wait(waitTime);
							if (!inUse) {
								inUse = true;
								nbOwners++;
								return true;
							} 
							waitTime = msecs - (OdbTime.getCurrentTimeInMs() - start);
							if (waitTime <= 0)
								return false;
						}
					} catch (InterruptedException ex) {
						notify();
						throw ex;
					}
				}
			}			
		}finally{
			if(debug){
				DLogger.info("Thread "+OdbThread.getCurrentThreadName()+" : Mutex " + name+" acquired! (by attempt)"  );
			}
		}
	}
	public String getName() {
		return name;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public boolean isInUse(){
		return inUse;
	}
	public int getNbOwners() {
		return nbOwners;
	}
	
}
