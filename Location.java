import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Location {
    final int DIAMETER = 30;
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

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public ArrayList<Location> getConnections(){
        return this.connections;
    }

    public boolean contains(Point2D point) {
        return isHovered = drawCircle.contains(point);

    }

    public boolean compare(Location location1, Location location2) {
        return location1.getName().equals(location2.getName()) && location1.getX() == location2.getX()
                && location1.getY() == location2.getY();
    }

    public double getEdge(Location other) {
        if (this.isConnected(other)) {
            double xDifference = this.getX() - other.getX();
            double yDifference = this.getY() - other.getY();
            Double length = Math.sqrt(xDifference * xDifference + yDifference * yDifference);
            System.out.println(other.toString() + length);
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

    public void drawEdge(Graphics2D g2) {
        g2.setColor(Color.black);
        for (Location location : connections) {
            g2.setStroke(new BasicStroke(3));
            g2.drawLine((int) this.getX(), (int) this.getY(), (int) location.getX(), (int) location.getY());
        }
    }

    public void drawVertex(Graphics2D g2) {
        if (isHovered) {
            g2.setColor(new Color(139, 0, 0));
        } else {
            g2.setColor(Color.red);
        }
        g2.fill(drawCircle);

        FontMetrics fontMetrics = g2.getFontMetrics();

        g2.setColor(Color.white);
        int rectWidth = fontMetrics.stringWidth(name) + 2;
        int rectHeight = fontMetrics.getHeight();
        int rectX = (int) (x - rectWidth / 2);
        int rectY = (int) (y + rectHeight);
        g2.fillRect(rectX, rectY, rectWidth, rectHeight);

        g2.setColor(Color.BLACK);
        int textX = (int) (x - fontMetrics.stringWidth(name) / 2);
        int textY = rectY + (rectHeight - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        g2.drawString(name, textX, textY);
    }

    @Override
    public String toString() {
        String str = "";
        for (Location location : connections) {
            str = str + " " + location.getName();
        }
        return name + ": " + str;
    }

}
