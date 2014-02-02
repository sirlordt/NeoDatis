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
package org.neodatis.odb.xml.tool;

import java.io.IOException;

public class Test implements NodeEventListener {
	public static void main(String[] args) throws IOException {
		System.out.println("building");
		NodeEventListener nel = new Test();
		XMLGenerator.addListener(nel);
		XMLGenerator.setIncrementalWriteOn("testi.xml");
		XMLNode root = XMLGenerator.createRoot("odb");
		root.addAttribute("name", "test.odb").addAttribute("creation-date", "09/05/2006");
		root.endHeader();
		XMLNode metaModel = root.createNode("meta-model").addAttribute("nb-classes", "3");
		metaModel.endHeader();
		for (int i = 0; i < 1000; i++) {
			metaModel.createNode("class").addAttribute("name", "User" + i).end();
			metaModel.createNode("class").addAttribute("name", "Profile" + i).end();
			metaModel.createNode("class").addAttribute("name", "Function" + i).end();
			if (i % 5000 == 0) {
				System.out.println(i);
			}
		}
		metaModel.end();
		root.end();
		// XMLGenerator.writeNodeToFile(root,"test.xml");
		XMLGenerator.end();
		// System.out.println(root.toString());
		System.out.println("done");

	}

	public void startOfDocument() {
		// System.out.println("start of document");
	}

	public void endOfDocument() {
		// System.out.println("end of document");

	}

	public void startOfNode(String nodeName, XMLNode node) {
		// System.out.println("start of node " + nodeName);
	}

	public void endOfNode(String nodeName, XMLNode node) {
		// System.out.println("end of node " + nodeName);
	}
}
