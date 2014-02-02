package org.neodatis.odb.impl.core.layers.layer2.meta.serialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.tool.wrappers.OdbString;

public class CollectionObjectInfoSerializer implements ISerializer {

	public static final String classId = Serializer.getClassId(CollectionObjectInfo.class);

	public Object fromString(String data) throws Exception {
		String[] tokens = OdbString.split(data,Serializer.FIELD_SEPARATOR);

		if (!tokens[0].equals(classId)) {
			throw new ODBRuntimeException(NeoDatisError.SERIALIZATION_FROM_STRING.addParameter(classId).addParameter(tokens[0]));
		}
		String realCollectionName = tokens[1];
		int collectionSize = Integer.parseInt(tokens[2]);
		String collectionData = tokens[3];
		String [] objects = OdbString.split(collectionData,Serializer.COLLECTION_ELEMENT_SEPARATOR);
		if(objects.length!=collectionSize){
			throw new ODBRuntimeException(NeoDatisError.SERIALIZATION_COLLECTION.addParameter(collectionSize).addParameter(objects.length));
		}
		Collection<AbstractObjectInfo> l = new ArrayList<AbstractObjectInfo>(collectionSize);
		for(int i=0;i<collectionSize;i++){
			l.add((AbstractObjectInfo) Serializer.getInstance().fromOneString(objects[i]));
		}
		CollectionObjectInfo coi = new CollectionObjectInfo(l);
		coi.setRealCollectionClassName(realCollectionName);
		return coi;
	}

	public String toString(Object object) {
		CollectionObjectInfo coi = (CollectionObjectInfo) object;
		StringBuffer buffer = new StringBuffer();
		// TODO escape ;
		buffer.append(classId).append(Serializer.FIELD_SEPARATOR);
		buffer.append(coi.getRealCollectionClassName()).append(Serializer.FIELD_SEPARATOR);
		buffer.append(coi.getCollection().size()).append(Serializer.FIELD_SEPARATOR);
		buffer.append(Serializer.COLLECTION_START);
		Iterator iterator = coi.getCollection().iterator();
		while(iterator.hasNext()){
			buffer.append(Serializer.getInstance().toString(iterator.next()));
			if(iterator.hasNext()){
				buffer.append(Serializer.COLLECTION_ELEMENT_SEPARATOR);
			}
		}
		buffer.append(Serializer.COLLECTION_END);
		return buffer.toString();
	}

}
