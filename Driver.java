import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Driver extends Client {
    ArrayList<User> ryders;
    Long phoneNum; 
    // User ryder;
    int capacity;
    Location currentLocation;
    // ArrayList<Location> ryderStarts, ryderEnds;
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
        // this.ryder = ryder;
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
        this.ryders.remove(ryder);
        ryder.inRide = false;
        System.out.println("You've arrived at your destination, have a good day");
    }

    public void move() {
        // assign passenger before moving, ie after they pay
        // for (User ryder: this.ryders){
        //     ryderStarts.add(ryder.start);
        //     ryderEnds.add(ryder.end);
        // }

        System.out.println("\nDriver's current location: " + currentLocation.getName());
        // System.out.println("Start: "+ryderStart.getName());
        // System.out.println("End: " +ryderEnd.getName());

        //pickup ryders
        ArrayList<User> pickupPending = new ArrayList<>(ryders);
        while(!pickupPending.isEmpty()){
            ArrayList<ArrayList<Location>> closestRyder = new ArrayList<>();
            // find the shortest path to a user, move, keep repeating until collected all users
            for (User ryder: pickupPending){
                if (ryder.inRide == false){
                    closestRyder.add(createPath(currentLocation,ryder.start));
                }
            }
            ArrayList<Location> closest = closestRyder.get(0);
            for (ArrayList<Location> path: closestRyder){
                if (path.size() < closest.size()){
                    closest = path;
                }
            }
            // print and move 
            System.out.println("Path to the ryders: ");
            for (int i = closest.size()-1; i > 0; i--){
                System.out.print(closest.get(i).getName() + ", ");
                moveSteps(closest.get(i),closest.get(i-1));
            }
            System.out.println(closest.get(0).getName() + " || done");
            System.out.println("Driver's new location after picking up: " + currentLocation.getName());

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
                    //update currentLocation after moving
                    closestStop.add(createPath(currentLocation,ryder.end));
                }
            }
            ArrayList<Location> closest = closestStop.get(0);
            for (ArrayList<Location> path: closestStop){
                if (path.size() < closest.size()){
                    closest = path;
                }
            }
            // print and move 
            System.out.println("Path to drop off: ");
            for (int i = closest.size()-1; i > 0; i--){
                System.out.print(closest.get(i).getName() + ", ");
                moveSteps(closest.get(i),closest.get(i-1));
            }
            System.out.println(closest.get(0).getName());

            ArrayList<User> temp = new ArrayList<>(dropoffPending);
            for(User ryder: temp){
                if (ryder.end.getX() == currentLocation.getX() && ryder.end.getY() == currentLocation.getY()){
                    dropoffPending.remove(ryder);
                }
            }
        }

        
        // ArrayList<Location> currentToStart = shortestPath(currentLocation, ryderStart);
        // ArrayList<Location> startToEnd = shortestPath(ryderStart, ryderEnd);
        // ArrayList<Location> totalPath = new ArrayList<>();
        // //path for driver to get from current location to user's starting point
        // if (currentToStart == null){System.out.println("The driver cannot get to the starting point from their location");}
        // else {
        //     System.out.print("Path to ryder: ");
        //     for (int i = currentToStart.size()-1; i >= 0; i--){
        //         System.out.print(currentToStart.get(i).getName());
        //         totalPath.add(currentToStart.get(i));
        //     }
        //     System.out.println();
        //     //path for driver to get from user's starting point to their destination
        //     if (startToEnd == null){System.out.println("You cannot get from the starting to location to the destination");}
        //     else {
        //         System.out.print("Path to destination: ");
        //         for (int i = startToEnd.size()-1; i >= 0; i--){
        //             System.out.print(startToEnd.get(i).getName());
        //             totalPath.add(startToEnd.get(i));
        //         }
        //         System.out.println();
        //     }
        // }
        // //follow the path
        // for (int i = 0; i < totalPath.size()-1; i++){
        //     moveSteps(totalPath.get(i),totalPath.get(i+1));
        // }
    }

    public void moveSteps(Location initial, Location next) {// (Graphics g){
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
            // g.fillRect(x1, y1, 1, 1);
            // System.out.println("("+x1+","+y1+")");

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
                    currentLocation = location;
                }
            }
            for(User ryder: ryders){
                if(currentLocation.getX() == ryder.start.getX() && currentLocation.getY() == ryder.start.getY()){
                    addRyder(ryder);
                    // currentLocation = ryder.start;
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
                for(User ryder: ryders){
                    if(ryder.inRide == true && currentLocation.getX() == ryder.end.getX() && currentLocation.getY() == ryder.end.getY()){
                        removeRyder(ryder);
                        if(ryders.isEmpty()){break;}
                    }
                }
            }
        }
    }

    public ArrayList<Location> createPath(Location start, Location end) {
        // ArrayList<Point> pickups = new ArrayList<>();
        // for (User passenger: passengers){
        // pickups.add(graph.getCoordinates(passenger.start));
        // }
        // ArrayList<Point> destinations = new ArrayList<>();
        // for (User passenger: passengers){
        // destinations.add(graph.getCoordinates(passenger.destination));
        // }
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
        return null;
    }
    //-------------------------------------------------------------------------
    public ArrayList<Location> shortestPath(Location start, Location end){
        HashMap<Location,Double> distance = new HashMap<>();
        HashSet<Location> unvisited = new HashSet<>();
        Queue<Location> queue = new LinkedList<>();
        HashMap<Location,Location> connections = new HashMap<>();
        ArrayList<Location> path = new ArrayList<>();
        for (String name: graph.getLocations().keySet()){
            unvisited.add(graph.getLocation(name));
        }
        for (Location location: graph.getLocations().values()){
            distance.put(location,Double.MAX_VALUE);
        }
        distance.put(start,0.0);
        unvisited.remove(start);
        queue.add(start);

        while(!unvisited.isEmpty()){
            Location current = queue.remove();
            if (current.equals(end)){
                while(!current.equals(start)){
                    path.add(current);
                    current = connections.get(current);
                }
                path.add(current);
                return path;
            }
            // add all the distances of the connectors
            for(Location connector: current.getConnections()){
                if (unvisited.contains(connector)){
                    distance.put(connector,distance.get(current)+current.getEdge(connector));
                }
            }
            // find the shortest distance connector that isn't already visited
            Double length = Double.MAX_VALUE;
            Location closest = current;
            for (Location connector: distance.keySet()){
                if (unvisited.contains(connector) && distance.get(connector) < length){
                    closest = connector;
                }
            }
            unvisited.remove(closest);
            queue.add(closest);
            connections.put(closest,current);
        }
        return null;

    }
}
