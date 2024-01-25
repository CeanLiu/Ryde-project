import javax.swing.SwingUtilities;

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
            String msg = driver.receive();
            if (msg != null) {
                // Ensure proper synchronization for database updates
                synchronized (db) {
                    db.update(msg);
                    db.saveDatabase();
                }
            }
            //runs the gui 
            SwingUtilities.invokeLater(() -> {
                driver.updateGUI();
            });
            // driver.move();

            // pause thread execution for the duration of one video frame
            try {
                Thread.sleep(15);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
