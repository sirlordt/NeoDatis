/**
 * 
 */
package org.neodatis.odb.test.buffer;

import java.nio.ByteBuffer;

import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.impl.core.layers.layer3.engine.DefaultByteArrayConverter;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier to test performance of java.nio.ByteBuffer against byte[]
 * 
 */
public class ByteBufferTest extends ODBTest {
	public void test1() {
		ByteBuffer buffer = ByteBuffer.allocate(1000);
		byte b1 = 1;
		int i1 = 10;
		buffer.put(b1);
		buffer.putInt(i1);

		buffer.rewind();

		assertEquals(b1, buffer.get());
		assertEquals(i1, buffer.getInt());
	}

	public void test2Perf() {
		int size = 1000000;
		IByteArrayConverter byteArrayConverter = new DefaultByteArrayConverter();

		long startBuffer = System.currentTimeMillis();
		ByteBuffer buffer = ByteBuffer.allocate(size * 8);
		for (int i = 0; i < size; i++) {
			long l = i;
			buffer.putLong(l);
		}
		buffer.rewind();
		for (int i = 0; i < size; i++) {
			long l = i;
			assertEquals(l, buffer.getLong());
		}
		long endBuffer = System.currentTimeMillis();

		long startArray = System.currentTimeMillis();
		byte[] bytes = new byte[size * 8];
		for (int i = 0; i < size; i++) {
			long l = i;
			byte[] longBytes = byteArrayConverter.longToByteArray(l);
			System.arraycopy(longBytes, 0, bytes, i * 8, 8);
		}
		for (int i = 0; i < size; i++) {
			long l = i;
			long l2 = byteArrayConverter.byteArrayToLong(bytes, i * 8);

			assertEquals(l, l2);
		}
		long endArray = System.currentTimeMillis();

		println("time with ByteBuffer=" + (endBuffer - startBuffer));
		println("time with byte array=" + (endArray - startArray));

	}

}
