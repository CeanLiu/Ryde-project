import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Database {
    private HashMap<Long, User> users;
    private HashMap<Long, Driver> drivers;
    private String fileName;
    private SimpleGraph map;

    public Database(String fileName, SimpleGraph map) {
        this.fileName = fileName;
        this.users = new HashMap<>();
        this.drivers = new HashMap<>();
        this.map = map;
        //loadDatabase();
        
    }
    private void loadDatabase() {
        final int PHONE= 0;
        final int START_LOCAT = 1;
        final int END_LOCAT = 2;
        final int HAS_DRIVER = 3;
        final int CURR_LOCAT = 1;
        final int CAPCITY = 2;
        // add final indexes for box attributes
        try {
            Scanner input = new Scanner(new FileReader(fileName));
            boolean readUser = true;
            boolean readDriver = false;
            while (input.hasNext()) {
                String info = input.nextLine().trim();
                if (info.equals("Driver:")) {
                    readUser  = false;
                }
                if (readUser) {
                    String[] driverDetail = info.split(",");
                    long phoneNum = Long.parseLong(driverDetail[PHONE]);
                    Location startLocat = map.getLocation(driverDetail[START_LOCAT]);
                    Location endLocat = map.getLocation(driverDetail[END_LOCAT]);
                    boolean hasRide = ("true").equals(driverDetail[HAS_DRIVER]);
                    users.put(phoneNum, new User(phoneNum,startLocat,endLocat,hasRide));
                }else if (readDriver){
                    String[] driverDetail = info.split(",");
                    long phoneNum = Long.parseLong(driverDetail[PHONE]);
                    int capacity = Integer.parseInt(driverDetail[CAPCITY]);
                    Location currLocat = map.getLocation(driverDetail[CURR_LOCAT]);
                    drivers.put(phoneNum, new Driver(phoneNum, currLocat, capacity));
                }
                if (info.equals("Driver:")) {
                    readDriver = true;
                }
            }
            input.close();
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    public void saveDatabase() {
        for (User user : users.values()) {
            System.out.println("asdfasdfasdf");
        }
        try {
            PrintWriter writer = new PrintWriter(new File(this.fileName));
            for (User user : users.values()) {
                writer.println(user.getNumber());
                System.out.println("asdfasdfasdf");
            }
            writer.println("Driver:");
            for (Driver driver : drivers.values()) {
                writer.write(driver.toString());
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    public User getUser(long phoneNum){
        return users.get(phoneNum);
    }

    public Driver getDriver(long phoneNum){
        return drivers.get(phoneNum);
    }

    public void addUser(long phoneNum) {
        users.put(phoneNum, new User(phoneNum));
    }

    public void addDriver(long phoneNum, int capacity) {
        drivers.put(phoneNum, new Driver(map, phoneNum, capacity));
    }

    public void removeUser(long phoneNum) {
        users.remove(phoneNum);
    }

    public void removeDriver(long phoneNum) {
        drivers.remove(phoneNum);
    }
}
