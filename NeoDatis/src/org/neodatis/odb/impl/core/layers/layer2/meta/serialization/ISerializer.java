package org.neodatis.odb.impl.core.layers.layer2.meta.serialization;

public interface ISerializer {
	public String toString(Object object);

	public Object fromString(String data) throws Exception ;

}
