//imports for network communication
import java.io.*;
import java.net.*;

public class Client {
    final String LOCAL_HOST = "127.0.0.1";//"192.168.0.100";
    final int PORT = 7777;
    
    Socket clientSocket;
    PrintWriter output;    
    BufferedReader input;
    
    public void start() throws Exception{ 
        //create a socket with the local IP address and attempt a connection
        System.out.println("Attempting to establish a connection ...");
        clientSocket = new Socket(LOCAL_HOST, PORT);          //create and bind a socket, and request connection
        output = new PrintWriter(clientSocket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connection to server established!");
        
        output.println("Hi. I am a client of the Ryde Project!");           //send a message to the server
        output.flush();                                       //ensure the message was sent but not kept in the buffer
        String msg = input.readLine();                        //get a response from the server
        System.out.println("Message from server: '" + msg+"'");   
    }
    
    public void stop() throws Exception{ 
        input.close();
        output.close();
        clientSocket.close();
    }
}
