import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class User extends Client {
    private long phoneNum; // acts as the user id
    private InfoPanel gui;
    private Location current, start, end;
    private boolean inRide, isAlone;
    private boolean finished;
    private Driver driver;
    private SimpleGraph graph;
    

    public User(long phoneNum, InfoPanel gui, SimpleGraph map) { 
        this.gui = gui;
        this.phoneNum = phoneNum;
        this.inRide = false;
        this.finished = false;
    }

    public User(long phoneNum, Location start, Location end, boolean isAlone, boolean inRide, SimpleGraph graph) {
        this.phoneNum = phoneNum;
        this.start = start;
        this.end = end;
        this.isAlone = isAlone;
        this.inRide = inRide;
        this.finished = false;
        this.graph = graph;
    }

    public long getNumber() {
        return phoneNum;
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

    public SimpleGraph getGraph() {
        return graph;
    }

    public boolean isDoneChoose(){
        return getEnd()!=null && getStart()!=null;
    }

    public void setStart(Location start) {
        this.start = start;
        this.current = new Location(start.getName(), start.getX(), start.getY());
        for (Location connector : start.getConnections()) {
            current.addConnection(connector);
        }
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public void setChoice(Boolean isAlone) {
        this.isAlone = isAlone;
    }

    public void setRideStatus(Boolean inRide) {
        this.inRide = inRide;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setGui(InfoPanel gui) {
        this.gui = gui;
    }

    public void setGraph(SimpleGraph graph) {
        this.graph = graph;
    }

    // move the user
    public void move(double x, double y) {
        this.current.setX(x);
        this.current.setY(y);
        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            System.out.println("I've arrived at my destination: " + end.getName());
        }
    }

    // Connection to server
    public void start() throws Exception {
        super.start("ryder");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            String info = "User " + getNumber() + ":\nStart Location: " + getStart().toString() + "\nEnd Location: "+ getEnd().toString();
            if (isAlone()) {
                info += "\nCar Pool: No";
            } else {
                info += "\nCar Pool: Yes";
            }
            if (getDriver() != null) {
                info += "\nDriver Phone Number: " + getDriver().getNumber();
                if (isInRide()) {
                    info += "\nEnjoy your ride";
                } else {
                    info += "\nPlease wait for the driver to pick up";
                }
            } else {
                System.out.println(getDriver());
                info += "\nPlease wait patiently for a driver to pick up the order";
            }
            if(isDoneChoose()){
                gui.bottomPanel.setVisible(false);
                gui.setLocationText(start.toString(), end.toString());
                gui.displayInfo(Color.black, info);
            }else{
                gui.bottomPanel.setVisible(true);
                gui.resetTextField();
            }
        });
    }

    @Override
    public void drawPath(Graphics2D g2){
        SwingUtilities.invokeLater (()->{
            getStart().drawPath(g2, graph, current);
        });
    }

    @Override
    public String toString() {
        return "User:" + getNumber() + "," + getStart() + "," + getEnd() + "," + isAlone() + "," + isInRide();
    }

}
