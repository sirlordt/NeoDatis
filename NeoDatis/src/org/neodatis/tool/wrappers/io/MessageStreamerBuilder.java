package org.neodatis.tool.wrappers.io;

import java.lang.reflect.Constructor;
import java.net.Socket;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.layers.layer3.engine.IMessageStreamer;
/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class MessageStreamerBuilder {
	/** (non-Javadoc)
	 * @see org.neodatis.odb.core.ICoreProvider#getMessageStreamer(java.net.Socket)
	 * 
	 */
	public static IMessageStreamer getMessageStreamer(Socket socket) {
		Class clazz = null;
		try{
			clazz = OdbConfiguration.getMessageStreamerClass();
			Constructor c = clazz.getDeclaredConstructor(Socket.class);
			
			IMessageStreamer messageStreamer = (IMessageStreamer) c.newInstance(socket);
			return messageStreamer;
		}catch (Exception e) {
			String streamerClassName = "<null>";
			if(clazz!=null){
				streamerClassName = clazz.getName();
			}
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_CREATING_MESSAGE_STREAMER.addParameter(streamerClassName),e);
		}
	}
	/**
	 * 
	 */
	public static IMessageStreamer getMessageStreamer(String host, int port, String name) {
		Class clazz = null;
		try{
			clazz = OdbConfiguration.getMessageStreamerClass();
			Constructor c = clazz.getDeclaredConstructor(String.class, Integer.TYPE, String.class);
			
			IMessageStreamer messageStreamer = (IMessageStreamer) c.newInstance(host,port,name);
			return messageStreamer;
		}catch (Exception e) {
			String streamerClassName = "<null>";
			if(clazz!=null){
				streamerClassName = clazz.getName();
			}
			throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_CREATING_MESSAGE_STREAMER.addParameter(streamerClassName),e);
		}
	}
	

}
