import java.awt.Point;
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

public class Driver extends Client {
    ArrayList<User> ryders = new ArrayList<>();
    Long phoneNum; 
    int capacity;
    Location currentLocation;
    SimpleGraph graph;

    // public Driver(ArrayList<User> passengers, int capacity) {
    // this.passengers = passengers;
    // for (User passenger: passengers){
    // passenger.inRide = true;
    // }
    // this.capacity = capacity;
    // this.currentLocation = "A";
    // }

    public Driver(SimpleGraph graph, long phoneNum, int capacity){
        this.phoneNum = phoneNum;
        this.capacity = capacity;
        this.currentLocation = graph.getLocation("Central Park");
        this.graph = graph;
    }

    public Driver(long phoneNum, Location current, int capacity){
        this.phoneNum = phoneNum;
        this.currentLocation = current;
        this.capacity = capacity;
    }
    
    public Driver(SimpleGraph graph, Location current, int capacity){
        this.graph = graph;
        this.ryders = null;
        this.currentLocation = current;
        this.capacity = capacity;
    }

    public long getNumber(){
        return this.phoneNum;
    }

    public void setCurrentLocation(Location location){
        this.currentLocation = location;
    }
    // Connection to server
    public void start() throws Exception {
        super.start("driver");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void assignRyder(User ryder){
        this.ryders.add(ryder);
    }
    public void addRyder(User newRyder){
        for (User ryder: this.ryders){
            if (ryder.getNumber() == newRyder.getNumber()){
                newRyder.inRide = true;
                System.out.println("Welcome aboard, ryder " + newRyder.getNumber());
                return;
            }
        }
    }
    public void removeRyder(User ryder){
        ryder.inRide = false;
        ryders.remove(ryder);
        System.out.println("You've arrived at your destination, have a good day");
    }

    public void move() {
        System.out.println("\nDriver's current location: " + currentLocation.getName());

        //pickup ryders
        ArrayList<User> pickupPending = new ArrayList<>(ryders);
        while(!pickupPending.isEmpty()){
            ArrayList<ArrayList<Location>> closestRyder = new ArrayList<>();
            // find the shortest path to a user, move, keep repeating until collected all users
            for (User ryder: pickupPending){
                if (ryder.inRide == false){
                    closestRyder.add(shortestPath(currentLocation,ryder.start));
                }
            }
            // find the shortest path out of all the users
            ArrayList<Location> closest = closestRyder.get(0);
            for (ArrayList<Location> path: closestRyder){
                if (pathLength(path) < pathLength(closest)){
                    closest = new ArrayList<>(path);
                }
            }
            // print and move 
            System.out.println("Path to the ryders: ");
            for (int i = 0; i < closest.size()-1; i++){
                System.out.print(closest.get(i).getName() + ", ");
                if(i==closest.size()-2){
                    System.out.println(closest.get(closest.size()-1).getName());
                }
                moveSteps(closest.get(i),closest.get(i+1));
            }

            System.out.println("Driver's new location after picking up: " + currentLocation.getName());

            // remove the user from the list to be picked up
            ArrayList<User> temp = new ArrayList<>(pickupPending);
            for(User ryder: temp){
                if (ryder.start.getX() == currentLocation.getX() && ryder.start.getY() == currentLocation.getY()){
                    pickupPending.remove(ryder);
                }
            }
        }
        // drop off ryders
        ArrayList<User> dropoffPending = new ArrayList<>(ryders);
        while(!dropoffPending.isEmpty()){
            ArrayList<ArrayList<Location>> closestStop = new ArrayList<>();
            // find the shortest path to a user, move, keep repeating until collected all users
            for (User ryder: dropoffPending){
                if (ryder.inRide == true){
                    closestStop.add(shortestPath(currentLocation,ryder.end));
                }
            }
            // find the shortest path out of all the users
            ArrayList<Location> closest = closestStop.get(0);
            for (ArrayList<Location> path: closestStop){
                if (pathLength(path) < pathLength(closest)){
                    closest = new ArrayList<>(path);
                }
            }
            // print and move 
            System.out.println("Path to drop off: ");
            for (int i = 0; i < closest.size()-1; i++){
                System.out.print(closest.get(i).getName() + ", ");
                if(i==closest.size()-2){
                    System.out.println(closest.get(closest.size()-1).getName());
                }
                moveSteps(closest.get(i),closest.get(i+1));
            }
            // remove the user from the list to be dropped off
            ArrayList<User> temp = new ArrayList<>(dropoffPending);
            for(User ryder: temp){
                if (ryder.end.getX() == currentLocation.getX() && ryder.end.getY() == currentLocation.getY()){
                    dropoffPending.remove(ryder);
                }
            }
        }
    }

    public void moveSteps(Location initial, Location next) {
        int x1 = (int) initial.getX();
        int y1 = (int) initial.getY();
        int x2 = (int) next.getX();
        int y2 = (int) next.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
                currentLocation.setX(x1);
            }

            if (e2 < dx) {
                err += dx;
                y1 += sy;
                currentLocation.setY(y1);
            }
            for(Location location: graph.getLocations().values()){
                if(currentLocation.getX() == location.getX() && currentLocation.getY() == location.getY()){
                    currentLocation = new Location(location.getName(), location.getX(), location.getY());
                    for(Location connector: location.getConnections()){
                        currentLocation.addConnection(connector);
                    }
                }
            }
            for(User ryder: ryders){
                if(currentLocation.compare(currentLocation, ryder.start)){
                    addRyder(ryder);
                }
            }
            if(this.ryders != null){
                for (User ryder: ryders){
                    if(ryder.inRide){
                        ryder.move(currentLocation.getX(),currentLocation.getY());
                    }
                }
            }
            if(!this.ryders.isEmpty()){
                ArrayList<User> temp = new ArrayList<>(ryders);
                for(User ryder: temp){
                    if (ryder.inRide && currentLocation.compare(currentLocation,ryder.end)){
                        removeRyder(ryder);
                    }
                }
                temp = new ArrayList<>(ryders);
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
    //-------------------------------------------------------------------------
    public ArrayList<Location> shortestPath(Location start, Location end){ // shortest distance
        HashMap<Location,Double> distance = new HashMap<>();
        HashSet<Location> unvisited = new HashSet<>();
        HashMap<Location,Location> previous = new HashMap<>();
        ArrayList<Location> path = new ArrayList<>();

        PriorityQueue<Location> queue = new PriorityQueue<>(Comparator.comparingDouble(distance::get));

        for (Location location: graph.getLocations().values()){
            distance.put(location,Double.MAX_VALUE);
            unvisited.add(location);
            previous.put(location,null);
        }
        distance.put(start,0.0);
        queue.add(start);

        while(!queue.isEmpty()){
            Location current = queue.poll();
            unvisited.remove(current);

            if (current.equals(end)){
                while(current != null){
                    path.add(current);
                    current = previous.get(current);
                }
                Collections.reverse(path);
                return path;
            }
            // add all the distances of the connectors
            for(Location connector: current.getConnections()){
                double newDistance = distance.get(current) + current.getEdge(connector);
                if (newDistance < distance.get(connector)){
                    distance.put(connector,newDistance);
                    previous.put(connector,current);
                    queue.add(connector);
                }
            }

        }
        return null;

    }
    public double pathLength(ArrayList<Location> path){
        double length = 0;
        for (int i = 0; i < path.size()-1; i++){
            length = length + path.get(i).getEdge(path.get(i+1));
        }
        return length;
    }
}