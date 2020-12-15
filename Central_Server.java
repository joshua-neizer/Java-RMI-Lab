import java.rmi.*;
import java.rmi.registry.*;

// Central Server program
class Central_Server extends java.rmi.server.UnicastRemoteObject implements Central_Interface
{
	/**
    * Central Server constructor
    */ 
	public Central_Server() throws RemoteException
	{
		System.out.println("Central_Server> Starting up ...");
    }
	
	/**
    * Request handler for when a client request is made
    *
    * @param  client_name   the String of the client name
    * @param  request       the integer client request
    * @return               an integer of the square of the request
    */ 
    public int request(String client_name, int request) throws RemoteException {
        System.out.printf("Central_Server> Page Swap request received on %d, swapped in and sending to %s...\n", 
        request, client_name);

        return request;
    }
	
	
	/**
    * Central Server main function
    *
    * @param  args   command line input String array    
    */ 
	public static void main (String args[]) {
		try {
			
            Central_Server S = new Central_Server();
 			//Register the server and bind it to a name
			Registry R = LocateRegistry.createRegistry(1099);
 			System.out.println("Central_Server> Ready");
 			R.rebind("Central_Server", S);
		} catch(Exception e){}
	}
}
