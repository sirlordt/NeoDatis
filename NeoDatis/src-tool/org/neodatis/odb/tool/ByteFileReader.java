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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.layers.layer3.buffer.MultiBufferedFileIO;


/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class ByteFileReader {

	public static void main3(String[] args) throws IOException {

		RandomAccessFile raf = new RandomAccessFile("1141067269187.transaction", "r");

		long length = raf.length();
		System.out.println("File length = " + length);

		for (int i = 0; i < length; i++) {
			System.out.println(i + ":\t" + raf.read());
		}
		raf.close();
	}

	public static void main7(String[] args) throws IOException {

		MultiBufferedFileIO braf = new MultiBufferedFileIO(1, "data", "1141067269187.transaction", false, OdbConfiguration
				.getDefaultBufferSizeForData());

		long length = braf.getLength();
		System.out.println("File length = " + length);

		for (int i = 0; i < length; i++) {
			System.out.println(i + "\t:" + braf.readByte());
		}
		braf.close();
	}

	public static void main(String[] args) throws IOException {

		MultiBufferedFileIO braf = new MultiBufferedFileIO(1, "data", "C:/o/myProjects/NeoDatis/odb/java/test-xml-imp.odb", false,
				OdbConfiguration.getDefaultBufferSizeForData());
		byte[] bytes = braf.readBytes(8);

		long length = braf.getLength();
		System.out.println("File length = " + length);

		for (int i = 0; i < length; i++) {
			System.out.println(i + "\t\t: b=" + ByteBuffer.wrap(bytes).get() + "\ti=" + ByteBuffer.wrap(bytes).getInt() + " \tl="
					+ ByteBuffer.wrap(bytes).getLong());

			int b = braf.readByte();
			bytes = shift(bytes);
			bytes[7] = (byte) b;

		}
		braf.close();
	}

	public static byte[] shift(byte[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			array[i] = array[i + 1];
		}
		return array;
	}

}
