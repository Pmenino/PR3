package RMI;

/* DO NOT CHANGE THIS FILE */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Client extends Thread {
	
	public static void main (String [] args) {
		// launcher
		new Client().start();
	}

	public void run () {
		try {
			innerRun();
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void innerRun () throws Exception  {
		BufferedReader fileBR;
		String line;
		String response;
		int id = -100;
		
		System.out.println("RMI based client");
		
		Registry registry = LocateRegistry.getRegistry("localhost", 1999);
		TextProcessingService provider = (TextProcessingService) registry.lookup("TEXT");
		
		id = provider.establishConnection();  // comment this line and you should receive an exception stating that the id is unknown

		JFileChooser fc = new JFileChooser(".");
		fc.showOpenDialog(null);
		File file = fc.getSelectedFile();
		fileBR = new BufferedReader(new FileReader(file));
		
		
		/* STEP ONE send all the lines in the file. Line by line */
		line = fileBR.readLine();
		while (line!=null) {
			// send line to server
			provider.digestLine(id, line);
			System.out.print(".");
			line = fileBR.readLine();
			// simulate some delay
			Thread.sleep(300);
		}
		// eof reached. Send eof...
		provider.digestLine(id, "\u001a");
		fileBR.close();
		System.out.println();
		
		
		
		/* STEP TWO get the reply from the server */
		response = provider.getOneResult(id);
		while (!response.equals("SERVICE_TERMINATED")) {
			System.out.println(response);
			response = provider.getOneResult(id);
		}
		
	}
	
}
