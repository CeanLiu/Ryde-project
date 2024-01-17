import java.util.ArrayList;

public class Driver {
    ArrayList<User> passengers;
    int capacity;
    int x;
    int y;
    int[] current = new int[2];
    int[] end = new int[2];
    Map map;

    public Driver (ArrayList<User> passengers, int capacity){
        this.passengers = passengers;
        this.capacity = capacity;
    }

    public void move (Driver driver, Graphics g){
        int x1 = driver.current[0];
        int y1 = driver.current[1];
        int x2 = driver.end[1];
        int y2 = driver.end[2];
        
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
    class DriverThread implements Runnable{

        ArrayList<Driver> drivers;
        Driver driver;
        int threadID;

        public DriverThread(ArrayList<Driver> drivers, ArrayList<User> passengers, int capacity, int driverID){
            this.drivers = drivers;
            this.threadID = driverID;
            this.driver = new Driver(passengers,capacity);
            drivers.add(driver);
        }

        @Override
        public void run() {
            // move the driver along the roads
            for (Driver driver: drivers){
                move(driver);
            }

            //pause thread execution for the duration of one video frame
            try{Thread.sleep(15);} catch (Exception e){e.printStackTrace();}
        }
        
    }
}
