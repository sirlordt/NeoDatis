/**
 * 
 */
package org.neodatis.odb.impl.core.layers.layer1.introspector;

import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer1.introspector.IIntrospectionCallback;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.tool.DLogger;

/**
 * @author olivier
 * 
 */
public class DefaultInstrospectionCallbackForStore implements IIntrospectionCallback {

	protected boolean isUpdate;
	protected ITriggerManager triggerManager;
	protected ICrossSessionCache crossSessionCache;
	protected IStorageEngine engine;

	public DefaultInstrospectionCallbackForStore(IStorageEngine engine, ITriggerManager triggerManager, boolean isUpdate) {
		super();
		this.engine = engine;
		this.triggerManager = triggerManager;
		this.isUpdate = isUpdate;
		// Just for junits
		if(engine!=null){
			this.crossSessionCache = CacheFactory.getCrossSessionCache(engine.getBaseIdentification().getIdentification());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.layers.layer1.introspector.IIntrospectionCallback
	 * #objectFound(java.lang.Object)
	 */
	public boolean objectFound(Object object) {
		if (!isUpdate) {
			if (triggerManager != null) {
				triggerManager.manageInsertTriggerBefore(object.getClass().getName(), object);
			}
		}else{
			if (triggerManager != null) {
				triggerManager.manageUpdateTriggerBefore(object.getClass().getName(), null, object, null);
			}
		}

		if (OdbConfiguration.reconnectObjectsToSession()) {
			checkIfObjectMustBeReconnected(object);
		}
		return true;
	}

	/**
	 * Used to check if object must be reconnected to current session
	 * 
	 * <pre>
	 * An object must be reconnected to session if OdbConfiguration.reconnectObjectsToSession() is true
	 * and object is not in local cache and is in cross session cache. In this case
	 * we had it to local cache
	 * 
	 * </pre>
	 * 
	 * @param object
	 */
	private OID checkIfObjectMustBeReconnected(Object o) {
		if(engine==null){
			// This protection is for JUnit
			return null;
		}
		ISession session = engine.getSession(true);
		
		// If object is in local cache, no need to reconnect it
		OID oid = session.getCache().getOid(o, false);
		if(oid!=null){
			return oid;
		}
		OID oidCrossSession = crossSessionCache.getOid(o);
		//DLogger.info(String.format("Trying to reconnect object %s, type=%s, hc=%d, OID is null ? %s | hc-cache=%d",o.toString(),o.getClass().getName(), System.identityHashCode(o),oidCrossSession==null?"is null":"not null="+oidCrossSession.toString(),System.identityHashCode(crossSessionCache)));
		if(oidCrossSession!=null){
			// reconnect object
			ObjectInfoHeader oih = engine.getObjectInfoHeaderFromOid(oidCrossSession,true);
			session.addObjectToCache(oidCrossSession, o, oih);
			return oidCrossSession;
		}else{
			//DLogger.info(String.format("Cache(hc=%d) is content:\n%s",System.identityHashCode(crossSessionCache), crossSessionCache.toString()));
			//boolean b = crossSessionCache.slowExistObject(o);
			//DLogger.info(String.format("b=%b",b));
		}
		return null;
	}

}
