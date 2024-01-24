import java.awt.Point;
import java.util.ArrayList;

public class User extends Client{
    private long phoneNum; // acts as the user id
    InfoPanel gui;
    Location current, start, end;
    boolean inRide, isAlone;
    boolean finished;

    public User(long phoneNum, InfoPanel gui ){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
        this.gui = gui;
        this.phoneNum = phoneNum;
        this.inRide = false;
        this.finished = false;
    }
    public User(long phoneNum,Location start, Location end, boolean isAlone, boolean inRide){ 
        this.phoneNum = phoneNum;
        this.start = start;
        this.end = end;
        this.isAlone = isAlone;
        this.inRide = inRide;
        this.finished = false;
    }
    public void setStart(Location start){
        this.start = start;
        this.current = new Location(start.getName(),start.getX(),start.getY());
        for(Location connector: start.getConnections()){
            current.addConnection(connector);
        }
    }
    public void setEnd(Location end){
        this.end = end;
    }
    public void setChoice(Boolean isAlone){
        this.isAlone = isAlone;
    }
    public void setStatus(Boolean inRide){
        this.inRide = inRide;
    }

    // public User(long phoneNum, String start, String destination){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
    //     this.phoneNum = phoneNum;
    //     // this.start = start;
    //     // this.destination = destination;
    //     this.inRide = false;
    // }

    public long getNumber(){
        return phoneNum;
    }
    //move the user
    public void move(double x, double y){
        this.current.setX(x);
        this.current.setY(y);
        if (current.getX() == end.getX() && current.getY() == end.getY()){
            System.out.println("I've arrived at my destination: "+end.getName());
        }
    }

    //Connection to server
    public void start() throws Exception{
        super.start("ryder");
    }
    @Override
    public void stop() throws Exception{
        super.stop();
    }

    @Override 
    public String toString(){
        return phoneNum + "," + start + "," + end + "," + isAlone + "," + inRide;
    }
    

    
}
