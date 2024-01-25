
import java.util.Scanner;

import javax.imageio.ImageIO;
import java.util.HashMap;
import java.awt.Graphics2D;
import java.awt.Color;
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
    private HashMap<String, Location> locations;

    // Constructor
    public SimpleGraph() {
        this.locations = new HashMap<String, Location>();
        // this.coordinates = new HashMap<>();
        loadMap("map.txt");
        try {
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
            boolean readConnection = false;
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
                    locations.put(name, new Location(name, x, y));
                }else if (readConnection){
                    String[] connectDetail = location.split(":");
                    String src = connectDetail[0];
                    String[] connections = connectDetail[1].split(",");
                    for (int i = 0; i < connections.length; i++) {
                        locations.get(src).addConnection(locations.get(connections[i]));
                    }
                }
                if (location.equals("Edge:")) {
                    readConnection = true;
                }
            }
            input.close();
            System.out.println(this.toString());
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    public HashMap<String, Location> getLocations() {
        return this.locations;
    }

    public Location getLocation(String name) {
        return locations.get(name);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.orange);
        g2.drawLine(0, 0, 0, 1024);
        g2.drawLine(0, 0, 1024, 0);
        g2.drawLine(0, 0, 1024, 1024);
        // for (Location location : locations.values()) {
        //     location.drawAllEdge(g2);
        // }
        for (Location location : locations.values()) {
            location.drawVertex(g2);
        }
    }

    public Location contains(Point2D p) {
        for (Location location : locations.values()) {
            if (location.contains(p)) {
                return location;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String str = "";
        for (Location location : locations.values()) {
            str = str + "\n" + location.toString();
        }
        return str;
    }
}
