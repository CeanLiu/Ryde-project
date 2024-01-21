import java.awt.Point;
import java.util.ArrayList;

public class User extends Client{
    private long phoneNum; // acts as the user id
    Location start, end;
    Point location;
    boolean inRide;

    public User(long phoneNum){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
        this.inRide = false;
        this.phoneNum = phoneNum;
    }
    public void setStart(Location start){
        this.start = start;
    }
    public void setEnd(Location end){
        this.end = end;
    }

    public User(long phoneNum, String start, String destination){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
        this.phoneNum = phoneNum;
        // this.start = start;
        // this.destination = destination;
        this.inRide = false;
    }

    public long getNumber(){
        return phoneNum;
    }
    //move the user
    // public void move(Point location,SimpleGraph graph){
    //     this.location = location;
    //     if (this.location.equals(graph.getCoordinates(destination))){
    //         System.out.println("I've arrived at my destination: "+destination);
    //     }
    //     // for (String node: graph.getAllCoords().keySet()){
    //     //     if (graph.getCoordinates(node) == this.location){System.out.println("User is now at: " + node);}
    //     // }
    // }

    //Connection to server
    @Override
    public void start() throws Exception{
        super.start();
    }
    @Override
    public void stop() throws Exception{
        super.stop();
    }

    @Override 
    public String toString(){
        return "User: " + phoneNum + ", start: " + start + ", end: " + end;
    }
    

    
}
