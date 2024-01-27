import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class User extends Client {
    private Location start, end;
    private boolean inRide, isAlone;
    private Driver driver;

    public User(BufferedImage userImage, Interface gui, long phoneNum) {
        super(userImage, gui, phoneNum);
        this.inRide = false;
    }

    public User(BufferedImage userImage, Interface gui, long phoneNum, Location start, Location end,boolean isAlone, boolean inRide) {
        super(userImage, gui, phoneNum);
        this.start = start;
        this.end = end;
        this.isAlone = isAlone;
        this.inRide = inRide;
        setCurrent(start);
    }
    // Connection to server
    public void start() throws Exception {
        super.start("newUser:" + getNumber());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    @Override
    public Location getIsHeading() {
        return getEnd();
    }

    public boolean isInRide() {
        return inRide;
    }

    public boolean isAlone() {
        return isAlone;
    }

    public boolean hasDriver() {
        return this.driver != null;
    }

    public Driver getDriver() {
        return driver;
    }

    public boolean isDoneChoose() {
        return getEnd() != null && getStart() != null;
    }

    public void setStart(Location start) {
        this.start = start;
        setCurrent(start);
    }

    public double getPrice(Location start, Location end, SimpleGraph graph){
        ArrayList<Location> path = start.shortestPath(end ,graph);
        if(isAlone()){
            return Math.round(start.pathLength(path)*0.05);
        }else{
            return Math.round(start.pathLength(path)*0.03);
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

    public void reset() {
        setCurrent(null);
        this.start = null;
        this.end = null;
        this.driver = null;
        this.inRide = false;
    }

    // move the user
    public void move() {
        double x = driver.getCurrent().getX();
        double y = driver.getCurrent().getY();
        if (getCurrent().compare(getCurrent(), getEnd())) {
            System.out.println("I've arrived at my destination: " + end.getName());
            reset();
        } else {
            setCurrent(driver.getCurrent());
            getCurrent().setX(x);
            getCurrent().setY(y);
            setRideStatus(true);
        }
    }

    public void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            InfoPanel infoPanel = getGui().getInfoPanel();
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
            g2.drawImage(getIcon(), ((int) getCurrent().getX() - 50), ((int) getCurrent().getY() - 153), null);
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
