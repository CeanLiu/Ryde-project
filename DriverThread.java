import java.util.ArrayList;

public class DriverThread implements Runnable{

        ArrayList<Driver> drivers;
        Driver driver;
        int threadID;
        SimpleGraph graph;

        public DriverThread(SimpleGraph graph, ArrayList<Driver> drivers, int capacity, int driverID){
            this.graph = graph;
            this.drivers = drivers;
            this.threadID = driverID;
            this.driver = new Driver(graph,graph.getLocation("Denver"),capacity);
            drivers.add(driver);
        }

        @Override
        public void run() {
            try {
                driver.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.move();
            
            //pause thread execution for the duration of one video frame
            try{Thread.sleep(15);} catch (Exception e){e.printStackTrace();}
        }
    
}
