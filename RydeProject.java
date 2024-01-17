import java.util.ArrayList;

public class RydeProject {
    public static void main(String[] args) throws Exception{

        ArrayList<User> users = new ArrayList<>();
        String start = "location 1";
        String end = "location 2";
        int number = 1234567890;

        Thread userThread = new Thread(new UserThread(users, start, end, number));
        userThread.start();
    }
}
