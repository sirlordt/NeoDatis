package org.neodatis.tool.wrappers;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class NeoDatisClassLoader {
	public static ClassLoader getCurrent()
	{
		return NeoDatisClassLoader.class.getClassLoader();
	}

}
