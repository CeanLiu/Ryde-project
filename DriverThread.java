import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        while (true) {
            System.out.println("driver thread repeated");
            String msg = driver.receive();
            if (msg != null) {
                // Ensure proper synchronization for database updates
                synchronized (db) {
                    db.update(msg);
                    db.saveDatabase();
                }
            }
            System.out.println("driver isdrive"+driver.isDrive());
            if (driver.isDrive()) {
                System.out.println("moved");
                driver.move();
                db.saveDatabase();
            // } else {
            //     driver.setCurrentLocation(null);
            //     driver.send("stopDriver:"+driver.getNumber());
            //     db.saveDatabase();
            }
            System.out.println("has currLocation" +driver.hasCurrLocation());
            SwingUtilities.invokeLater(() -> {
                driver.updateGUI();
            });

            // pause thread execution for the duration of one video frame
            try {
                Thread.sleep(12);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
