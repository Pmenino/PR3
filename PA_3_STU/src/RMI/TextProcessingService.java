package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TextProcessingService extends Remote {
	
	public int establishConnection () throws RemoteException;
	public void digestLine (int id, String line) throws RemoteException;
	public String getOneResult (int id) throws RemoteException;
}


