package SocketBasedCS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

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

	public void run() {
		words = new TreeMap<String, Integer>();
		try {
			this.inputChannel = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        	this.outputChannel = new PrintWriter(connection.getOutputStream(), true);
			innerRun();
		} catch(IOException ioex){}
	}

	private void innerRun() throws IOException {
		for(String line = receiveRequest(); !line.equals("\u001a"); line = receiveRequest()) {
			line = line.toUpperCase();
		 	String [] lineWords = line.split("[\\s!?\"\',;:.-]+");
		 	for(int i = 0; i < lineWords.length; i++) {
		 		if(lineWords[i].length() >= 9){
		 			if(words.containsKey(lineWords[i])){
		 				words.put(lineWords[i], words.get(lineWords[i]) + 1);
		 			}
		 			else{
		 				words.put(lineWords[i], 1);
		 			}
				}
			}
		}
		sendReply(words);
	}

	private String receiveRequest () throws IOException {
        return this.inputChannel.readLine();
    }

	private void sendReply (Map<String, Integer> words) throws IOException {
		for (String s: words.keySet()) {
			this.outputChannel.println(s + " " + words.get(s));
		}
	}
	
}

