
//imports for network communication
import java.awt.Graphics2D;
import java.io.*;
import java.net.*;

import javax.swing.SwingUtilities;

abstract public class Client {

    final String LOCAL_HOST = "127.0.0.1"; // "192.168.0.100";
    final int PORT = 7777;

    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;

    public void start(String outputMsg) throws Exception {
        // create a socket with the local IP address and attempt a connection
        System.out.println("Attempting to establish a connection ...");
        clientSocket = new Socket(LOCAL_HOST, PORT); // create and bind a socket, and request connection
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connection to server established!");
        
        output.flush(); // ensure the message was sent but not kept in the buffer
    }

    public void send(String msg) {
        output.println(msg);
    }

    public String receive() {
        try {
            String msg = input.readLine();
            System.out.println(msg);
            return msg;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void stop() throws Exception {
        input.close();
        output.close();
        clientSocket.close();
    }

    abstract public void updateGUI();
    abstract public void draw(Graphics2D g2);
}
