import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Database {
    final int PHONE = 0;
    final int START_LOCAT = 1;
    final int END_LOCAT = 2;
    final int IS_ALONE = 3;
    final int IN_RIDE = 4;

    final int CURR_LOCAT = 1;
    final int CAPCITY = 2;
    final int IS_DRIVE = 3;
    final int ANGLE = 4;
    final int X = 4;
    final int Y = 5;
    private final String CLIENT_FILE = "clients.txt";
    private final String RYDE_INFO_FILE = "rydeInfo.txt";
    private final String USER_IMAGE = "user.png";
    private final String DRIVER_IMAGE = "driver.png";
    private File clientFile;
    private File rydeInfoFile;
    private BufferedImage userImage;
    private BufferedImage driverImage;
    private HashMap<Long, User> users;
    private HashMap<Long, Driver> drivers;
    private SimpleGraph map;
    private Interface gui;

    public Database(SimpleGraph map, Interface gui) {
        this.gui = gui;
        this.users = new HashMap<>();
        this.drivers = new HashMap<>();
        this.clientFile = new File(CLIENT_FILE);
        this.rydeInfoFile = new File(RYDE_INFO_FILE);
        this.map = map;
        try {
            userImage = ImageIO.read(new File(USER_IMAGE));
        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
        try {
            driverImage = ImageIO.read(new File(DRIVER_IMAGE));
        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
        loadDatabase();
    }

    public void loadDatabase() {
        users.clear();
        drivers.clear();
        try {
            Scanner input = new Scanner(new FileReader(CLIENT_FILE));
            boolean readUser = true;
            while (input.hasNext()) {
                String infoLine = input.nextLine().trim();
                String[] info = infoLine.split(":");
                if (info[0].equals("Driver")) {
                    readUser = false;
                } else {
                    readUser = true;
                }
                if (readUser) {
                    String[] driverDetail = info[1].split(",");
                    long phoneNum = Long.parseLong(driverDetail[PHONE]);
                    Location startLocat = map.getLocation(driverDetail[START_LOCAT]);
                    Location endLocat = map.getLocation(driverDetail[END_LOCAT]);
                    boolean isAlone = ("true").equals(driverDetail[IS_ALONE]);
                    boolean hasRide = ("true").equals(driverDetail[IN_RIDE]);
                    users.put(phoneNum, new User(userImage, gui, phoneNum, startLocat, endLocat, isAlone, hasRide));
                } else {
                    String[] driverDetail = info[1].split(",");
                    long phoneNum = Long.parseLong(driverDetail[PHONE]);
                    Location currLocat = map.getLocation(driverDetail[CURR_LOCAT]);
                    int capacity = Integer.parseInt(driverDetail[CAPCITY]);
                    boolean isDrive = ("true").equals(driverDetail[IS_DRIVE]);
                    drivers.put(phoneNum, new Driver(driverImage, gui, map, phoneNum, currLocat, capacity, isDrive));
                }
            }
            input = new Scanner(new FileReader(RYDE_INFO_FILE));
            while (input.hasNext()) {
                String rydeInfo = input.nextLine().trim();
                String[] infoDetail = rydeInfo.split(":");
                Driver driver = drivers.get(Long.parseLong(infoDetail[0]));
                String[] ryders = infoDetail[1].split(",");
                for (String ryder : ryders) {
                    User user = users.get(Long.parseLong(ryder));
                    driver.assignRyder(user);
                    user.setDriver(driver);
                }
            }
            input.close();
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    public void saveDatabase() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(clientFile, false));
            for (User user : users.values()) {
                writer.println(user.toString());
            }
            for (Driver driver : drivers.values()) {
                writer.println(driver.getInfo());
            }
            writer.close();
            PrintWriter rydeWriter = new PrintWriter(new FileOutputStream(rydeInfoFile, false));
            for (Driver driver : drivers.values()) {
                if (driver.hasRyders()) {
                    rydeWriter.println(driver.getNumber() + ":" + driver.getRydeInfo());
                }
            }
            rydeWriter.close();
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    public User getUser(long phoneNum) {
        return users.get(phoneNum);
    }

    public Driver getDriver(long phoneNum) {
        return drivers.get(phoneNum);
    }

    public HashMap<Long, User> getUsers() {
        return this.users;
    }

    public HashMap<Long, Driver> getDrivers() {
        return this.drivers;
    }

    public void addUser(long phoneNum) {
        users.put(phoneNum, new User(userImage, gui,phoneNum));
    }

    public void addDriver(long phoneNum, int capacity) {
        if (drivers.containsKey(phoneNum)) {
            if (capacity > drivers.get(phoneNum).getCapacity()) {
                drivers.get(phoneNum).setCapacity(capacity);
            }
            drivers.get(phoneNum).setDrive(false);
        } else {
            drivers.put(phoneNum, new Driver(driverImage, gui, map, phoneNum, capacity));
        }
    }

    public void update(String dataReceived) {
        String type = dataReceived.split(":")[0];
        String textDetail = dataReceived.split(":")[1];
        if (type.equals("request")) {
            requestUser(textDetail);
        }
        if (type.equals("accept")) {
            acceptRequest(textDetail);
        }
        if (type.equals("moveDriver")) {
            moveDriver(textDetail);
        }
        if (type.equals("aboard")) {
            aboardRide(textDetail);
        }
        if (type.equals("arrive")) {
            arriveRide(textDetail);
        }
        if (type.equals("stopDriver")) {
            stopDriver(textDetail);
        }
        if (type.equals("newUser")) {
            addUser(Long.parseLong(textDetail));
        }
        if (type.equals("newDriver")) {
            addDriver(Long.parseLong(textDetail.split(",")[0]), Integer.parseInt(textDetail.split(",")[1]));
        }
    }

    public void requestUser(String requestText) {
        String[] userDetail = requestText.split(",");
        long phoneNum = Long.parseLong(userDetail[PHONE]);
        Location endLocation = map.getLocation(userDetail[END_LOCAT]);
        Location startLocation = map.getLocation(userDetail[START_LOCAT]);
        boolean isAlone = userDetail[IS_ALONE].equals("true");
        User user = users.get(phoneNum);
        user.setStart(startLocation);
        user.setEnd(endLocation);
        user.setChoice(isAlone);
    }

    public void acceptRequest(String acceptText) {
        long userPhone = Long.parseLong(acceptText.split(",")[PHONE]);
        long driverPhone = Long.parseLong(acceptText.split(",")[1]);
        Location current = map.getLocation(acceptText.split(",")[2]);
        User user = users.get(userPhone);
        Driver driver = drivers.get(driverPhone);
        driver.setCurrent(current);
        driver.assignRyder(user);
        user.setDriver(driver);
    }

    public void moveDriver(String moveText) {
        long driverPhone = Long.parseLong(moveText.split(",")[PHONE]);
        Location current = map.getLocation(moveText.split(",")[1]);
        double x = Double.parseDouble(moveText.split(",")[2]);
        double y = Double.parseDouble(moveText.split(",")[3]);
        double directionAngle = Double.parseDouble(moveText.split(",")[4]);
        Driver driver = drivers.get(driverPhone);
        driver.setDrive(true);
        driver.setCurrent(current);
        driver.getCurrent().setX(x);
        driver.getCurrent().setY(y);
        driver.setDirectionAngle(directionAngle);
    }

    public void aboardRide(String aboardText) {
        long userPhone = Long.parseLong(aboardText.split(",")[PHONE]);
        long driverPhone = Long.parseLong(aboardText.split(",")[1]);
        User user = users.get(userPhone);
        Driver driver = drivers.get(driverPhone);
        user.setCurrent(driver.getCurrent());
        user.setRideStatus(true);
    }

    public void arriveRide(String arriveText) {
        long userPhone = Long.parseLong(arriveText.split(",")[PHONE]);
        long driverPhone = Long.parseLong(arriveText.split(",")[1]);
        User user = users.get(userPhone);
        Driver driver = drivers.get(driverPhone);
        user.setDriver(null);
        user.setEnd(null);
        user.setStart(null);
        user.setRideStatus(false);
        driver.removeRyder(user);
    }

    public void stopDriver(String stopText) {
        long phoneNum = Long.parseLong(stopText);
        Driver driver = drivers.get(phoneNum);
        driver.setCurrent(null);
        driver.setDrive(false);
    }
}
