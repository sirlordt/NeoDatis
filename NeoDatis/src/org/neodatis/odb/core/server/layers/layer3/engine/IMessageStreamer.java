/**
 * 
 */
package org.neodatis.odb.core.server.layers.layer3.engine;



/**
 * @author olivier
 *
 */
public interface IMessageStreamer {

	void close();

	void write(Message message) throws Exception;

	Message read() throws Exception ;
	void clearCache();

}