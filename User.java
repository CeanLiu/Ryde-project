import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
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
    private boolean checkedInRide = false;

    public User(BufferedImage userImage, Interface gui, SimpleGraph graph, long phoneNum) {
        this.userImage = userImage;
        this.gui = gui;
        this.graph = graph;
        this.phoneNum = phoneNum;
        this.inRide = false;
        this.finished = false;
    }

    public User(BufferedImage userImage, Interface gui, SimpleGraph graph, long phoneNum, Location start, Location end,
            boolean isAlone, boolean inRide) {
        this.userImage = userImage;
        this.gui = gui;
        this.graph = graph;
        this.phoneNum = phoneNum;
        this.start = start;
        this.end = end;
        this.isAlone = isAlone;
        this.inRide = inRide;
        this.finished = false;
        setCurrent(start);
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

    @Override
    public Location getIsHeading() {
        return getEnd();
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

    public void reset() {
        // if(isFinished()){
        this.current = null;
        this.start = null;
        this.end = null;
        this.driver = null;
        this.inRide = false;
        this.checkedInRide = false;
        // }
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

    // public boolean isInRide() {
    // if(hasDriver()){
    // if(!checkedInRide){
    // if(getCurrent().compare(getCurrent(), driver.getCurrentLocation())){
    // checkedInRide = true;
    // send("aboard:"+getNumber()+","+getDriver());
    // return inRide = true;
    // }else{
    // return inRide = false;
    // }
    // }else{
    // return inRide;
    // }
    // }else{
    // return inRide = false;
    // }
    // }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setStart(Location start) {
        this.start = start;
        if (start != null) {
            this.current = new Location(start.getName(), start.getX(), start.getY());
            for (Location connector : start.getConnections()) {
                current.addConnection(connector);
            }
        } else {
            return;
        }
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public void setCurrent(Location input) {
        if (input != null) {
            Location location = graph.getLocation(input.getName());
            current = new Location(location.getName(), location.getX(), location.getY());
            for (Location connector : location.getConnections()) {
                current.addConnection(connector);
            }
        } else {
            System.out.println(getNumber() + "START IS NULL");
            return;
        }
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
    public void move() {
        double x = driver.getCurrent().getX();
        double y = driver.getCurrent().getY();
        if (getCurrent().compare(getCurrent(), getEnd())) {
            System.out.println("I've arrived at my destination: " + end.getName());
            // finished = true;
            reset();
        } else {
            Location driverLocat = graph.getLocation(driver.getCurrent().getName());
            setCurrent(driverLocat);
            this.current.setX(x);
            this.current.setY(y);
            // current = new Location(driverLocat.getName(), driverLocat.getX(), driverLocat.getY());
            // for (Location connector : driverLocat.getConnections()) {
            //     current.addConnection(connector);
            // }
            setRideStatus(true);
        }
    }

    // Connection to server
    public void start() throws Exception {
        super.start("newUser:" + getNumber());
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
                String info = "User " + getNumber() + ":\nStart Location: " + getStart().toString() + "\nEnd Location: "
                        + getEnd().toString();
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
                    // System.out.println(getDriver());
                    info += "\nPlease wait patiently for a driver to pick up the order";
                }
                infoPanel.bottomPanel.setVisible(false);
                infoPanel.setLocationText(start.toString(), end.toString());
                infoPanel.displayInfo(Color.black, info);
                // System.out.println(mapPanel.toString());
                // System.out.println(graph.toString());
                // System.out.println(getCurrent());
                // System.out.println(getEnd());
                // mapPanel.setPathToDraw(getStart().shortestPath(getEnd(), graph));
            } else {
                infoPanel.bottomPanel.setVisible(true);
                infoPanel.resetDisplay();
            }
        });
    }

    @Override
    public void draw(Graphics2D g2) {
        if (isDoneChoose()) {
            g2.drawImage(userImage, ((int) current.getX() - 50), ((int) current.getY() - 153), null);
            if (hasDriver()) {
                driver.draw(g2);
            }
        } else {
            return;
        }
    }

    public String requestInfo() {
        return getNumber() + "," + getStart() + "," + getEnd() + "," + isAlone();
    }

    @Override
    public String toString() {
        return "User:" + getNumber() + "," + getStart() + "," + getEnd() + "," + isAlone() + "," + isInRide();
    }

}
