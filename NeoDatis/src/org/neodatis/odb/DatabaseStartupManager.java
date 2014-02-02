/**
 * 
 */
package org.neodatis.odb;

/**
 * @author olivier
 * An interface to implement a startup manager. A startup manager will be call on each database open
 *
 */
public interface DatabaseStartupManager {
	void start(ODB odb);

}
