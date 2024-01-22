import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Driver extends Client {
    ArrayList<User> passengers;
    User passenger;
    int capacity;
    Location currentLocation, passengerStart, passengerEnd;
    UserPanel userPanel;

    // public Driver(ArrayList<User> passengers, int capacity) {
    //     this.passengers = passengers;
    //     for (User passenger: passengers){
    //         passenger.inRide = true;
    //     }
    //     this.capacity = capacity;
    //     this.currentLocation = "A";
    // }
    public Driver(Location current, int capacity){
        this.passenger = null;
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

    public void assignPassenger(User passenger){
        this.passenger = passenger;
    }
    public void addPassenger(User passenger){
        if (this.passenger.getNumber() == passenger.getNumber()){
            passenger.inRide = true;
            System.out.println("Welcome aboard, passenger " + this.passenger.getNumber());
        } else {
            System.out.println("This passenger has not been assigned to the driver");
        }
    }
    public void removePassenger(User passenger){
        this.passenger = null;
        passenger.inRide = false;
        System.out.println("You've arrived at your destination, have a good day");
    }
    public boolean getUpdates(){
        try {
            User temp = userPanel.getUser();
            assignPassenger(temp);
            return true;
        } catch (NullPointerException e) {
            System.out.println("There is no user yet");
            return false;
        }
    }

    public void move (){
        // assign passenger before moving, ie after they pay
        passengerStart = passenger.start;
        passengerEnd = passenger.end;

        System.out.println("Current location: " + currentLocation.getName());
        System.out.println("Start: "+passengerStart.getName());
        System.out.println("End: " +passengerEnd.getName());

        ArrayList<Location> currentToStart = createPath(currentLocation, passengerStart);
        ArrayList<Location> startToEnd = createPath(passengerStart, passengerEnd);
        ArrayList<Location> totalPath = new ArrayList<>();
        //path for driver to get from current location to user's starting point
        if (currentToStart == null){System.out.println("The driver cannot get to the starting point from their location");}
        else {
            System.out.print("Path to passenger: ");
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
            

            if(currentLocation.getX() == passengerStart.getX() && currentLocation.getY() == passengerStart.getY()){
                addPassenger(passenger);
            }
            if(this.passenger != null && passenger.inRide){
                passenger.move(currentLocation.getX(),currentLocation.getY());
            }
            if(this.passenger.inRide == true && currentLocation.getX() == passengerEnd.getX() && currentLocation.getY() == passengerEnd.getY()){
                removePassenger(passenger);
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
    public Point closestPoint(ArrayList<Point> pickups, ArrayList<Point> destinations){
        Point currentPoint = this.current;
        Point nextPoint = null;
        if (this.passengers.isEmpty()){
            for (Point pickup: pickups){
                //find distance
            }
        }
    }
    public int findDistance(Point current, Point other){

    }
}
