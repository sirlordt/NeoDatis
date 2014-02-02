package org.neodatis.odb.core.transaction;

import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;

public interface ITransaction {

	/** clear the transaction*/
	public abstract void clear();

	public abstract String getName();

	public abstract boolean isCommited();

	public abstract void rollback();

	/** Execute the commit process of the transaction
	 * 
	 * @throws Exception
	 */
	public abstract void commit();

	public abstract void setFsiToApplyWriteActions(IFileSystemInterface fsi);

	/**
	 * @return Returns the archiveLog.
	 */
	public abstract boolean isArchiveLog();

	/**
	 * @param archiveLog
	 *            The archiveLog to set.
	 */
	public abstract void setArchiveLog(boolean archiveLog);

	/**
	 * The public method to add a write action to the transaction. If first checks if the new write action action can be appended to the current write action. 
	 * It is done by checking the currentWritePositioninWA. If yes (position==currentPositioninWA, just append the WA. If not, adds the current one to the transaction and creates a new one (as current)
	 * @param position
	 * @param bytes
	 */
	public abstract void manageWriteAction(long position, byte[] bytes);

	/**
	 * @return Returns the numberOfWriteActions.
	 */
	public int getNumberOfWriteActions();

	/**
	 * Set the write position (position in main database file). This is used to know if the next write can be 
	 * appended to the previous one (in the same current Write Action) or not. 
	 * @param position
	 */
	public void setWritePosition(long position);

	/** Reset the transaction
	 * 
	 */
	public void reset();

}