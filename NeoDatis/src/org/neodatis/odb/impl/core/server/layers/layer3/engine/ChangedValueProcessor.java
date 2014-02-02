/**
 * 
 */
package org.neodatis.odb.impl.core.server.layers.layer3.engine;

import java.lang.reflect.Field;

import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.impl.core.server.ReturnValue;
import org.neodatis.odb.impl.core.server.trigger.ChangedValueNotification;

/**
 * Used when a server side trigger has changed a value of an object of the server side
 * @author olivier
 *
 */
public class ChangedValueProcessor implements ReturnValueProcessor {
	protected IClassIntrospector classIntrospector;
	
	public ChangedValueProcessor(IClassIntrospector classIntrospector){
		this.classIntrospector = classIntrospector;
	}

	public void process(ReturnValue rv, Object object) throws Exception {
		// only manage ChangedValueNotification
		if(rv==null || !(rv instanceof ChangedValueNotification)){
			return;
		}
		ChangedValueNotification cvn = (ChangedValueNotification) rv;
		
		// Get the object class
		Class c = object.getClass();
		// Get the field that is to be changed
		Field f = classIntrospector.getField(c, cvn.getAttributeName());
		// Tells java t let us update the field even if it private
		f.setAccessible(true);
		// set the new value
		f.set(object, cvn.getValue());
	}

}
