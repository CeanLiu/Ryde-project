import java.awt.Point;
import java.util.ArrayList;

public class Driver extends Client {
    ArrayList<User> passengers;
    int capacity;
    int x;
    int y;
    Point current;
    Point end;
    SimpleGraph graph;

    public Driver(ArrayList<User> passengers, int capacity) {
        this.passengers = passengers;
        this.capacity = capacity;
    }

    // Connection to server
    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public void move (SimpleGraph graph){
        this.graph = graph;
        User passenger = passengers.get(0);
        current = this.graph.getCoordinates(passenger.start);
        end = this.graph.getCoordinates(passenger.destination);

        System.out.println("Start: "+current);
        System.out.println("End: " +end);

        moveSteps();

    }
    public void moveSteps (){//(Graphics g){
        int x1 = (int)current.getX();
        int y1 = (int)current.getY();
        int x2 = (int)end.getX();
        int y2 = (int)end.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // g.fillRect(x1, y1, 1, 1);
            System.out.println("("+x1+","+y1+")");

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
