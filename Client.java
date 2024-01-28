import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

abstract public class Client {

    final String LOCAL_HOST = "127.0.0.1";
    final int PORT = 7777;

    Socket clientSocket;
    PrintWriter output;
    BufferedReader input;
    
    private Long phoneNum;
    private BufferedImage icon;
    private Interface gui;
    private Location current;

    public Client (BufferedImage icon, Interface gui, long phoneNum){
        this.icon = icon;
        this.gui = gui;
        this.phoneNum = phoneNum;
    }

    public void start(String outputMsg) throws Exception {
        System.out.println("Attempting to establish a connection ...");
        clientSocket = new Socket(LOCAL_HOST, PORT);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Connection to server established!");
        output.println(getNumber()+"%"+outputMsg);
        output.flush();
    }

    public long getNumber(){
        return this.phoneNum;
    }

    public Location getCurrent(){
        return this.current;
    }

    public Interface getGui() {
        return gui;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    public void setCurrent(Location location){
        if(location != null){
            this.current = new Location(location.getName(), location.getX(), location.getY());
            for (Location connector : location.getConnections()) {
                current.addConnection(connector);
            }
        }else{
            this.current= null;
            return;
        }
    }

    public void send(String msg) {
        output.println(getNumber()+"%"+msg);
    }

    // receive messages from the server only if the client is not the one that sent the messages
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

    abstract public Location getIsHeading();
    abstract public void updateGUI();
    abstract public void draw(Graphics2D g2);
}
