// Client 1 program
public class Client_1 {

    // Instantiates static variables
    private static int[] requests = new int[] {7, 0, 1, 2, 4, 3, 0, 6, 2, 3, 0, 5, 2, 1, 0, 5, 1, 7, 0, 2};
    // private static int[] requests = new int[] {3, 1, 4, 2, 3, 6, 7, 0, 1, 3, 6, 4, 2, 0, 1, 3, 2, 7, 0, 1};
    // private static int[] requests = new int[] {7, 0, 1, 2, 4, 3};
    private static int response;

    /**
    * Method puts itself to sleep waiting for the other client to awaken it
    */ 
    private static void sleep_socket(){
        System.out.println("Client_1> Waiting for Client_2...");
        // Goes to sleep waiting for the other client to respond
        Socket_IO.socket_in("Client_1", "Client_2");
        System.out.println("Client_1> Client_2 done, now executing...");
    }


    /**
    * Method awakens the other client and alerts it that it can execute
    */ 
    private static void awaken_socket(){
        System.out.println("Client_1> Finished request, awaking Client_2...");
        // Awakens the other client with the socket message
        Socket_IO.socket_out("Client_1", "Client_2", 0);
    }


    /**
    * Connects to a server and sends a request
    *
    * @param  server   a String of the server name
    * @param  request  an integer of the client's request  
    */ 
    private static void connect_server(String server, int request){
        Socket_IO.socket_out("Client_1", server, request);
        response = Socket_IO.socket_in("Client_1", server);
        System.out.printf("Client_1> Received response from %s: %d\n", server, response);
    }
    

	/**
    * Client_1 main function
    *
    * @param  args   command line input String array    
    */ 
    public static void main(String[] args) { 
        // Iterates over all of the requests
        for (int request : requests){

            // Connects to each server and makes its request
            connect_server("FIFO_Server", request);
            connect_server("LFU_Server", request);
            connect_server("MFU_Server", request);

            // After making it's request it awakes the other client and goes
            // to sleep
            awaken_socket();

            sleep_socket();
        } 

        System.out.println("Client_1> Done executing");
    }  
}
