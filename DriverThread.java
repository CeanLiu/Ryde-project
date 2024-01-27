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

        // Create a timer to update the GUI at regular intervals
        Timer timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDriver();
            }
        });
        timer.start();

        while (true) {
            System.out.println("driver thread started");
            String msg = driver.receive();
            if (msg != null) {
                // Ensure proper synchronization for database updates
                synchronized (db) {
                    db.update(msg);
                    db.saveDatabase();
                }
            }

            if (driver.isDrive()) {
                System.out.println("moved");
                driver.move();
            }

            // pause thread execution for the duration of one video frame
            try {
                Thread.sleep(12);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to update the GUI on the Event Dispatch Thread
    private void updateDriver() {
        //SwingUtilities.invokeLater(() -> {
            driver.updateGUI();
        //});
    }
}
