import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
        this.current = null;
        this.start = null;
        this.end = null;
        this.driver = null;
        this.inRide = false;
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

    public void setCurrent(Location location) {
        if (location != null) {
            current = new Location(location.getName(), location.getX(), location.getY());
            for (Location connector : location.getConnections()) {
                current.addConnection(connector);
            }
        } else {
            current = null;
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
            reset();
        } else {
            setCurrent(driver.getCurrent());
            this.current.setX(x);
            this.current.setY(y);
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
                    info += "\nPlease wait patiently for a driver to pick up the order";
                }
                infoPanel.bottomPanel.setVisible(false);
                infoPanel.setLocationText(start.toString(), end.toString());
                infoPanel.displayInfo(Color.black, info);
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

    public String acceptInfo(){
       return "User " + getNumber() + ": Start: " + getStart().toString() + ", End : " + getEnd().toString() + ", Carpool: " + !isAlone();
    }

    @Override
    public String toString() {
        return "User:" + getNumber() + "," + getStart() + "," + getEnd() + "," + isAlone() + "," + isInRide();
    }

}
