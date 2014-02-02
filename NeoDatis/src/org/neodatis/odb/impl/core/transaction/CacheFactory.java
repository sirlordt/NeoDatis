package org.neodatis.odb.impl.core.transaction;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITmpCache;

public class CacheFactory {
	
	public static ICache getLocalCache(ISession session, String name){
		if(OdbConfiguration.useLazyCache()){
			return null;//new LazyCache(session);
		}
		return new Cache(session,name);
	}

	public static ITmpCache getLocalTmpCache(ISession session, String name){
		if(OdbConfiguration.useLazyCache()){
			return new TmpCache(session,name);
		}
		return new TmpCache(session,name);
	}
	
	public static ICache getServerCache(ISession session){
		
		if(OdbConfiguration.useLazyCache()){
			return null;//new LazyServerCache(session);
		}
		
		return new ServerCache(session);
	}
	
	/**
	 * This factory method returns an implementation of {@link ICrossSessionCache}
	 * to take over the objects across the sessions.
	 * @param identification TODO
	 * @return {@link ICrossSessionCache}
	 */
	public static ICrossSessionCache getCrossSessionCache(String identification){
		return CrossSessionCache.getInstance(identification);
	}

}
