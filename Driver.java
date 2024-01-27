import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.SwingUtilities;

public class Driver extends Client {
    private ArrayList<Location> combinedPath = new ArrayList<>();
    private ArrayList<User> ryders = new ArrayList<>();
    private Long phoneNum;
    private int capacity;
    private Location currentLocation;
    private Interface gui;
    private SimpleGraph graph;
    private volatile boolean isDrive;
    private BufferedImage driverImage;
    private double directionAngle;
    private Location isHeading;

    public Driver(BufferedImage driverImage, Interface gui, SimpleGraph graph,long phoneNum, int capacity) {
        this.driverImage = driverImage;
        this.gui = gui;
        this.graph = graph;
        this.phoneNum = phoneNum;
        this.capacity = capacity;
        this.isDrive = false;
        this.isDrive = false;
    }

    public Driver(BufferedImage driverImage, Interface gui, SimpleGraph graph, long phoneNum, Location current, int capacity, boolean isDrive) {
        this.driverImage = driverImage;
        this.gui = gui;
        this.graph = graph;
        this.phoneNum = phoneNum;
        setCurrentLocation(current);
        this.capacity = capacity;
        this.isDrive = isDrive;
    }

    public void start() throws Exception {
        super.start("newDriver:"+getNumber()+","+getCapacity());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public long getNumber() {
        return this.phoneNum;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public Location getIsHeading() {
        return isHeading;
    }

    public void setCapacity(int cap) {
        this.capacity = cap;
    }

    public Location getCurrent() {
        return currentLocation;
    }

    public double getDirectionAngle() {
        return directionAngle;
    }

    public boolean hasRyders() {
        return !this.ryders.isEmpty();
    }

    public boolean hasCurrLocation() {
        return this.currentLocation != null;
    }

    public synchronized boolean isDrive() {
        return this.isDrive;
    }

    public void setCurrentLocation(Location location) {
        if(location != null){
            this.currentLocation = new Location(location.getName(), location.getX(), location.getY());
            for (Location connector : location.getConnections()) {
                currentLocation.addConnection(connector);
            }
        }else{
            System.out.println("DRIVER CURR IS NULL");
            return;
        }
    }


    public synchronized void setDrive(boolean isDrive) {
        this.isDrive = isDrive;
    }

    public void setGUI(Interface gui) {
        this.gui = gui;
    }

    public void setDirectionAngle(double directionAngle) {
            this.directionAngle = directionAngle;
    }

    public void assignRyder(User ryder) {
        if(!ryders.contains(ryder)){
            this.ryders.add(ryder);
        }
    }


    // public void addRyder(User newRyder) {
    //     for (User ryder : this.ryders) {
    //         if (ryder.getNumber() == newRyder.getNumber()) {
    //             // newRyder.inRide = true;
    //             newRyder.setRideStatus(true);
    //             System.out.println("Welcome aboard, ryder " + newRyder.getNumber());
    //             return;
    //         }
    //     }
    // }

    public void removeRyder(User ryder){
        if(ryders.contains(ryder)){
            ryders.remove(ryder);
        }
    }

    public void move() {
      //  System.out.println("\nDriver's current location: " + currentLocation.getName());
//
        // pickup ryders

        ArrayList<User> pickupPending = new ArrayList<>();
        for (User ryder: ryders){
            if (!ryder.isInRide()){
                pickupPending.add(ryder);
            }
        }
        //System.out.println(pickupPending + " " + ryders);
        while (!pickupPending.isEmpty()) {
            ArrayList<ArrayList<Location>> closestRyder = new ArrayList<>();
            // find the shortest path to a user, move, keep repeating until collected all users
            for (User ryder : pickupPending) {
                if (!ryder.isInRide()) {
            //        System.out.println(currentLocation + "  " + currentLocation.shortestPath(ryder.getStart(),graph)+ " " + ryder.getStart());
                    closestRyder.add(currentLocation.shortestPath(ryder.getStart(), graph));
                }
            }
            // find the shortest path out of all the users
         //   System.out.println(closestRyder.get(0));
            ArrayList<Location> closest = closestRyder.get(0);
            for (ArrayList<Location> path : closestRyder) {
                if (currentLocation.pathLength(path) < currentLocation.pathLength(closest)) {
                    closest = new ArrayList<>(path);
                }
            }
            isHeading = closest.get(closest.size()-1);
            //combinedPath = currentLocation.shortestPath(closest.get(closest.size()-1),graph);
            //combinedPath = new ArrayList<>(closest);
            // add to total path for the driver
            // for (Location step: closest){
            //     if (combinedPath.size() == 0){
            //         combinedPath.add(step);
            //     }
            //     else {
            //         if (!combinedPath.get(combinedPath.size()-1).equals(step)){
            //             combinedPath.add(step);
            //         }
            //     }
            // }
            System.out.println("closest: "+closest);
            // print and move 
            System.out.println("Path to the ryders: ");
            if(closest.size()==1){
                System.out.print(closest.get(0));
                for (User ryder : ryders) {
                    if (currentLocation.compare(currentLocation, ryder.getStart())) {
                        send("aboard:"+ryder.getNumber()+","+getNumber());
                        ryder.setRideStatus(true);
                    }
                }
            } else {
                for (int i = 0; i < closest.size() - 1; i++) {
                    // System.out.print(closest.get(i).getName() + ", ");
                    // if (i == closest.size() - 2) {
                    //     System.out.println(closest.get(closest.size() - 1).getName());
                    // }
                    moveSteps(closest.get(i), closest.get(i + 1));
                }
            }

            //System.out.println("Driver's new location after picking up: " + currentLocation.getName());

            // remove the user from the list to be picked up
            ArrayList<User> temp = new ArrayList<>(pickupPending);
            for (User ryder : temp) {
                if (ryder.getStart().getX() == currentLocation.getX() && ryder.getStart().getY() == currentLocation.getY()) {
                    pickupPending.remove(ryder);
                }
            }
        }
        // drop off ryders
        combinedPath.clear();
     //   System.out.println(getRydeInfo());
        ArrayList<User> dropoffPending = new ArrayList<>();
        for (User ryder: ryders){
            if (ryder.isInRide()){
                dropoffPending.add(ryder);
            }
        }
        while (!dropoffPending.isEmpty()) {
            ArrayList<ArrayList<Location>> closestStop = new ArrayList<>();
            // find the shortest path to a user, move, keep repeating until collected all
            // users
            for (User ryder : dropoffPending) {
               // System.out.println("ryder in dropoff pending: "+ryder.toString());
                if (ryder.isInRide()) {
                    closestStop.add(currentLocation.shortestPath(ryder.getEnd(), graph));
                }
            }
            // find the shortest path out of all the users
            ArrayList<Location> closest = closestStop.get(0);
            for (ArrayList<Location> path : closestStop) {
                if (currentLocation.pathLength(path) < currentLocation.pathLength(closest)) {
                    closest = new ArrayList<>(path);
                }
            }
            isHeading = closest.get(closest.size()-1);

            // gui.getMapPanel().setPathToDraw(combinedPath);
            //System.out.println(isHeading);
            // add to total path for driver
            // for (Location step: closest){
            //     if (combinedPath.size() == 0){
            //         combinedPath.add(step);
            //     }
            //     else {
            //         if (!combinedPath.get(combinedPath.size()-1).equals(step)){
            //             combinedPath.add(step);
            //         }
            //     }
            // }
           // System.out.println("combined path");
            for(Location path: combinedPath){
                System.out.println(path.toString());
            }
            // print and move 
            //System.out.println("Path to drop off: ");
            for (int i = 0; i < closest.size() - 1; i++) {
                System.out.print(closest.get(i).getName() + ", ");
                if (i == closest.size() - 2) {
                   System.out.println(closest.get(closest.size() - 1).getName());
                }
                moveSteps(closest.get(i), closest.get(i + 1));
            }
           // System.out.println("combined path");
            for(Location path: combinedPath){
               System.out.println(path.toString());
            }
            // remove the user from the list to be dropped off
            ArrayList<User> temp = new ArrayList<>(dropoffPending);
            for (User ryder : temp) {
                if (ryder.getEnd().getX() == currentLocation.getX() && ryder.getEnd().getY() == currentLocation.getY()) {
                    dropoffPending.remove(ryder);
                    ryder.reset();
                }
            }
        }
        System.out.println("asdfasdfadsfsdf");
        setDrive(false);
        setCurrentLocation(null);
        this.send("stopDriver:"+getNumber());
        updateGUI();
    }
    
    // public void ArrayList<>

    public void moveSteps(Location initial, Location next) {
        int x1 = (int) initial.getX();
        int y1 = (int) initial.getY();
        int x2 = (int) next.getX();
        int y2 = (int) next.getY();
        directionAngle = Math.atan2(y2-y1,x2-x1);

        // int dx = Math.abs(x2 - x1);
        // int dy = Math.abs(y2 - y1);
        // int sx = (x1 < x2) ? 1 : -1;
        // int sy = (y1 < y2) ? 1 : -1;
        // int err = dx - dy;

        while (!currentLocation.compare(currentLocation,next)) {
            x1 += 200 * Math.cos( directionAngle);
            y1 += 200 * Math.sin( directionAngle);
            currentLocation.setX(x1);
            currentLocation.setY(y1);

            // int e2 = 2 * err;

            // if (e2 > -dy) {
            //     err -= dy;
            //     x1 += sx;
            //     currentLocation.setX(x1);
            // }

            // if (e2 < dx) {
            //     err += dx;
            //     y1 += sy;
            //     currentLocation.setY(y1);
            // }
            // for (Location location : graph.getLocations().values()) {
            //     if (Math.abs(currentLocation.getX()-location.getX())<=100 && Math.abs(currentLocation.getY() - location.getY())<=100) {
            //         currentLocation = new Location(location.getName(), location.getX(), location.getY());
            //         for (Location connector : location.getConnections()) {
            //             currentLocation.addConnection(connector);
            //         }
            //     }
            // }
            // if (this.ryders != null) {
            //     for (User ryder : ryders) {
            //         if (ryder.isInRide()) {
            //             ryder.move(currentLocation.getX(), currentLocation.getY());
            //         }
            //     }
            // }
            if (Math.abs(currentLocation.getX()-next.getX())<=200 && Math.abs(currentLocation.getY() - next.getY())<=200){
                currentLocation = new Location(next.getName(), next.getX(), next.getY());
                for (Location connector : next.getConnections()) {
                    currentLocation.addConnection(connector);
                }
           //     Location last = combinedPath.get(combinedPath.size()-1);
              //  combinedPath = new ArrayList<Location>(currentLocation.shortestPath(last, graph));
            }
            for (User ryder : ryders) {
                if (currentLocation.compare(currentLocation, ryder.getStart())) {
                    send("aboard:"+ryder.getNumber()+","+getNumber());
                    ryder.setRideStatus(true);
                }
            }
            if (!this.ryders.isEmpty()) {
                ArrayList<User> temp = new ArrayList<>(ryders);
                for (User ryder : temp) {
                    if (ryder.isInRide() && currentLocation.compare(currentLocation, ryder.getEnd())) {
                        send("arrive:"+ryder.getNumber()+","+getNumber());
                        removeRyder(ryder);
                    }
                }
                temp = new ArrayList<>(ryders);
            } 
            if(this.ryders.isEmpty()){
                System.out.println("asdfasdfadsfsdf");
                setDrive(false);
                setCurrentLocation(null);
                this.send("stopDriver:"+getNumber());
                updateGUI();
            }
            //System.out.println("driver has "+ryders.size()+" ryders left");
            this.send("moveDriver:"+getNumber()+","+currentLocation.getName()+","+currentLocation.getX()+","+currentLocation.getY());
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
    
    public ArrayList<Location> getAllPaths(){
        return this.combinedPath;
    }

    // -------------------------------------------------------------------------
    @Override
    public void updateGUI() {
        SwingUtilities.invokeLater(() -> {
            InfoPanel infoPanel = gui.getInfoPanel();
            MapPanel mapPanel = gui.getMapPanel();
            String info = "Driver " + getNumber() + ": You have " + ryders.size() + " ryders.";
            for (User ryder : ryders) {
                info += "\nUser " + ryder.getNumber() + ":\nStart Location: " + ryder.getStart().toString() + "\nEnd Location: " + ryder.getEnd().toString();
            }
            if (hasCurrLocation()) {
                infoPanel.setLocationText(currentLocation.toString());
                infoPanel.confirmButton.setVisible(false);
                if (!isDrive()) {
                  //  System.out.println("ryders.size: "+ryders.size());
                    if(ryders.size() < getCapacity()){
                        if (ryders.size() == 1 && ryders.get(0).isAlone() == true){ // selected a user that rides alone
                         //   System.out.println("none. was used");
                            infoPanel.createRequest(Color.black, info, "none");
                        } 
                        else if (ryders.size() == 0){ // did not select a user yet
                     //       System.out.println("all. was used");
                            infoPanel.createRequest(Color.black, info,"all");
                        } else {    // selected a user that wants to carpool
                     //       System.out.println("carpool. was used");
                            infoPanel.createRequest(Color.black, info, "carpool");
                        }
                    }
                    //System.out.println("hasRyders: " + hasRyders());
                    if(hasRyders()){
                        System.out.println(combinedPath);
                        infoPanel.dButtonPanel.setVisible(true); // 
                        //mapPanel.setPathToDraw(combinedPath);
                    }else{
                        infoPanel.dButtonPanel.setVisible(false);
                    }
                } else {
                    System.out.println(combinedPath);
           //         System.out.println("location:"+currentLocation.toString());
              //      System.out.println("isDrive:" + isDrive());
                    // System.out.println(ryders.size() <= getCapacity());
                    //mapPanel.setPathToDraw(combinedPath);
                //    System.out.println("combinedPath:"+combinedPath.size());
                    infoPanel.dButtonPanel.setVisible(false);
                    infoPanel.displayInfo(Color.black, "Please go pick up your Ryder\n" + info);
                }
            } else {
                infoPanel.resetDisplay();
                //mapPanel.setPathToDraw(null);
                infoPanel.confirmButton.setVisible(true);
                infoPanel.displayInfo(Color.red, "Please Choose Your Current Location to Start Picking Customers");
            }
        });
    }

    @Override 
    public void draw(Graphics2D g2){
            if(hasCurrLocation()){
                int x = (int)(currentLocation.getX() - driverImage.getWidth(null) / 2.0);
                int y = (int)(currentLocation.getY()- driverImage.getHeight(null) / 2.0);
                if(isDrive()){
                    AffineTransform transform = new AffineTransform();
                    transform.translate(x, y);
                    transform.rotate(getDirectionAngle(), driverImage.getWidth(null) / 2.0, driverImage.getHeight(null) / 2.0);
                    g2.drawImage(driverImage, transform, null);
                }
                else{
                    g2.drawImage(driverImage,x,y,null);
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