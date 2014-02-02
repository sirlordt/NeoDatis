/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test.vo.crawler.jdk5;

import org.neodatis.tool.wrappers.OdbTime;

/**
 * Created by IntelliJ IDEA. User: olivier s Date: 04/12/2005 Time: 19:19:19 To
 * change this template use File | Settings | File Templates.
 */
public class SearchElement {
	// private static final byte DEFAULT_INTERVAL =
	// (byte)CrawlerConf.get().getInt("default.fetch.interval", 30);

	private long id;
	private byte version;
	private String url;
	private long nextFetch = OdbTime.getCurrentTimeInMs();
	private byte retries;
	private byte fetchInterval = 30;
	private int numOutlinks;
	private float score = 1.0f;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getNextFetch() {
		return nextFetch;
	}

	public void setNextFetch(long nextFetch) {
		this.nextFetch = nextFetch;
	}

	public byte getRetries() {
		return retries;
	}

	public void setRetries(byte retries) {
		this.retries = retries;
	}

	public byte getFetchInterval() {
		return fetchInterval;
	}

	public void setFetchInterval(byte fetchInterval) {
		this.fetchInterval = fetchInterval;
	}

	public int getNumOutlinks() {
		return numOutlinks;
	}

	public void setNumOutlinks(int numOutlinks) {
		this.numOutlinks = numOutlinks;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}
