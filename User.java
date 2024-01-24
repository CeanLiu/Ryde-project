import java.awt.Point;
import java.util.ArrayList;

public class User extends Client{
    private long phoneNum; // acts as the user id
    private InfoPanel gui;
    private Location current, start, end;
    private boolean inRide, isAlone;
    private boolean finished;
    private Driver driver;

    public User(long phoneNum, InfoPanel gui){ // add a parameter for the map containing the coordinates, and then allocate the coords for start to x and y
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

    public Location getCurrent() {
        return current;
    }
    public Location getEnd() {
        return end;
    }
    public Location getStart() {
        return start;
    }
    public boolean isInRide() {
        return inRide;
    }
    public boolean isAlone() {
        return isAlone;
    }
    public boolean isFinished() {
        return finished;
    }
    public boolean hasDriver() {
        return this.driver != null;
    }
    public Driver getDriver() {
        return driver;
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
    public void setRideStatus(Boolean inRide){
        this.inRide = inRide;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }

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

    public void displayInfoGUI(){
        String info = "User " + getNumber() + ":\nStart Location: " + getStart().toString() + "\nEnd Location: " + getEnd().toString();
        if(isAlone()){
            info += "\nCar Pool: No";
        }else{
            info += "\nCar Pool: Yes";
        }
        if(getDriver()!=null){
            info += "\nDriver Phone Number: " + getDriver().getNumber();
            if(isInRide()){
                info += "\nEnjoy your ride";
            }else{
                info += "\nPlease wait for driver to pick-up";
            }
        }else{
            info += "\nPlease wait patiently for a driver to pick up order";
        }
        gui.displayInfo(info);
    }

    @Override 
    public String toString(){
        return "User:"+ phoneNum + "," + start + "," + end + "," + isAlone + "," + inRide;
    }
    

    
}
