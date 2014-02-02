package org.neodatis.odb.core.server.layers.layer1;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.tool.wrappers.list.IOdbList;

public interface IClientObjectIntrospector extends IObjectIntrospector {

	public abstract IOdbList<OID> getClientOids();

	public abstract void synchronizeIds(OID[] clientIds, OID[] serverIds);

}