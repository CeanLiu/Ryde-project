import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    final int PORT = 7777;

    ServerSocket serverSocket;
    ArrayList<ConnectionHandler> clients= new ArrayList<>();
    private LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<String>();
    int clientCounter = 0;

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.go();
    }

    public void go() throws Exception {
        System.out.println("Waiting for a connection request from a client ...");
        serverSocket = new ServerSocket(PORT);
        Thread accept = new Thread() {
            public void run(){
                while(true){
                    try{
                        Socket clientSocket = serverSocket.accept();
                        ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
                        clients.add(connectionHandler);
                        Thread connectionThread = new Thread(connectionHandler);
                        connectionThread.start();
                       System.out.println("Client "+clients.size()+" connected");  
                    }
                    catch(Exception e){ e.printStackTrace(); }
                }
            }
        };
        accept.start();

        //a thread that keeps track of a queue of items, and sends the msgs out if the queue is not empty 
        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        if(!messages.isEmpty()){
                            String msg = messages.remove();
                            System.out.println("Message Received: " + msg);
                            sendToAll(msg);
                        }
                    }catch(Exception e){e.printStackTrace(); }
                }
            }
        };
        messageHandling.start();
    }

    // ------------------------------------------------------------------------------
    class ConnectionHandler extends Thread {
        Socket socket;
        PrintWriter output;
        BufferedReader input;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        // ------------------------------------------------------------------------------
        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(),false);
                while (true) {
                    // Receive a message from the client
                    String msg = input.readLine();
                    messages.put(msg);
                    System.out.println("Message from client " + clientCounter + ": " + msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void send(String msg){
            try{
                output.println(msg);
                output.flush();
            }catch(Exception e){ e.printStackTrace(); }
        }
    }
    
    // sends information back to all the clients
    public void sendToAll(String msg){
        int i = 0;
        for(ConnectionHandler client : clients){
            System.out.print("client " + i++ + msg + "\n");
            client.send(msg);
        }
    }
}
