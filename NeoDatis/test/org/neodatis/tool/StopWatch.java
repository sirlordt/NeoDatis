package org.neodatis.tool;

import java.util.Date;

/**
 * A simple timer to get task duration. you make start. End when ends your task.
 * Then you can get te duration by using getDurationinMseconds
 * getDurationInSeconds
 * 
 * @author olivier smadja
 * @version 03/09/2001 - creation
 */

public class StopWatch {
	/** The start date time in ms */
	private long start;

	/** The end date time in ms */
	private long end;

	/** Constructor */
	public StopWatch() {
		start = 0;
		end = 0;
	}

	/** Mark the start time */
	public void start() {
		start = new Date().getTime();
	}

	/** Mark the end time */
	public void end() {
		end = new Date().getTime();
	}

	/**
	 * gets the duration in mili seconds
	 * 
	 * @return long The duration in ms
	 */
	public long getDurationInMiliseconds() {
		return end - start;
	}

	/**
	 * gets the duration in seconds
	 * 
	 * @return long The duration in seconds
	 */
	public long getDurationInSeconds() {
		return (end - start) / 1000;
	}

	/**
	 * string description of the object
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer sResult = new StringBuffer();
		sResult.append("Start = ").append(start).append(" / End = ").append(end).append(" / Duration(ms) = ").append(
				getDurationInMiliseconds());
		return sResult.toString();
	}
}