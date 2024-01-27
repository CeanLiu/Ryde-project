import java.awt.Graphics2D;
import java.io.*;
import java.net.*;

abstract public class Client {

    final String LOCAL_HOST = "127.0.0.1"; // "192.168.0.100";
    final int PORT = 7777;

    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;

    public void start(String outputMsg) throws Exception {
        System.out.println("Attempting to establish a connection ...");
        clientSocket = new Socket(LOCAL_HOST, PORT);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connection to server established!");
        output.println(getNumber()+"%"+outputMsg);
        output.flush();
    }

    public void send(String msg) {
        output.println(getNumber()+"%"+msg);
    }

    // receive messages from the server
    public String receive() {
        try {
            String msg = input.readLine();
            Long clientNum = Long.parseLong(msg.split("%")[0]);
            if(clientNum == getNumber()){
                return null;
            }
            return msg.split("%")[1];
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

    abstract public long getNumber();
    abstract public Location getIsHeading();
    abstract public Location getCurrent();
    abstract public void updateGUI();
    abstract public void draw(Graphics2D g2);
}
