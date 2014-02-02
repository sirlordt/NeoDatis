package org.neodatis.odb.test.ee2.powerloss;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.neodatis.odb.impl.core.layers.layer3.engine.DefaultByteArrayConverter;

public class Reader {
	public static void main(String[] args) throws IOException {
		new Reader().run3();
	}
	public void run3() throws IOException {
		DefaultByteArrayConverter c = new DefaultByteArrayConverter();
		RandomAccessFile raf = null;
		byte[] bytes = new byte[1000];
		
		
		raf = new RandomAccessFile("test.neodatis", "rw");
		int size = raf.read(bytes);
		raf.close();
		for(int i=0;i<size/4;i++){
			int j = c.byteArrayToInt(bytes, i*4);
			System.out.println(j);
		}
	}
}
