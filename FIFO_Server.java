import java.rmi.*;
import java.rmi.registry.*;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

// Frame Sever that follows the First-In First-Out page replacement policy
class FIFO_Server extends java.rmi.server.UnicastRemoteObject implements Central_Interface {
    
    // Variables used in the FIFO server
    private static int frame_count = 5;
    private int[] frames;
    private int[] FIFO_queue;
    private int occupied_frames;
    private HashMap<String, Integer> fault_count;
    private int request_count;
    private int client_request;
    private int server_response;
    
    // Variables used for the Central Server
    private Central_Interface S_Server;
    private static Registry R_Server;
    
    /**
    * FIFO Server constructor; instantiates variables and connects to the central
    * server.
    */ 
    public FIFO_Server() throws RemoteException {
        // Instantiates stated variables 
        // Frames array starts of with all -1 as to not confuse with client requests
        this.frames = new int[frame_count];
        this.FIFO_queue = new int[frame_count];
        this.occupied_frames = 0;
        this.request_count = 0;
        this.fault_count = new HashMap<String, Integer>();

        // Tries to connect to the Central Server
        System.out.println("FIFO_Server> Starting up ...");
        try {
            //locate a server which IP address is 127.0.0.1 (localhost) and which is listening on port 1099
            R_Server = LocateRegistry.getRegistry("127.0.0.1", 1099);
            //Using the server name CISC324, obtain a reference of the remote object (server) 
            S_Server = (Central_Interface) (R_Server.lookup("Central_Server"));
        } catch (Exception e){
            // Error handling on failed connection
            System.out.println("___FAILED___");
            System.err.println("Client exception: " + e.toString()); 
            e.printStackTrace(); 
            System.exit(1);
        }
    }


    /**
    * Returns a String of the current frames in the server
    *
    * @return      the string of the current frames in the server
    */
    private String print_frames() throws RemoteException {
        // Uses collectors to concatenate and return int array as a string
        return "[" + Arrays.stream(this.frames)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(" | ")) 
                + "]";
    }


    /**
    * Returns a boolean depending on whether the current request triggers a page
    * fault
    *
    * @param  request   the integer client request
    * @return           boolean of whether a page fault has occurred
    */ 
    private boolean isPageFault(int request) throws RemoteException {
        // Iterates through all of the frames to find if the current request
        // already exists in the frames
        for (int i = 0; i < this.occupied_frames; i++){
            if (this.frames [i] == request){
                return false;
            }
        }

        return true;
    }


    /**
    * Frame replacement algorithm that replaces a victim frame with the current
    * current request based on the FIFO model
    *
    * @param  request   the integer client request
    */ 
    private void frameReplacement(int request) throws RemoteException {
        // Checks to see if the frames array is full
        if (this.occupied_frames < frame_count){
            // If it is not, the current request is placed at the end of the
            // frame list and queue
            this.frames [this.occupied_frames] = request;
            this.FIFO_queue [this.occupied_frames] = request;
            this.occupied_frames ++;
        } else {
            // If it is then the request victim frame is selected from the start 
            // of the FIFO queue
            int victim = this.FIFO_queue [0];

            // Iterates over the frames to replace the victim frame, and
            // shift all of the FIFO queue up to make room for the newest frame
            for (int i = 0; i < frame_count-1; i++){
                if (this.frames [i] == victim)
                    this.frames [i] = request;
                this.FIFO_queue [i] = this.FIFO_queue [i+1];
            }

            // Checks the last frame to see if it should be replaced
            if (this.frames [frame_count - 1] == victim)
                this.frames [frame_count - 1] = request;

            this.FIFO_queue [frame_count - 1] = request;
        }        
    }


    /**
    * Request handler for when a client request is made
    *
    * @param  client_name   the String of the client name
    * @param  request       the integer client request
    * @return               an integer of the square of the request
    */ 
    public int request(String client_name, int request) throws RemoteException{
        // States request and increases request count
        System.out.printf("FIFO_Server> Request from %s for integer %d...\n", client_name, request);

        this.request_count ++;
        
        // Checks for a page fault and handles it accordingly
        if (this.isPageFault(request)) {
            System.out.printf("FIFO_Server> Page Fault detected from %s for integer request %d...\n", client_name, request);
            this.fault_count.put(client_name, this.fault_count.get(client_name) + 1);
            this.frameReplacement(request);

            // Connects to the Central Server when a page fault occurs
            S_Server.request("FIFO_Server", request);
        }

        System.out.printf("FIFO_Server> Frames Illustration %s...\n", this.print_frames());

        // Squares input and returns it for client
        return this.Compute_Square(request);
    }


	/**
    * Returns the square of the input
    *
    * @param  x   the integer square of the input
    * @return           
    */ 
	public int Compute_Square(int x) throws RemoteException {
		System.out.println("FIFO_Server> Computing ...");
 		System.out.println("FIFO_Server> Returning "+x*x+" ...");
		return x*x;
    }   


    /**
    * Attempts to wait for a client to connect itself
    *
    * @param  client  the String of the client attempting to connect      
    */ 
    private void connect_clint(String client) throws RemoteException {
        // Waits for client to connect to server
        this.client_request = Socket_IO.socket_in("FIFO_Server", client);
        // Accepts and handles client request
        this.server_response = this.request(client, client_request);
        // Responds to client with server response
        Socket_IO.socket_out("FIFO_Server", client, server_response);
    }

	/**
    * FIFO Server main function
    *
    * @param  args   command line input String array    
    */ 
	public static void main (String args[]) throws RemoteException {
        // Instantiates server
        FIFO_Server S_Client = new FIFO_Server();
        
        // Instantiate the fault count
        S_Client.fault_count.put("Client_1", 0);
        S_Client.fault_count.put("Client_2", 0);

        // Waits for clients to connect
        for (int i=0; i < 20; i++) {
            S_Client.connect_clint("Client_1");
            S_Client.connect_clint("Client_2");
        }

        System.out.printf("FIFO_Server> On 20 references from Client_1, got %d page faults\n", 
                            S_Client.fault_count.get("Client_1"));
        System.out.printf("FIFO_Server> On 20 references from Client_2, got %d page faults\n", 
                            S_Client.fault_count.get("Client_2"));
        System.out.printf("FIFO_Server> On 40 references from Clients,  got %d page faults\n", 
                            S_Client.fault_count.get("Client_1") + S_Client.fault_count.get("Client_2"));
	}
}

