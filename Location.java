import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Location {
    final int DIAMETER = 30;
    private String name;
    private double x;
    private double y;
    private Ellipse2D drawCircle;
    private Map<Location, Double> connections;
    private boolean isHovered;

    public Location(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        drawCircle = new Ellipse2D.Double(x - DIAMETER / 2, y - DIAMETER / 2, DIAMETER, DIAMETER);
        connections = new HashMap<Location, Double>();
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

    public void addConnection(Location other) {
        double xDifference = this.getX() - other.getX();
        double yDifference = this.getY() - other.getY();
        Double length = Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        System.out.println(other.toString() + length);
        connections.put(other, length);
    }

    public boolean contains(Point2D point) {
        return isHovered = drawCircle.contains(point);

    }

    public void draw(Graphics2D g2) {
        if (isHovered) {
            g2.setColor(new Color(139, 0, 0));
        } else {
            g2.setColor(Color.red);
        }

        // Draw the filled circle
        g2.fill(drawCircle);

        FontMetrics fontMetrics = g2.getFontMetrics();

        // Draw the white rectangle
        g2.setColor(Color.white);
        int rectWidth = fontMetrics.stringWidth(name)+2; // Adjust the width as needed
        int rectHeight = fontMetrics.getHeight(); // Adjust the height as needed
        int rectX = (int) (x - rectWidth / 2);
        int rectY = (int) (y + rectHeight);
        g2.fillRect(rectX, rectY, rectWidth, rectHeight);

        // Draw the text centered within the white rectangle
        g2.setColor(Color.BLACK);
        int textX = (int) (x - fontMetrics.stringWidth(name) / 2);
        int textY = rectY + (rectHeight - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        g2.drawString(name, textX, textY);
    }

    @Override
    public String toString() {
        return name;
    }

}
