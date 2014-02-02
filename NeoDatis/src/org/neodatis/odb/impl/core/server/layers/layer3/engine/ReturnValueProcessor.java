/**
 * 
 */
package org.neodatis.odb.impl.core.server.layers.layer3.engine;

import org.neodatis.odb.impl.core.server.ReturnValue;

/**
 * @author olivier
 *
 */
public interface ReturnValueProcessor {
	void process(ReturnValue rv, Object object) throws Exception;
}
