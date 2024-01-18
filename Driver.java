import java.util.ArrayList;

public class Driver extends Client{
    ArrayList<User> passengers;
    int capacity;
    int x;
    int y;
    int[] current = new int[2];
    int[] end = new int[2];
    SimpleGraph graph;

    public Driver (ArrayList<User> passengers, int capacity){
        this.passengers = passengers;
        this.capacity = capacity;
    }

    //Connection to server
    @Override
    public void start() throws Exception{
        super.start();
    }
    @Override
    public void stop() throws Exception{
        super.stop();
    }

    public void move (){
        User passenger = passengers.get(0);
        current = graph.coordinates.get(passenger.start);
    }
    public void move (Graphics g){
        int x1 = current[0];
        int y1 = current[1];
        int x2 = end[1];
        int y2 = end[2];
        
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            g.fillRect(x1, y1, 1, 1);

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
    
}
