package org.neodatis.odb.core.layers.layer2.instance;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public interface IInstanceBuilder {

	/**
	 * Builds a Non Native Object instance TODO Perf checks the IFs Builds a non native object using The object info
	 * 
	 * @param objectInfo
	 * @return The instance
	 * @
	 */
	public abstract Object buildOneInstance(NonNativeObjectInfo objectInfo) ;

	/** Returns the session id of this instance builder (odb database identifier)
	 * 
	 * @return
	 */
	public String getSessionId();
	/** To specify if instance builder is part of local StorageEngine. In server mode, for instance, when called on 
	 * the server, it will return false
	 * 
	 * @return
	 */
	public boolean isLocal();
}