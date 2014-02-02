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
package org.neodatis.odb.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.tool.wrappers.OdbString;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class IOLogSearcher {
	private long start;
	private long end;
	private List result;

	public IOLogSearcher(long start, long end) {
		this.start = start;
		this.end = end;
		result = new ArrayList(10000);
	}

	public void search(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String line = null;
		do {
			line = in.readLine();
			if (line != null) {
				manageOneLine(line);
			}
		} while (line != null);
	}

	public void manageOneLine(String line) {
		if (!line.startsWith("writing")) {
			return;
		}
		String[] array = OdbString.split(line, " ");
		String type = array[1];
		String data = array[2];
		String position = array[4];
		long nposition = Long.parseLong(position);

		if (nposition >= start && nposition <= end) {
			// result.add(line);
			System.out.println(line);
		}
	}

}
