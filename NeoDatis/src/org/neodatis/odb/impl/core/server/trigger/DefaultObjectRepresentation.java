package org.neodatis.odb.impl.core.server.trigger;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.impl.core.layers.layer1.introspector.DefaultInstrumentationCallback;

public class DefaultObjectRepresentation extends Observable implements ObjectRepresentation {
	private final NonNativeObjectInfo nnoi;
	private Map<String, Object> changedValues;

	public DefaultObjectRepresentation(NonNativeObjectInfo nnoi){
		this.nnoi = nnoi;
		this.changedValues = new HashMap<String, Object>();
	}
	//neodatisee
	public Object getValueOf(String attributeName) {
		if(nnoi.isNull()){
			throw new ODBRuntimeException(NeoDatisError.TRIGGER_CALLED_ON_NULL_OBJECT.addParameter(nnoi.getClassInfo().getFullClassName()).addParameter(attributeName));
		}
		AbstractObjectInfo aoi = nnoi.getMetaValueOf(attributeName);
		
		if(aoi==null || aoi.isNull()){
			return null;
		}
		if(aoi.isAtomicNativeObject()){
			return aoi.getObject();
		}
		if(aoi.isNonNativeObject()){
			return new DefaultObjectRepresentation((NonNativeObjectInfo) aoi);
		}
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("getValueOf for " + aoi.getOdbType().getName()));
		
	}

	public void setValueOf(String attributeName, Object value) {
		//fixme : storage engine is null?
		IObjectIntrospector introspector = OdbConfiguration.getCoreProvider().getLocalObjectIntrospector(null);
		AbstractObjectInfo aoi = introspector.getMetaRepresentation(value, null, true, null, new DefaultInstrumentationCallback());
		nnoi.setValueOf(attributeName, aoi);
		changedValues.put(attributeName, value);
		setChanged();
		this.notifyObservers(new ChangedValueNotification(nnoi,nnoi.getOid(),attributeName,value));
	}
	public OID getOid() {
		return this.nnoi.getOid();
	}
	public String getObjectClassName() {
		return nnoi.getClassInfo().getFullClassName();
	}
	public final NonNativeObjectInfo getNnoi() {
		return nnoi;
	}
	public Map<String, Object> getChangedValues(){
		return changedValues;
	}

}
