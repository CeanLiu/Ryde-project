import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class User extends Client {
    private long phoneNum; // acts as the user id
    private Interface gui;
    private Location current, start, end;
    private boolean inRide, isAlone;
    private boolean finished;
    private Driver driver;
    private SimpleGraph graph;
    private BufferedImage userImage;

    public User(Interface gui, SimpleGraph graph, long phoneNum) {
        this.gui = gui;
        this.graph = graph;
        this.phoneNum = phoneNum;
        this.inRide = false;
        this.finished = false;
        try {
            userImage = ImageIO.read(new File("user.png"));
        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
    }

    public User(Interface gui, SimpleGraph graph,long phoneNum, Location start, Location end, boolean isAlone, boolean inRide) {
        this.gui = gui;
        this.graph = graph;
        this.phoneNum = phoneNum;
        this.start = start;
        this.end = end;
        this.isAlone = isAlone;
        this.inRide = inRide;
        this.finished = false;
        this.current = new Location(start.getName(), start.getX(), start.getY());
        for (Location connector : start.getConnections()) {
            current.addConnection(connector);
        }
        try {
            userImage = ImageIO.read(new File("user.png"));
        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
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

    public boolean isDoneChoose() {
        return getEnd() != null && getStart() != null;
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

    public void setGui(Interface gui) {
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
            InfoPanel infoPanel = gui.getInfoPanel();
            MapPanel mapPanel = gui.getMapPanel();
            if (isDoneChoose()) {
                String info = "User " + getNumber() + ":\nStart Location: " + getStart().toString() + "\nEnd Location: " + getEnd().toString();
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
                infoPanel.bottomPanel.setVisible(false);
                infoPanel.setLocationText(start.toString(), end.toString());
                infoPanel.displayInfo(Color.black, info);
                System.out.println(mapPanel.toString());
                System.out.println(graph.toString());
                System.out.println(getCurrent());
                System.out.println(getEnd());
                mapPanel.setPathToDraw(getCurrent().shortestPath(getEnd(),graph));
            } else {
                infoPanel.bottomPanel.setVisible(true);
                infoPanel.resetTextField();
            }
        });
    }

    @Override 
    public void draw(Graphics2D g2){
        if(isDoneChoose()){
            g2.drawImage(userImage,150,160,null);
        }
        else{
            return;
        }
    }

    @Override
    public String toString() {
        return "User:" + getNumber() + "," + getStart() + "," + getEnd() + "," + isAlone() + "," + isInRide();
    }

}
