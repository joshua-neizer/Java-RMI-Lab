import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.Map;

// Socket IO Communication for other classes to utilize
public class Socket_IO {

    // Instantiates the variables
    public static ObjectInputStream in;
    public static ObjectOutputStream out;
    public static ServerSocket ss;
    public static Socket sc, ssc;
    public static int request_in;

    // A HashMap storing port numbers associated with each socket host/client
    public static final Map<String, Integer> socket_port = Map.of(
        "Client_1", 12345,
        "Client_2", 12346,
        "FIFO_Server", 12347,
        "LFU_Server", 12348,
        "MFU_Server", 12349
    );

    /**
    * Sends a message from a host socket to the client socket
    *
    * @param  socket_host   the String name of the host socket
    * @param  socket_client   the String name of the client socket    
    * @param  request_out   the integer request from the host socket    
    */ 
    public static void socket_out(String socket_host, String socket_client, int request_out){
        try {
            sc = new Socket("127.0.0.1", socket_port.get(socket_client));
            System.out.printf("%s> Connected to %s on port: %d\n", socket_host, socket_client, socket_port.get(socket_client));
            out = new ObjectOutputStream(sc.getOutputStream());
            out.writeObject(Integer.toString(request_out));
            out.flush();
            System.out.printf("%s> Sending %d to %s ...\n", socket_host, request_out, socket_client);
            out.close();			
            sc.close();
        } catch (Exception e){
            System.out.println("___FAILED___");
            System.err.println("Client exception: " + e.toString()); 
            e.printStackTrace(); 
            System.exit(1);
        }
    }

    /**
    * Prepares to receive a message from a client socket to the host socket and 
    * returns the message.
    *
    * @param  socket_host   the String name of the host socket
    * @param  socket_client   the String name of the client socket   
    * @return               an integer of the received message
    */ 
    public static int socket_in(String socket_host, String socket_client){
        try {
            //Waiting for connection from thread 2
            ss = new ServerSocket(socket_port.get(socket_host));
            System.out.printf("%s> Waiting for connection from %s ...\n", socket_host, socket_client);
            ssc = ss.accept();
            System.out.printf("%s> Connection established with %s ...\n", socket_host, socket_client);
            //receiving integer
            in = new ObjectInputStream(ssc.getInputStream());
            request_in = Integer.parseInt((String) in.readObject());
            in.close();
            ss.close();

            return request_in;
        } catch (Exception e){
            System.out.println("___FAILED___");
            System.err.println("Client exception: " + e.toString()); 
            e.printStackTrace(); 
            System.exit(1);
        }

        // If an error occurs, -1 is returned
        return -1;
    }
}
