import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Custom Graph implementation
 * 
 * @author ICS4UE
 * @version Oct 2023
 */
class SimpleGraph {
    BufferedImage mapImage;
    private ArrayList<Location> locations;
    private Map<String, Point> coordinates;

    // Constructor
    public SimpleGraph() {
        this.locations = new ArrayList<Location>();
        // this.coordinates = new HashMap<>();
        loadMap("map.txt");
        try {

            // Load the image that will be shown in the panel
            mapImage = ImageIO.read(new File("mapImage.png"));

        } catch (IOException ex) {
            System.out.println("No Image Found");
        }

    }

    private void loadMap(String file) {
        final int LOCATION_NAME_INDEX = 0;
        final int LOCATION_X_INDEX = 1;
        final int LOCATION_Y_INDEX = 2;
        // add final indexes for box attributes
        try {
            Scanner input = new Scanner(new FileReader(file));
            boolean readLocation = true;
            boolean readEdge = false;
            while (input.hasNext()) {
                String location = input.nextLine().trim();
                if (location.equals("Edge:")) {
                    readLocation = false;
                }
                if (readLocation) {
                    String[] locationDetails = location.split(",");
                    String name = locationDetails[LOCATION_NAME_INDEX];
                    double x = Double.parseDouble(locationDetails[LOCATION_X_INDEX]);
                    double y = Double.parseDouble(locationDetails[LOCATION_Y_INDEX]);
                    locations.add(new Location(name, x, y));
                    System.out.println(name);
                } else if (readEdge) {
                    String[] edgeDetails = location.split(":");
                    String srcStr = edgeDetails[0];
                    String[] edges = edgeDetails[1].split(",");
                    Location src = new Location("",0,0);
                    Location dest;
                    for (int i = 0; i < edges.length; i++) {
                        for(int k = 0; k< locations.size();k++){
                            if (locations.get(k).getName().equals(srcStr)){
                                src = locations.get(k);
                            }
                            if(locations.get(k).getName().equals(edges[i])){
                                dest = locations.get(k);
                                src.addConnection(dest);
                            }
                        }
                    }
                }
                if (location.equals("Edge:")) {
                    readEdge = true;
                }
            }
            input.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    public ArrayList<Location> getLocations(){
        return this.locations;
    }

    // ------------------------------------------------------------------------------
    // public Point getCoordinates(String location) {
    //     return coordinates.get(location);
    // }

    // public Map<String, Point> getAllCoords() {
    //     return coordinates;
    // }

    // public Map<String, Double> getConnections(String location) {
    //     return locations.get(location);
    // }

    // ------------------------------------------------------------------------------
    // Adds a vertex to the graph
    // public void addVertex(String location, int x, int y) {
    //     locations.put(location, new HashMap<>());
    //     coordinates.put(location, new Point(x, y));
    // }

    // // Adds an edge between src and dest; adds src/dest vertices if they don't exist
    // public void addEdge(String src, String dest) {
    //     double xDifference = coordinates.get(src).getX() - coordinates.get(dest).getX();
    //     double yDifference = coordinates.get(src).getY() - coordinates.get(dest).getY();
    //     Double length = Math.sqrt(xDifference * xDifference + yDifference * yDifference);
    //     locations.get(src).put(dest, length);
    //     locations.get(dest).put(src, length);
    // }

    // ------------------------------------------------------------------------------
    // Removes an edge between src and dest vertices
    // public boolean removeEdge(T src, T dest){
    // if (locations.containsKey(src) && locations.containsKey(dest)){
    // locations.get(src).remove(dest);
    // locations.get(dest).remove(src);
    // return true;
    // }else {
    // return false;
    // }
    // }
    // //Removes an existing vertex and all edges from other vertices
    // public boolean removeVertex(T vertex){
    // if (this.containsVertex(vertex)){
    // for (T currentVertex : locations.keySet()){
    // List<T> list = locations.get(currentVertex);
    // list.remove(vertex);
    // }
    // locations.remove(vertex);
    // return true;
    // }else {
    // return false;
    // }
    // }
    // ------------------------------------------------------------------------------
    // Returns true if the given vertex exists and false otherwise
    // public boolean containsVertex(String vertex) {
    //     return locations.containsKey(vertex);
    // }

    // // Returns true if there is an edge between sorce and dest vertices
    // public boolean containsEdge(String src, String dest) {
    //     return locations.get(src).containsKey(dest);
    // }

    // ------------------------------------------------------------------------------

    public void draw(Graphics2D g2) {
        final int DIAMETER = 30;
        g2.setColor(Color.orange);
        g2.drawLine(0, 0, 0, 1024);
        g2.drawLine(0, 0, 1024, 0);
        g2.drawLine(0, 0, 1024, 1024);
        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).draw(g2);
        }

        // g2.setColor(Color.red);
        // for (String currentVertex : coordinates.keySet()) {
        //     Point point = coordinates.get(currentVertex);
        //     Ellipse2D circle = new Ellipse2D.Double((int) point.getX() - DIAMETER / 2,(int) point.getY() - DIAMETER / 2, DIAMETER, DIAMETER);
        //     g2.fill(circle);
        // }
        // g2.setColor(Color.BLACK);
        // for (String currentVertex : locations.keySet()) {
        //     Map<String, Double> edges = locations.get(currentVertex);
        //     Point srcPoint = coordinates.get(currentVertex);
        //     for (String edge : edges.keySet()) {
        //         Point destPoint = coordinates.get(edge);
        //         g2.drawLine((int) srcPoint.getX(), (int) srcPoint.getY(), (int) destPoint.getX(),
        //                 (int) destPoint.getY());
        //     }
        // }

    }
    public Location contains(Point2D p){
        for (int i = 0; i < locations.size(); i++) {
            if(locations.get(i).contains(p)){
                return locations.get(i);
            }
        }
        return null;
    }
    public Location getLocation(String name){
        for (int i = 0; i < locations.size(); i++) {
            if(locations.get(i).getName().equals(name)){
                return locations.get(i);
            }
        }
        return null;
    }
}
