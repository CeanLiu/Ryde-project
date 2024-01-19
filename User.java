import java.awt.Point;
import java.util.ArrayList;

public class User extends Client{
    private int phoneNum; // acts as the user id
    String start;
    String destination;
    Point location;
    boolean inRide;

    public User(int phoneNum, String start, String destination){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
        this.phoneNum = phoneNum;
        this.start = start;
        this.destination = destination;
        this.inRide = false;
    }

    //move the user
    public void move(Point location){
        this.location = location;
    }

    //Connection to server
    @Override
    public void start() throws Exception{
        super.start();
    }
    @Override
    public void stop() throws Exception{
        super.stop();
    }
    

    
}
