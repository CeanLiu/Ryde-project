import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Custom Graph implementation
 * @author ICS4UE
 * @version Oct 2023
 */
class SimpleGraph{  
    BufferedImage mapImage; 
    private Map<String, Map<String,Double>> map;
    private Map<String, Point> coordinates;  
    //Constructor
    public SimpleGraph(){
        this.map = new HashMap<>();
        this.coordinates = new HashMap<>();
        loadMap("map.txt");
        try {

            // Load the image that will be shown in the panel
            mapImage = ImageIO.read(new File("mapImage.png"));

        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
        

    }
    private void loadMap(String file){
            final int LOCATION_NAME_INDEX = 0;
            final int LOCATION_X_INDEX = 1;
            final int LOCATION_Y_INDEX = 2;
            // add final indexes for box attributes 
            try{
              Scanner input = new Scanner(new FileReader(file));
              boolean readLocation= true;
              boolean readEdge = false;
              while (input.hasNext()){
                String location = input.nextLine().trim();
                if(location.equals("Edge:")){
                    readLocation = false;
                }
                if(readLocation){
                    String [] locationDetails = location.split(",");
                    String name = locationDetails[LOCATION_NAME_INDEX];
                    int x= Integer.parseInt(locationDetails[LOCATION_X_INDEX]);
                    int y = Integer.parseInt(locationDetails[LOCATION_Y_INDEX]);
                    addVertex(name,x,y);
                    System.out.println(name);
                }else if (readEdge){
                    String [] edgeDetails = location.split(":");
                    String src = edgeDetails[0];
                    String [] edges = edgeDetails[1].split(",");
                    for (int i = 0; i < edges.length; i++) {
                        addEdge(src, edges[i]);
                    }
                }
                if(location.equals("Edge:")){
                    readEdge = true;
                }
                
                // for (int i = 0; i < edgeDetails.length; i++) {
                //     addEdge(name,edgeDetails[i]);
                // }
                  // use variables for id, weight, etc. after confiming formatting of box info 
        //          trucks.get(truckIndex).addBox(new Box(10,10,boxLength,boxWidth,10,boxX,boxY,Color.red));
                }
                input.close();
              }catch(IOException ex){
              System.out.println(ex);
            }
          }
//------------------------------------------------------------------------------
    public Point getCoordinates(String location){
        return coordinates.get(location);
    }
    public Map<String,Point> getAllCoords(){
        return coordinates;
    }
    public Map<String,Double> getConnections(String location){
        return map.get(location);
    }
//------------------------------------------------------------------------------
    //Adds a vertex to the graph  
    public void addVertex(String location, int x, int y){   
        map.put(location, new HashMap<>());   
        coordinates.put(location, new Point(x,y));
    } 
    //Adds an edge between src and dest; adds src/dest vertices if they don't exist   
    public void addEdge(String src, String dest){  
        double xDifference = coordinates.get(src).getX() - coordinates.get(dest).getX();
        double yDifference = coordinates.get(src).getY() - coordinates.get(dest).getY();
        Double length = Math.sqrt(xDifference * xDifference + yDifference * yDifference);
        map.get(src).put(dest, length);  
        map.get(dest).put(src,length);   
    } 
//------------------------------------------------------------------------------  
    //Removes an edge between src and dest vertices
    // public boolean removeEdge(T src, T dest){
    //     if (map.containsKey(src) && map.containsKey(dest)){
    //         map.get(src).remove(dest);
    //         map.get(dest).remove(src);
    //         return true;
    //     }else {
    //         return false;
    //     }
    // }
    // //Removes an existing vertex and all edges from other vertices
    // public boolean removeVertex(T vertex){
    //     if (this.containsVertex(vertex)){
    //         for (T currentVertex : map.keySet()){   
    //             List<T> list = map.get(currentVertex);
    //             list.remove(vertex);
    //         }   
    //         map.remove(vertex);
    //         return true;
    //     }else {
    //         return false;
    //     }
    // }
//------------------------------------------------------------------------------  
    //Returns true if the given vertex exists and false otherwise
    public boolean containsVertex(String vertex){   
        return map.containsKey(vertex);   
    }   
    //Returns true if there is an edge between sorce and dest vertices
    public boolean containsEdge(String src, String dest){   
        return map.get(src).containsKey(dest);
    }    

    
//------------------------------------------------------------------------------   

    public void draw(Graphics2D g){ 
        final int DIAMETER = 30; 
        g.setColor(Color.orange);
        g.drawLine(0,0,0,1000) ;
        g.drawLine(0,0,1000,0) ;
        g.drawLine(0,0,1000,1000) ;

        g.setColor(Color.red);
        for (String currentVertex : coordinates.keySet()){
            Point point = coordinates.get(currentVertex);
            g.fillOval((int)point.getX()-DIAMETER/2, (int)point.getY()-DIAMETER/2,DIAMETER,DIAMETER);
        }
        g.setColor(Color.black);
        for (String currentVertex : map.keySet()){
            Map<String, Double> edges = map.get(currentVertex);
            Point srcPoint = coordinates.get(currentVertex);
            for (String edge : edges.keySet()){
                Point destPoint = coordinates.get(edge);
                g.drawLine((int)srcPoint.getX(), (int)srcPoint.getY(),(int)destPoint.getX(), (int)destPoint.getY());
            }
        }
        

        

    }         
}   
 