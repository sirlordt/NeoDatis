/**
 * 
 */
package org.neodatis.odb.core.trigger;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;

/**
 * @author olivier
 *
 */
public abstract class OIDTrigger extends Trigger{
	abstract public void setOid(final ObjectRepresentation o, OID oid);
}
