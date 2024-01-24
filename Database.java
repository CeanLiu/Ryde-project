import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Database {
    final int PHONE = 0;
    final int START_LOCAT = 1;
    final int END_LOCAT = 2;
    final int IS_ALONE = 3;
    final int IN_RIDE = 4;
    final int CURR_LOCAT = 1;
    final int CAPCITY = 2;
    private ArrayList<User> requestingUsers = new ArrayList<>();
    private ArrayList<Driver> freeDrivers = new ArrayList<>();
    private ArrayList<User> noRequestUsers = new ArrayList<>();
    private ArrayList<Driver> occupiedDrivers = new ArrayList<>();
    private HashMap<Long, User> users;
    private HashMap<Long, Driver> drivers;
    private String fileName;
    private SimpleGraph map;

    public Database(String fileName, SimpleGraph map) {
        this.fileName = fileName;
        this.users = new HashMap<>();
        this.drivers = new HashMap<>();
        this.map = map;
        // loadDatabase();

    }

    private void loadDatabase() {
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
                    boolean isAlone = ("true").equals(driverDetail[IS_ALONE]);
                    boolean hasRide = ("true").equals(driverDetail[IN_RIDE]);
                    users.put(phoneNum, new User(phoneNum,startLocat,endLocat,isAlone,hasRide));
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
        try {
            PrintWriter writer = new PrintWriter(new File(this.fileName));
            // for (User user : users.values()) {
            //     writer.println(user.getNumber());
            //     System.out.println("asdfasdfasdf");
            // }
            // writer.println("Driver:");
            // for (Driver driver : drivers.values()) {
            //     writer.write(driver.toString());
            // }

            // for(User user : requestingUsers){
            //     writer.println(user.)
            // }
            // writer.close();
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

    public void addUser(InfoPanel gui, long phoneNum) {
        users.put(phoneNum, new User(phoneNum, gui));
    }

    public ArrayList<User> getRequestList() {
        return requestingUsers;
    }

    public void addDriver(InfoPanel gui,long phoneNum, int capacity) {
        drivers.put(phoneNum, new Driver(map, gui, phoneNum, capacity));
    }

    public void addRequestingUser(String userText) {
        System.out.println("ok");
        String[] userDetail = userText.split(",");
        long phoneNum = Long.parseLong(userDetail[PHONE]);
        Location endLocation = map.getLocation(userDetail[END_LOCAT]);
        Location startLocation = map.getLocation(userDetail[START_LOCAT]);
        boolean isAlone = userDetail[IS_ALONE].equals("true");
        boolean inRide = userDetail[IN_RIDE].equals("true");
        if (users.containsKey(phoneNum)) {
            User user = users.get(phoneNum);
            user.setStart(startLocation);
            user.setEnd(endLocation);
            user.setChoice(isAlone);
            user.setStatus(inRide);
        } else {
            requestingUsers.add(new User(phoneNum, startLocation, endLocation, isAlone, inRide));
        }
    }

    public void removeUser(long phoneNum) {
        users.remove(phoneNum);
    }

    public void removeDriver(long phoneNum) {
        drivers.remove(phoneNum);
    }
}
