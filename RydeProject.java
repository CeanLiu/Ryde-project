import java.util.ArrayList;

public class RydeProject {
    public static void main(String[] args) throws Exception{
        SimpleGraph graph = new SimpleGraph();
        Interface pInterface = new Interface(graph, "mapImage.png");
        pInterface.runGUI();

        // ArrayList<User> users = new ArrayList<>();
        // ArrayList<Driver> drivers = new ArrayList<>();
        // Thread driverThread = new Thread(new DriverThread(graph,5,101000,pInterface));
        // driverThread.start();


        // int number = 1234567890;

        // Thread userThread = new Thread(new UserThread(users, number));
        // userThread.start();

        // users.add(new User(number));
        // users.get(0).setStart(graph.getLocation("Dallas"));
        // users.get(0).setEnd(graph.getLocation("Miami"));

        

        
    }
}
