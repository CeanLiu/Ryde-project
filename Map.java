import java.util.ArrayList;

public class Map {

    final char ROAD = 'R';
    final char STOPS = 'S';
    final char 
    Node<String> location;


    private static class Node<String>{
        String location; 
        ArrayList<Node<String>> neighbours = new ArrayList<Node<String>>();
        Node<String> prevLocation;

        public Node(String location){
            this.location = location;
            neighbours = null;
            prevLocation = null;
        }
        public void setNeighbours(ArrayList<Node<String>> neighbours){
            this.neighbours = neighbours;
            for (Node<String> location: neighbours){
                location.prevLocation = this;
            }
        }
    }
}
