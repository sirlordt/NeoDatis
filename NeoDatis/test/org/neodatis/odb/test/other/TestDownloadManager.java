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
package org.neodatis.odb.test.other;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.download.Download;
import org.neodatis.odb.test.vo.download.User;

public class TestDownloadManager extends ODBTest {

	public void newDownload(String name, String email, String downloadType, String fileName) throws Exception {
		ODB odb = null;
		User user = null;
		try {
			odb = open("download.neodatis");
			Objects users = odb.getObjects(new CriteriaQuery(User.class, Where.equal("email", email)));

			if (!users.isEmpty()) {
				user = (User) users.getFirst();
				user.setLastDownload(new Date());
				user.setNbDownloads(user.getNbDownloads() + 1);
				odb.store(user);
			} else {
				user = new User();
				user.setName(name);
				user.setEmail(email);
				user.setLastDownload(new Date());
				user.setNbDownloads(1);
				odb.store(user);
			}

			Download download = new Download();
			download.setFileName(fileName);
			download.setType(downloadType);
			download.setUser(user);
			download.setWhen(new Date());
			odb.store(download);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test1() throws Exception {
		TestDownloadManager tdm = new TestDownloadManager();
		tdm.newDownload("olivier", "olivier@neodatis.com", "knowledger", "knowledger1.1");
		tdm.newDownload("olivier", "olivier@neodatis.com", "knowledger", "knowledger1.1");
		ODB odb = open("download.neodatis");
		assertEquals(2, odb.count(new CriteriaQuery(Download.class)).longValue());
		assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
		odb.close();
	}

	public void test2() throws Exception {
		TestDownloadManager tdm = new TestDownloadManager();
		int size = (isLocal ? 1000 : 50);
		for (int i = 0; i < size; i++) {
			tdm.newDownload("olivier", "olivier@neodatis.com", "knowledger", "knowledger1.1");
			tdm.newDownload("olivier", "olivier@neodatis.com", "knowledger", "knowledger1.1");
		}
		ODB odb = open("download.neodatis");
		assertEquals(size * 2, odb.count(new CriteriaQuery(Download.class)).longValue());
		assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
		odb.close();

	}

	public void setUp() throws Exception {
		super.setUp();
		deleteBase("download.neodatis");
	}

	public void tearDown() throws Exception {
		deleteBase("download.neodatis");
	}

	public static void main(String[] args) throws Exception {

		TestDownloadManager td = new TestDownloadManager();
		for (int i = 0; i < 2000; i++) {
			td.setUp();
			td.test1();
			td.tearDown();
			td.test1();
		}
	}
}
