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
package org.neodatis.odb.test.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.neodatis.tool.DLogger;

public class TestKeepAliveSocket implements Runnable {
	public static final int port = 9000;
	private ServerSocket socketServer;

	public void run() {
		try {
			executeServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void executeServer() throws IOException {
		socketServer = new ServerSocket(port);
		Socket connection = null;
		DLogger.info("Test Server:Starting ODB Server on port " + port);

		while (true) {
			try {
				connection = socketServer.accept();
				ConnectionThread ct = new ConnectionThread(connection);
				ct.start();
			} catch (SocketException e) {
			}
		}
	}

	public void runClient() throws UnknownHostException, IOException, InterruptedException {
		ClientThread ct1 = new ClientThread();
		ClientThread ct2 = new ClientThread();
		ct1.start();
		ct2.start();
	}

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		TestKeepAliveSocket t = new TestKeepAliveSocket();
		Thread th = new Thread(t);
		th.start();
		Thread.sleep(1000);

		t.runClient();
	}
}

class ConnectionThread extends Thread {
	private OutputStream out;
	private InputStream in;

	private Socket socket;

	public ConnectionThread(Socket socket) throws IOException {
		this.socket = socket;
		System.out.println("New Thread");
	}

	public void run() {

		try {
			out = socket.getOutputStream();
			in = socket.getInputStream();

			while (true) {
				int i = in.read();
				System.out.println("server received : " + i);
				out.write(i + 1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ClientThread extends Thread {
	public void run() {
		OutputStream out;
		InputStream in;
		BufferedOutputStream oos;
		BufferedInputStream ois;

		try {
			Socket socket = new Socket("localhost", TestKeepAliveSocket.port);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			// oos = new BufferedOutputStream(out);
			// ois = new BufferedInputStream(in);

			System.out.println("Starting client");
			int i = 1;
			while (true) {
				out.write(i++);
				System.out.println("client receiveing : " + in.read());
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}