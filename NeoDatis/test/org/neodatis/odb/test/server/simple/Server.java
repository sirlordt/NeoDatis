package org.neodatis.odb.test.server.simple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4444);
			System.out.println("Server started");
		} catch (IOException e) {
			System.out.println("Could not listen on port: 4444");
			System.exit(-1);
		}
		Socket socket = null;
		while (true) {
			socket = serverSocket.accept();
			Thread t = new Thread(new SocketManager(serverSocket, socket));
			t.start();
		}

	}

}

class SocketManager implements Runnable {
	private Socket socket;
	private ServerSocket serverSocket;

	public SocketManager(ServerSocket serverSocket, Socket socket) {
		this.socket = socket;
		try {
			this.socket.setTcpNoDelay(true);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverSocket = serverSocket;
	}

	public void run() {

		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());

			MyObject o1 = (MyObject) in.readObject();

			if (o1.getName().length() == 0) {
				serverSocket.close();
			} else {
				o1.setName(">" + o1.getName());
				out.writeObject(o1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
