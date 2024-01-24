import java.util.ArrayList;

public class DriverThread implements Runnable {
    private Database db;
    private Driver driver;
    private long phoneNum;

    public DriverThread(Database db, long phoneNum) {
        this.db = db;
        this.phoneNum = phoneNum;
        this.driver = db.getDriver(phoneNum);
    }

    @Override
    public void run() {
        try {
            driver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (driver.hasNoRyders()) {
            String msg = driver.receive();
            if(msg != null){
                db.addRequestingUser(msg);
                if(driver.hasCurrLocation()){
                    driver.createRequestGui();
                }   
            }
        }
        // driver.move();

        // pause thread execution for the duration of one video frame
        try {
            Thread.sleep(15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
