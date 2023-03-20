package RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TextProcessingServer extends UnicastRemoteObject implements TextProcessingService {

	public static void main (String [] args) throws Exception {
		Registry registry = LocateRegistry.createRegistry(1999);
		registry.bind("TEXT", new TextProcessingServer());
		System.out.println("Text proc service running at port 1999");
	}
	
	/* COMPLETE */
	
	
}
