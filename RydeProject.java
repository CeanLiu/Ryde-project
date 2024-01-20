import java.util.ArrayList;

public class RydeProject {
    public static void main(String[] args) throws Exception{
        SimpleGraph graph = new SimpleGraph();

        ArrayList<User> users = new ArrayList<>();
        String start = "F";
        String end = "I";
        int number = 1234567890;

        Thread userThread = new Thread(new UserThread(users, start, end, number));
        userThread.start();

        ArrayList<Driver> drivers = new ArrayList<>();
        Thread driverThread = new Thread(new DriverThread(graph,drivers,users.get(0),5,number));
        driverThread.start();

        
    }
}
