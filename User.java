import java.util.ArrayList;

public class User extends Client{
    private int phoneNum; // acts as the user id
    String start;
    String destination;
    int x;
    int y;
    boolean inRide;

    public User(int phoneNum, String start, String destination){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
        this.phoneNum = phoneNum;
        this.start = start;
        this.destination = destination;
        this.inRide = false;
        // this.x = 
        // this.y = 
    }

    //move the user
    public void move(User user, Map map){

    }

    @Override
    public void start() throws Exception{
        super.start();
    }
    @Override
    public void stop() throws Exception{
        super.stop();
    }
    

    
}
