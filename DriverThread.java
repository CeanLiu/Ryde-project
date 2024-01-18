import java.util.ArrayList;

public class DriverThread implements Runnable{

        ArrayList<Driver> drivers;
        Driver driver;
        int threadID;
        SimpleGraph graph;

        public DriverThread(SimpleGraph graph, ArrayList<Driver> drivers, ArrayList<User> passengers, int capacity, int driverID){
            this.graph = graph;
            this.drivers = drivers;
            this.threadID = driverID;
            this.driver = new Driver(passengers,capacity);
            drivers.add(driver);
        }

        @Override
        public void run() {
            // move the driver along the roads
            try {
                driver.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.move(graph);

            // for (Driver driver: drivers){
            //     driver.move(g);
            // }

            //pause thread execution for the duration of one video frame
            try{Thread.sleep(15);} catch (Exception e){e.printStackTrace();}
        }
    
}
