package org.neodatis.odb.impl.core.layers.layer2.meta.serialization;

import java.util.List;
import java.util.Map;

import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class Serializer {
	public static final String COLLECTION_ELEMENT_SEPARATOR = ",";
	public static final String FIELD_SEPARATOR = ";";
	public static final String ATTRIBUTE_SEPARATOR = "|";
	public static final String COLLECTION_START = "(";
	public static final String COLLECTION_END = ")";
	private static Map<String,ISerializer> serializers = null;
	private static Serializer instance = null;
	
	public static synchronized Serializer getInstance(){
		if(instance==null){
			instance = new Serializer();
		}
		return instance;
	}
	
	private Serializer(){
		serializers = new OdbHashMap<String,ISerializer>();
		serializers.put(getClassId(AtomicNativeObjectInfo.class),new AtomicNativeObjectSerializer());
		serializers.put(getClassId(CollectionObjectInfo.class),new CollectionObjectInfoSerializer());
	}
	
	public String toString(List objectList){
		
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<objectList.size();i++){
			buffer.append(toString(objectList.get(i))).append("\n");
		}
		return buffer.toString();
	}
	
	public String toString(Object object){
		
		String classId = getClassId(object.getClass());
		
		ISerializer serializer = serializers.get(classId);
		if(serializer!=null){
			return serializer.toString(object);
		}
		
        throw new RuntimeException("toString not implemented for " + object.getClass().getName());
    }
    
    public ObjectContainer fromString(String data) throws Exception{
      
    	ObjectContainer container = new ObjectContainer();
    	String [] lines = OdbString.split(data,"\n");
    	for(int i=0;i<lines.length;i++){
    		if(lines[i]!=null && lines[i].trim().length()>0){
    			container.add(fromOneString(lines[i]));
    		}
    	}
    	return container;
    }
    
    public Object fromOneString(String data) throws Exception {
    	
        int index = data.indexOf(";");
    	
        if(index==-1){
            return null;
        }
        String type = OdbString.substring(data,0,index);
    	
    	ISerializer serializer = serializers.get(type);
		if(serializer!=null){
			return serializer.fromString(data);
		}
        
		throw new RuntimeException("fromString unimplemented for " + type);
        
    }
    
    public static String getClassId(Class clazz){
    	if(clazz==AtomicNativeObjectInfo.class){
    		return "1";
    	}
    	if(clazz==CollectionObjectInfo.class){
    		return "2";
    	}
    	return "0";
    }
    
}
