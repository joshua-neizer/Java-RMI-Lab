import java.rmi.*;

// General interface for the RMI Central Server
public interface Central_Interface extends Remote {
    int request (String client_name, int request) throws RemoteException;
}