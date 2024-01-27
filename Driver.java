import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.SwingUtilities;

public class Driver extends Client {
    private ArrayList<User> ryders = new ArrayList<>();
    private int capacity;
    private SimpleGraph graph;
    private volatile boolean isDrive;
    private double directionAngle;
    private Location isHeading;

    public Driver(BufferedImage driverImage, Interface gui, SimpleGraph graph,long phoneNum, int capacity) {
        super(driverImage, gui, phoneNum);
        this.graph = graph;
        this.capacity = capacity;
        this.isDrive = false;
        this.isDrive = false;
    }

    public Driver(BufferedImage driverImage, Interface gui, SimpleGraph graph, long phoneNum, Location current, int capacity, boolean isDrive) {
        super(driverImage, gui, phoneNum);
        setCurrent(current);
        this.capacity = capacity;
        this.isDrive = isDrive;
    }

    public void start() throws Exception {
        super.start("newDriver:" + getNumber() + "," + getCapacity());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public Location getIsHeading() {
        return isHeading;
    }

    public double getDirectionAngle() {
        return directionAngle;
    }

    public boolean hasRyders() {
        return !this.ryders.isEmpty();
    }

    public boolean hasCurrLocation() {
        return getCurrent()!= null;
    }

    public synchronized boolean isDrive() {
        return this.isDrive;
    }
    
    public void setCapacity(int cap) {
        this.capacity = cap;
    }

    public synchronized void setDrive(boolean isDrive) {
        this.isDrive = isDrive;
    }

    public void setDirectionAngle(double directionAngle) {
            this.directionAngle = directionAngle;
    }

    public void assignRyder(User ryder) {
        if(!ryders.contains(ryder)){
            this.ryders.add(ryder);
        }
    }

    public void removeRyder(User ryder) {
        if (ryders.contains(ryder)) {
            ryders.remove(ryder);
        }
    }

    public void move() {
        // pickup ryders
        ArrayList<User> pickupPending = new ArrayList<>();
        for (User ryder : ryders) {
            if (!ryder.isInRide()) {
                pickupPending.add(ryder);
            }
        }
        while (!pickupPending.isEmpty()) {
            ArrayList<ArrayList<Location>> closestRyder = new ArrayList<>();
            // find the shortest path to a user, add it to a list to be compared, and keep repeating until collected all users
            for (User ryder : pickupPending) {
                if (!ryder.isInRide()) {
                    closestRyder.add(getCurrent().shortestPath(ryder.getStart(), graph));
                }
            }
            // find the shortest path out of all the users
            ArrayList<Location> closest = closestRyder.get(0);
            for (ArrayList<Location> path : closestRyder) {
                if (getCurrent().pathLength(path) < getCurrent().pathLength(closest)) {
                    closest = new ArrayList<>(path);
                }
            }
            // set the location (user starting location) as isHeading
            isHeading = closest.get(closest.size() - 1);
            // move
            if (closest.size() == 1) {
                for (User ryder : ryders) {
                    if (getCurrent().compare(getCurrent(), ryder.getStart())) {
                        send("aboard:" + ryder.getNumber() + "," + getNumber());
                        ryder.setRideStatus(true);
                    }
                }
            } else {
                for (int i = 0; i < closest.size() - 1; i++) {
                    moveSteps(closest.get(i), closest.get(i + 1));
                }
            }
            // remove the user from the list to be picked up
            ArrayList<User> temp = new ArrayList<>(pickupPending);
            for (User ryder : temp) {
                if (ryder.getStart().getX() == getCurrent().getX()
                        && ryder.getStart().getY() == getCurrent().getY()) {
                    pickupPending.remove(ryder);
                }
            }
        }
        // drop off ryders
        ArrayList<User> dropoffPending = new ArrayList<>();
        for (User ryder : ryders) {
            if (ryder.isInRide()) {
                dropoffPending.add(ryder);
            }
        }
        while (!dropoffPending.isEmpty()) {
            ArrayList<ArrayList<Location>> closestStop = new ArrayList<>();
            // find the shortest path to a user's destination, add it to a list to be compared, keep repeating until done for all
            for (User ryder : dropoffPending) {
                if (ryder.isInRide()) {
                    closestStop.add(getCurrent().shortestPath(ryder.getEnd(), graph));
                }
            }
            // find the shortest path out of all the users
            ArrayList<Location> closest = closestStop.get(0);
            for (ArrayList<Location> path : closestStop) {
                if (getCurrent().pathLength(path) < getCurrent().pathLength(closest)) {
                    closest = new ArrayList<>(path);
                }
            }
            // set the location (user destination) as isHeading
            isHeading = closest.get(closest.size() - 1);
            // move
            for (int i = 0; i < closest.size() - 1; i++) {
                moveSteps(closest.get(i), closest.get(i + 1));
            }
            // remove the user from the list to be dropped off
            ArrayList<User> temp = new ArrayList<>(dropoffPending);
            for (User ryder : temp) {
                if (ryder.getEnd().getX() == getCurrent().getX()
                        && ryder.getEnd().getY() == getCurrent().getY()) {
                    dropoffPending.remove(ryder);
                    ryder.reset();
                }
            }
        }
        isHeading = null;

        if (ryders.isEmpty()) {
            setDrive(false);
            setCurrent(null);
            this.send("stopDriver:" + getNumber());
        }
        updateGUI();
    }

    public void moveSteps(Location initial, Location next) {
        int x1 = (int) initial.getX();
        int y1 = (int) initial.getY();
        int x2 = (int) next.getX();
        int y2 = (int) next.getY();
        directionAngle = Math.atan2(y2 - y1, x2 - x1);

        while (!getCurrent().compare(getCurrent(), next)) {
            x1 += 200 * Math.cos(directionAngle);
            y1 += 200 * Math.sin(directionAngle);
            getCurrent().setX(x1);
            getCurrent().setY(y1);

            if (Math.abs(getCurrent().getX() - next.getX()) <= 200&& Math.abs(getCurrent().getY() - next.getY()) <= 200) {
                setCurrent(next);
           //     Location last = combinedPath.get(combinedPath.size()-1);
              //  combinedPath = new ArrayList<Location>(getCurrent().shortestPath(last, graph));
            }
            for (User ryder : ryders) {
                if (getCurrent().compare(getCurrent(), ryder.getStart())) {
                    send("aboard:" + ryder.getNumber() + "," + getNumber());
                    ryder.setRideStatus(true);
                }
            }
            if (!this.ryders.isEmpty()) {
                ArrayList<User> temp = new ArrayList<>(ryders);
                for (User ryder : temp) {
                    if (ryder.isInRide() && getCurrent().compare(getCurrent(), ryder.getEnd())) {
                        send("arrive:"+ryder.getNumber()+","+getNumber());
                        removeRyder(ryder);
                    }
                }
                temp = new ArrayList<>(ryders);
            } 
            this.send("moveDriver:"+getNumber()+","+getCurrent().getName()+","+getCurrent().getX()+","+getCurrent().getY()+","+getDirectionAngle());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Location> createPath(Location start, Location end) { // shortest node connections
        ArrayList<Location> path = new ArrayList<>();
        Queue<Location> queue = new LinkedList<>();
        HashSet<Location> visited = new HashSet<>();
        HashMap<Location, Location> connections = new HashMap<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.remove();
            if (current.equals(end)) {
                while (!current.equals(start)) {
                    path.add(current);
                    current = connections.get(current);
                }
                path.add(current);
                return path;
            }
            for (Location connector : current.getConnections()) {
                if (!visited.contains(connector)) {
                    queue.add(connector);
                    visited.add(connector);
                    connections.put(connector, current);
                }
            }
        }
        System.out.println("Return path is null in method create path");
        return null;
    }

    // -------------------------------------------------------------------------
    @Override
    public void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            InfoPanel infoPanel = getGui().getInfoPanel();
            String info = "Driver " + getNumber() + ": You have " + ryders.size() + " ryders.";
            for (User ryder : ryders) {
                info += "\nUser " + ryder.getNumber() + ":\nStart Location: " + ryder.getStart().toString() + "\nEnd Location: " + ryder.getEnd().toString();
            }
            if (hasCurrLocation()) {
                infoPanel.setLocationText(getCurrent().toString());
                infoPanel.confirmButton.setVisible(false);
                if (!isDrive()) {
                    if(ryders.size() <= getCapacity()){
                        if ((ryders.size() == 1 && ryders.get(0).isAlone() == true) || ryders.size() == getCapacity()){ // selected a user that rides alone
                            infoPanel.createRequest(Color.black, info, "none");
                        }
                        else if (ryders.size() == 0){ // did not select a user yet
                            infoPanel.createRequest(Color.black, info,"all");
                        } else {    // selected a user that wants to carpool
                            infoPanel.createRequest(Color.black, info, "carpool");
                        }
                    }
                    if(hasRyders()){
                        infoPanel.dButtonPanel.setVisible(true); // 
                    }else{
                        infoPanel.dButtonPanel.setVisible(false);
                    }
                } else {
                    infoPanel.dButtonPanel.setVisible(false);
                    infoPanel.displayInfo(Color.black, "Please go pick up your Ryder\n" + info);
                }
            } else {
                infoPanel.resetDisplay();
                infoPanel.confirmButton.setVisible(true);
                infoPanel.displayInfo(Color.red, "Please Choose Your Current Location to Start Picking Customers");
            }
        });
    }

    @Override
    public void draw(Graphics2D g2) {
        if (hasCurrLocation()) {
            int x = (int) (getCurrent().getX() - getIcon().getWidth(null) / 2.0);
            int y = (int) (getCurrent().getY() - getIcon().getHeight(null) / 2.0);
            if (isDrive()) {
                AffineTransform transform = new AffineTransform();
                transform.translate(x, y);
                transform.rotate(getDirectionAngle(), getIcon().getWidth(null) / 2.0,
                getIcon().getHeight(null) / 2.0);
                g2.drawImage(getIcon(), transform, null);
            } else {
                g2.drawImage(getIcon(), x, y, null);
            }
        }
    }

    public String getInfo() {
        return "Driver:" + getNumber() + "," + getCurrent() + "," + getCapacity() + "," + isDrive();
    }

    public String getRydeInfo() {
        String info = "";
        for (int i = 0; i < ryders.size(); i++) {
            if (i != ryders.size() - 1) {
                info += ryders.get(i).getNumber() + ",";
            } else {
                info += ryders.get(i).getNumber();
            }
        }
        return info;
    }

    @Override
    public String toString() {
        return getInfo() + "_" + getRydeInfo();
    }
}