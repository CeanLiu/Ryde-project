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
    User ryder;
    int capacity;
    Location currentLocation, ryderStart, ryderEnd;
    SimpleGraph graph;

    // public Driver(ArrayList<User> passengers, int capacity) {
    //     this.passengers = passengers;
    //     for (User passenger: passengers){
    //         passenger.inRide = true;
    //     }
    //     this.capacity = capacity;
    //     this.currentLocation = "A";
    // }
    public Driver(SimpleGraph graph, Location current, int capacity){
        this.graph = graph;
        this.ryder = null;
        this.currentLocation = current;
        this.capacity = capacity;
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
        this.ryder = ryder;
    }
    public void addRyder(User ryder){
        if (this.ryder.getNumber() == ryder.getNumber()){
            ryder.inRide = true;
            System.out.println("Welcome aboard, ryder " + this.ryder.getNumber());
        } else {
            System.out.println("This ryder has not been assigned to the driver");
        }
    }
    public void removeRyder(User ryder){
        this.ryder = null;
        ryder.inRide = false;
        System.out.println("You've arrived at your destination, have a good day");
    }

    public void move (){
        // assign passenger before moving, ie after they pay
        ryderStart = ryder.start;
        ryderEnd = ryder.end;

        System.out.println("Current location: " + currentLocation.getName());
        System.out.println("Start: "+ryderStart.getName());
        System.out.println("End: " +ryderEnd.getName());

        ArrayList<Location> currentToStart = shortestPath(currentLocation, ryderStart);
        ArrayList<Location> startToEnd = shortestPath(ryderStart, ryderEnd);
        ArrayList<Location> totalPath = new ArrayList<>();
        //path for driver to get from current location to user's starting point
        if (currentToStart == null){System.out.println("The driver cannot get to the starting point from their location");}
        else {
            System.out.print("Path to ryder: ");
            for (int i = currentToStart.size()-1; i >= 0; i--){
                System.out.print(currentToStart.get(i).getName());
                totalPath.add(currentToStart.get(i));
            }
            System.out.println();
            //path for driver to get from user's starting point to their destination
            if (startToEnd == null){System.out.println("You cannot get from the starting to location to the destination");}
            else {
                System.out.print("Path to destination: ");
                for (int i = startToEnd.size()-1; i >= 0; i--){
                    System.out.print(startToEnd.get(i).getName());
                    totalPath.add(startToEnd.get(i));
                }
                System.out.println();
            }
        }
        //follow the path
        for (int i = 0; i < totalPath.size()-1; i++){
            moveSteps(totalPath.get(i),totalPath.get(i+1));
        }
    }
    public void moveSteps (Location initial, Location next){//(Graphics g){
        int x1 = (int)initial.getX();
        int y1 = (int)initial.getY();
        int x2 = (int)next.getX();
        int y2 = (int)next.getY();

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
            

            if(currentLocation.getX() == ryderStart.getX() && currentLocation.getY() == ryderStart.getY()){
                addRyder(ryder);
            }
            if(this.ryder != null && ryder.inRide){
                ryder.move(currentLocation.getX(),currentLocation.getY());
            }
            if(this.ryder != null && this.ryder.inRide == true && currentLocation.getX() == ryderEnd.getX() && currentLocation.getY() == ryderEnd.getY()){
                removeRyder(ryder);
            }
        }
    }
    public ArrayList<Location> createPath(Location start, Location end){
        // ArrayList<Point> pickups = new ArrayList<>();
        // for (User passenger: passengers){
        //     pickups.add(graph.getCoordinates(passenger.start));
        // }
        // ArrayList<Point> destinations = new ArrayList<>();
        // for (User passenger: passengers){
        //     destinations.add(graph.getCoordinates(passenger.destination));
        // }
        ArrayList<Location> path = new ArrayList<>();
        Queue<Location> queue = new LinkedList<>();
        HashSet<Location> visited = new HashSet<>();
        HashMap<Location,Location> connections = new HashMap<>();
        queue.add(start);
        visited.add(start);

        while(!queue.isEmpty()){
            Location current = queue.remove();
            if (current.equals(end)){
                while(!current.equals(start)){
                    path.add(current);
                    current = connections.get(current);
                }
                path.add(current);
                return path;
            }
            for (Location connector: current.getConnections()){
                if (!visited.contains(connector)){
                    queue.add(connector);
                    visited.add(connector);
                    connections.put(connector,current);
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
