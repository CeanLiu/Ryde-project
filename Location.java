import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Location {
    final int DIAMETER = 60;
    private String name;
    private double x;
    private double y;
    private Ellipse2D drawCircle;
    private ArrayList<Location> connections;
    private boolean isHovered;

    public Location(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        drawCircle = new Ellipse2D.Double(x - DIAMETER / 2, y - DIAMETER / 2, DIAMETER, DIAMETER);
        connections = new ArrayList<Location>();
    }

    public void addConnection(Location other) {
        connections.add(other);
    }

    public String getName() {
        return this.name;
    }

    synchronized public double getX() {
        return this.x;
    }

    synchronized public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public ArrayList<Location> getConnections() {
        return this.connections;
    }

    public boolean contains(Point2D point) {
        return isHovered = drawCircle.contains(point);

    }

    public boolean compare(Location location1, Location location2) {
        return location1.getName().equals(location2.getName());
    }

    public double getEdge(Location other) {
        if (this.isConnected(other)) {
            double xDifference = this.getX() - other.getX();
            double yDifference = this.getY() - other.getY();
            Double length = Math.sqrt(xDifference * xDifference + yDifference * yDifference);
            return length;
        } else {
            return -1;
        }
    }

    public boolean isConnected(Location other) {
        for (Location location : connections) {
            if (compare(location, other)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Location> shortestPath(Location other, SimpleGraph graph){ // shortest distance from this to other
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
        distance.put(this,0.0);
        queue.add(this);

        while(!queue.isEmpty()){
            Location current = queue.poll();
            unvisited.remove(current);

            if (current.compare(current,other)){
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

    public void drawAllEdge(Graphics2D g2) {
        g2.setColor(Color.black);
        for (Location location : connections) {
            g2.setStroke(new BasicStroke(3));
            g2.drawLine((int) this.getX(), (int) this.getY(), (int) location.getX(), (int) location.getY());
        }
    }

    public void drawPath(Graphics2D g2, Location other){
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(10));
        g2.drawLine((int) this.getX(), (int) this.getY(), (int) other.getX(), (int) other.getY());
    }

    public void drawVertex(Graphics2D g2) {
        if (isHovered) {
            g2.setColor(new Color(139, 0, 0));
        } else {
            g2.setColor(Color.red);
        }
        g2.fill(drawCircle);

        g2.setFont(new Font("Arial", Font.PLAIN, 30)); 
        FontMetrics fontMetrics = g2.getFontMetrics();

        g2.setColor(Color.white);
        int rectWidth = fontMetrics.stringWidth(name) + 2;
        int rectHeight = fontMetrics.getHeight();
        int rectX = (int) (x - rectWidth / 2);
        int rectY = (int) (y + rectHeight+10);
        g2.fillRect(rectX, rectY, rectWidth, rectHeight);

        g2.setColor(Color.BLACK);
        int textX = (int) (x - fontMetrics.stringWidth(name) / 2);
        int textY = rectY + (rectHeight - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        g2.drawString(name, textX, textY);
    }

    @Override
    public String toString() {
        return getName();
    }
}
