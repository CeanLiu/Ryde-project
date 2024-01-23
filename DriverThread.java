import java.util.ArrayList;

public class DriverThread implements Runnable{
        Database db;
        Driver driver;
        int threadID;

        public DriverThread(Database db, int driverID){
            this.db = db;
            this.threadID = driverID;
            this.driver = db.getDriver(driverID);
        }

        @Override
        public void run() {
            try {
                driver.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //driver.move();
            
            //pause thread execution for the duration of one video frame
            try{Thread.sleep(15);} catch (Exception e){e.printStackTrace();}
        }
    
}
