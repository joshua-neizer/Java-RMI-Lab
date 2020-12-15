// Client 2 program
public class Client_2 {

    // Instantiates static variables
    private static int[] requests = new int[] {3, 1, 4, 2, 3, 6, 7, 0, 1, 3, 6, 4, 2, 0, 1, 3, 2, 7, 0, 1};
    // private static int[] requests = new int[] {3, 1, 4, 2, 3, 6};
    private static int response;

    /**
    * Method puts itself to sleep waiting for the other client to awaken it
    */ 
    private static void sleep_socket(){
        System.out.println("Client_2> Waiting for Client_1...");
        // Goes to sleep waiting for the other client to respond
        Socket_IO.socket_in("Client_2", "Client_1");
        System.out.println("Client_2> Client_1 done, now executing...");
    }


    /**
    * Method awakens the other client and alerts it that it can execute
    */ 
    private static void awaken_socket(){
        System.out.println("Client_2> Finished request, awaking Client_1...");
        // Awakens the other client with the socket message
        Socket_IO.socket_out("Client_2", "Client_1", 0);
    }


    /**
    * Connects to a server and sends a request
    *
    * @param  server   a String of the server name
    * @param  request  an integer of the client's request  
    */ 
    private static void connect_server(String server, int request){
        Socket_IO.socket_out("Client_2", server, request);
        response = Socket_IO.socket_in("Client_2", server);
        System.out.printf("Client_2> Received response from %s: %d\n", server, response);
    }


    /**
    * Client_2 main function
    *
    * @param  args   command line input String array    
    */ 
    public static void main(String[] args) { 
        // Iterates over all of the requests
        for (int request : requests){
            // Goes to sleep waiting for the other client to awaken it
            sleep_socket();

            // Connects to each server and makes its request
            connect_server("FIFO_Server", request);
            connect_server("LFU_Server", request);
            connect_server("MFU_Server", request);

            // After making it's request it awakes the other client
            awaken_socket();
        }
        
        System.out.println("Client_2> Done executing");
    }  
}
