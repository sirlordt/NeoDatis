/**
 * 
 */
package org.neodatis.odb.impl.core.layers.layer1.introspector;

import org.neodatis.odb.core.layers.layer1.introspector.IIntrospectionCallback;

/**
 * @author olivier
 * 
 */
public class DefaultInstrumentationCallback implements IIntrospectionCallback {

	public DefaultInstrumentationCallback() {
		super();
	}

	public boolean objectFound(Object object) {
		return true;
	}

}
