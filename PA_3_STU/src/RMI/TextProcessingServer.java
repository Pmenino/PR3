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
	private int nextId = 0;
	private TreeMap<Integer, ClientState> clients;
	
	protected TextProcessingServer() throws RemoteException {
		super();
		clients = new TreeMap<Integer, ClientState>();
		this.nextId = 0;
	}

	class ClientState {
		TreeMap<String, Integer> words;
	}
	

	@Override
	public int establishConnection() throws RemoteException {
		int id = nextId;
		ClientState clientState = new ClientState();
		clientState.words = new TreeMap<String, Integer>();
		clients.put(id, clientState);
		nextId++;
		return id;
	}

	@Override
	public void digestLine(int id, String line) throws RemoteException {
		if(id < 0 || !clients.containsKey(id)) throw new RemoteException("Invalid id: " + id);

		TreeMap<String, Integer> words = clients.get(id).words;

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

	@Override
	public String getOneResult(int id) throws RemoteException {
		TreeMap<String, Integer> words = clients.get(id).words;
		
		if(id < 0) throw new RemoteException("Invalid id: " + id);
		if(words.size() > 0){
			String firstEntry = words.firstKey().toString() + " " + words.get(words.firstKey()).toString();
			words.remove(words.firstKey());
			return firstEntry;
		}else{
			return "SERVICE_TERMINATED";
		}
	}
	
	
	
}
