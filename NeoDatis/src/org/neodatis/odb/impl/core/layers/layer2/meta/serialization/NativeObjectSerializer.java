package org.neodatis.odb.impl.core.layers.layer2.meta.serialization;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.impl.tool.ObjectTool;
import org.neodatis.tool.wrappers.OdbString;

public class NativeObjectSerializer implements ISerializer {

	public static final String classId = Serializer.getClassId(NativeObjectInfo.class);

	public Object fromString(String data) throws Exception {
		String[] tokens = OdbString.split(data,Serializer.FIELD_SEPARATOR);

		if (!tokens[0].equals(classId)) {
			throw new ODBRuntimeException(NeoDatisError.SERIALIZATION_FROM_STRING.addParameter(classId).addParameter(tokens[0]));
		}
		int odbTypeId = Integer.parseInt(tokens[1]);
		Object o = ObjectTool.stringToObjectInfo(odbTypeId, tokens[2], ObjectTool.ID_CALLER_IS_SERIALIZER,null);
		return o;
	}

	public String toString(Object object) {
		AtomicNativeObjectInfo anoi = (AtomicNativeObjectInfo) object;
		StringBuffer buffer = new StringBuffer();
		// TODO escape ;
		buffer.append(classId).append(Serializer.FIELD_SEPARATOR);
		buffer.append(anoi.getOdbTypeId()).append(Serializer.FIELD_SEPARATOR);
		buffer.append(ObjectTool.atomicNativeObjectToString(anoi,ObjectTool.ID_CALLER_IS_SERIALIZER));
		return buffer.toString();
	}

}
