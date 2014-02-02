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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.tool.wrappers.io.OdbFile;

public class XMLGenerator {
	private static List<NodeEventListener> listeners = new ArrayList<NodeEventListener>();
	private static String incrementalFileName;
	private static boolean writeIncremental;
	private static Writer incrementalWriter;
	private static boolean firstNode = true;

	public static void addListener(NodeEventListener listener) {
		listeners.add(listener);
	}

	public static void setIncrementalWriteOn(String fileName) throws IOException {
		incrementalFileName = fileName;
		writeIncremental = true;
		incrementalWriter = getWriter(fileName);
	}

	public static void end() throws IOException {
		if (writeIncremental && incrementalWriter != null) {
			incrementalWriter.close();
		}
	}

	public static XMLNode createRoot(String name) {

		XMLNode node = new XMLNode(name, true);
		startOfDocument(name);

		return node;
	}

	public static void startOfDocument(String name) {
		NodeEventListener listener = null;
		if (writeIncremental) {
			try {
				incrementalWriter.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
				incrementalWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ODBRuntimeException(NeoDatisError.XML_HEADER, e);
			}

		}
		for (int i = 0; i < listeners.size(); i++) {
			listener = (NodeEventListener) listeners.get(i);
			listener.startOfDocument();
		}
	}

	public static void endOfDocument(String name) {
		NodeEventListener listener = null;
		for (int i = 0; i < listeners.size(); i++) {
			listener = (NodeEventListener) listeners.get(i);
			listener.endOfDocument();
		}
	}

	public static void startOfNode(String name, XMLNode node) {
		NodeEventListener listener = null;
		for (int i = 0; i < listeners.size(); i++) {
			listener = (NodeEventListener) listeners.get(i);
			listener.startOfNode(name, node);
		}
		if (writeIncremental) {
			try {
				writeIncrementalNodeHeader(node, false);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public static boolean endOfNode(String name, XMLNode node) {
		NodeEventListener listener = null;
		for (int i = 0; i < listeners.size(); i++) {
			listener = (NodeEventListener) listeners.get(i);
			listener.endOfNode(name, node);
		}
		if (writeIncremental) {
			try {
				if (!node.headerHasBeenWritten) {
					if (!node.hasChildren()) {
						writeIncrementalNodeHeader(node, true);
						return true;
					}
				}
				writeIncrementalNodeFooter(node);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return false;
	}

	private static void writeIncrementalNodeHeader(XMLNode node, boolean closeTag) throws IOException {
		incrementalWriter.write(node.headerToString(closeTag));
		incrementalWriter.flush();
	}

	private static void writeIncrementalNodeFooter(XMLNode node) throws IOException {
		incrementalWriter.write(node.footerToString());
		incrementalWriter.flush();
	}

	public static void writeNodeToFile(XMLNode node, String fileName) throws IOException {
		Writer writer = getWriter(fileName);
		writer.write(node.toString());
		writer.close();
	}

	private static Writer getWriter(String fileName) throws IOException {
		OdbFile f = new OdbFile(fileName);
		if (!f.exists()) {
			if (f.getParentFile() != null) {
				f.getParentFile().mkdirs();
			}
		}
		FileOutputStream out = new FileOutputStream(fileName);
		OutputStreamWriter writer = null;

		if (OdbConfiguration.hasEncoding()) {
			writer = new OutputStreamWriter(out, OdbConfiguration.getDatabaseCharacterEncoding());
		} else {
			writer = new OutputStreamWriter(out);
		}
		return writer;
	}

	public static void close() throws IOException {
		if (incrementalWriter != null) {
			incrementalWriter.close();
		}
	}
}
