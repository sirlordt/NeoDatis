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

import java.util.ArrayList;
import java.util.List;

public class XMLNode {
	private String name;
	private List<XMLAttribute> attributes;
	private List<XMLNode> nodes;
	private boolean isRoot;
	private boolean isDone;
	private XMLNode currentChild;
	private int nodeLevel;
	boolean headerHasBeenWritten;

	XMLNode(String name, boolean isRoot) {
		this.name = name;
		attributes = new ArrayList<XMLAttribute>();
		nodes = new ArrayList<XMLNode>();
		this.isRoot = isRoot;
		this.isDone = false;
		this.nodeLevel = 0;
	}

	public XMLNode addAttribute(String name, String value) {
		attributes.add(new XMLAttribute(name, value));
		return this;
	}

	public XMLNode createNode(String name) {
		if (currentChild != null) {

			boolean canRemoveNode = true;

			if (!currentChild.isDone) {
				canRemoveNode = currentChild.end();
			}
			if (canRemoveNode) {
				currentChild.attributes.clear();
				currentChild.nodes.clear();
				nodes.remove(currentChild);
				currentChild = null;
			}
		}
		currentChild = new XMLNode(name, false);
		currentChild.nodeLevel = this.nodeLevel + 1;
		nodes.add(currentChild);
		return currentChild;
	}

	public void endHeader() {
		XMLGenerator.startOfNode(name, this);
		headerHasBeenWritten = true;
	}

	public boolean end() {
		isDone = true;
		/*
		 * if(!headerHasBeenWritten){ endHeader(); }
		 */
		boolean canRemoveNode = XMLGenerator.endOfNode(name, this);
		if (isRoot) {
			XMLGenerator.endOfDocument(name);
		}
		return canRemoveNode;
	}

	public String headerToString(boolean closeTag) {

		XMLAttribute attribute = null;
		StringBuffer buffer = new StringBuffer(1000);
		String spaces = buildSpaces(getNodeLevel());
		buffer.append("\n").append(spaces).append("<").append(name).append(" ");
		for (int i = 0; i < attributes.size(); i++) {
			if (i != 0) {
				buffer.append(" ");
			}
			attribute = (XMLAttribute) attributes.get(i);
			buffer.append(attribute.toString());
		}
		if (closeTag) {
			buffer.append("/>");
		} else {
			buffer.append(">");
		}
		return buffer.toString();
	}

	public boolean hasChildren() {
		return !nodes.isEmpty();
	}

	public String contentToString() {
		XMLNode node = null;

		StringBuffer buffer = new StringBuffer(5000);

		for (int i = 0; i < nodes.size(); i++) {
			node = (XMLNode) nodes.get(i);
			buffer.append("\n");
			buffer.append(node.toString());
		}

		return buffer.toString();
	}

	public String footerToString() {
		String spaces = buildSpaces(getNodeLevel());

		StringBuffer buffer = new StringBuffer(5000);
		buffer.append("\n").append(spaces).append("</").append(name).append(">");
		return buffer.toString();

	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(5000);
		buffer.append(headerToString(false));
		buffer.append(contentToString());
		buffer.append(footerToString());
		return buffer.toString();
	}

	String buildSpaces(int level) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < level; i++) {
			buffer.append("  ");
		}
		return buffer.toString();
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the nodeLevel.
	 */
	public int getNodeLevel() {
		return nodeLevel;
	}

}
