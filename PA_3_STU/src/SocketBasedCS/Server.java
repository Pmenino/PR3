package SocketBasedCS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {

	private Socket connection;
    private BufferedReader inputChannel;
    private PrintWriter outputChannel;
    private Map<String, Integer> words; // yes, this is a clue...
	
	public static void main(String[] args) throws IOException {

		Socket connection;
		ServerSocket serverSocket = new ServerSocket(4445);
		System.out.println("Accepting incoming connections on port 4445");

		while (true) {
			connection = serverSocket.accept();
			new Server(connection).start();
		}
	}
	
	// constructor
	
	public Server (Socket connection) {
		this.connection = connection;
	}
	
	/* COMPLETE */
	
}

