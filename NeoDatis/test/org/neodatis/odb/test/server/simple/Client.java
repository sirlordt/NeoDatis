package org.neodatis.odb.test.server.simple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.neodatis.tool.wrappers.OdbTime;

public class Client {
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		int size = 1000000;

		int nb = 40;
		long start = 0;
		long end = 0;
		long total = 0;
		double duration = 0;
		for (int i = 0; i < nb; i++) {
			start = OdbTime.getCurrentTimeInMs();
			MyObject o = oneConnection(size);
			end = OdbTime.getCurrentTimeInMs();
			duration = end - start;
			total += duration;
			System.out.println("i=" + (i + 1) + ": object=" + o.getName().length() + " - time=" + duration + "ms");
		}
		System.out.println("media=" + (total / nb));
		oneConnection(-1);

	}

	public static MyObject oneConnection(int size) throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket("localhost", 4444);
		socket.setTcpNoDelay(true);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

		MyObject o1 = buildObject(size);
		out.writeUnshared(o1);
		if (o1.getName().length() == 0) {
			return null;
		}
		MyObject o2 = (MyObject) in.readObject();
		out.close();
		in.close();
		socket.close();
		return o2;
	}

	private static MyObject buildObject(int size) {
		if (size == -1) {
			return new MyObject("", "");
		}
		return new MyObject(builsString(size), "street" + size);
	}

	private static String builsString(int size) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < size; i++) {
			b.append("á");
		}
		return b.toString();
	}
}
