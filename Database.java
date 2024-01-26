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
    final int IS_DRIVE = 3;
    private final String CLIENT_FILE = "clients.txt";
    private final String RYDE_INFO_FILE = "rydeInfo.txt";
    private File clientFile;
    private File rydeInfoFile;
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
        loadDatabase();
    }

    private void loadDatabase() {
        // add final indexes for box attributes
        try {
            Scanner input = new Scanner(new FileReader(CLIENT_FILE));
            boolean readUser = true;
            while (input.hasNext()) {
                String infoLine = input.nextLine().trim();
                String [] info = infoLine.split(":");
                if(info[0].equals("Driver")){
                    readUser = false;
                }else {
                    readUser = true;
                }
                if (readUser) {
                    String[] driverDetail = info[1].split(",");
                    long phoneNum = Long.parseLong(driverDetail[PHONE]);
                    Location startLocat = map.getLocation(driverDetail[START_LOCAT]);
                    Location endLocat = map.getLocation(driverDetail[END_LOCAT]);
                    boolean isAlone = ("true").equals(driverDetail[IS_ALONE]);
                    boolean hasRide = ("true").equals(driverDetail[IN_RIDE]);
                    users.put(phoneNum, new User(gui,map, phoneNum,startLocat,endLocat,isAlone,hasRide));
                }else {
                    String[] driverDetail = info[1].split(",");
                    long phoneNum = Long.parseLong(driverDetail[PHONE]);
                    Location currLocat = map.getLocation(driverDetail[CURR_LOCAT]);
                    int capacity = Integer.parseInt(driverDetail[CAPCITY]);
                    boolean isDrive = ("true").equals(driverDetail[IS_DRIVE]);
                    drivers.put(phoneNum, new Driver(gui, map, phoneNum, currLocat, capacity, isDrive));
                }
            }
            input = new Scanner(new FileReader(RYDE_INFO_FILE));
            while (input.hasNext()) {
                String rydeInfo = input.nextLine().trim();
                String [] infoDetail = rydeInfo.split(":");
                Driver driver = drivers.get(Long.parseLong(infoDetail[0]));
                String [] ryders = infoDetail[1].split(",");
                for(String ryder : ryders){
                    User user =users.get(Long.parseLong(ryder));
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
            PrintWriter writer = new PrintWriter(new FileOutputStream(clientFile,false));
            for(User user : users.values()){
                writer.println(user.toString());
            }
            for(Driver driver : drivers.values()){
                writer.println(driver.getInfo());
            }
            writer.close();
            writer.flush();
            PrintWriter rydeWriter = new PrintWriter(new FileOutputStream(rydeInfoFile, false));
            for(Driver driver : drivers.values()){
                if(driver.hasRyders()){
                    rydeWriter.println(driver.getNumber() + ":" +driver.getRydeInfo());
                }
            }
            rydeWriter.close();
            rydeWriter.flush();
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

    public HashMap<Long, User> getUsers(){
        return this.users;
    }
    public HashMap<Long, Driver> getDrivers(){
        return this.drivers;
    }

    public void addUser(Interface gui, long phoneNum) {
        if(users.containsKey(phoneNum)){
            users.get(phoneNum).setGui(gui);
        }else{
            users.put(phoneNum, new User(gui,map,phoneNum));
        }
    }

    public void addDriver(Interface gui,long phoneNum, int capacity) {
        if(drivers.containsKey(phoneNum)){
            drivers.get(phoneNum).setGUI(gui);
        }else{
            drivers.put(phoneNum, new Driver(gui,map,phoneNum,capacity));
        }
    }

    public void update(String dataReceived){
        String type = dataReceived.split(":")[0];
        if(type.equals("Driver")){
            updateDriver(dataReceived.split(":")[1]);
        }else{
            updateUser(dataReceived.split(":")[1]);
        }
        saveDatabase();
    }

    public void updateUser(String userText) {
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
            user.setRideStatus(inRide);
        } else {
            users.put(phoneNum, new User(gui,map,phoneNum, startLocation, endLocation, isAlone, inRide));
        }
    }

    public void updateDriver(String driverText){
        String [] driverLine = driverText.split("_");
        String driverDetails = driverLine[0];
        String[] driverDetail = driverDetails.split(",");
        long phoneNum = Long.parseLong(driverDetail[PHONE]);
        Location currLocat = map.getLocation(driverDetail[CURR_LOCAT]);
        int capacity = Integer.parseInt(driverDetail[CAPCITY]);
        boolean isDrive = ("true").equals(driverDetail[IS_DRIVE]);
        if (drivers.containsKey(phoneNum)) {
            Driver driver = drivers.get(phoneNum);
            driver.setCurrentLocation(currLocat);
            driver.setDrive(isDrive);
        } else {
            drivers.put(phoneNum, new Driver(gui,map, phoneNum, currLocat, capacity, isDrive));
        }
        if(driverLine.length > 1){
            Driver driver = drivers.get(phoneNum);
            String [] ryders = driverLine[1].split(",");
            for(String ryder : ryders){
                long ryderNum = Long.parseLong(ryder);
                users.get(ryderNum).setDriver(driver);
            }

        }
        
    }

    public void removeUser(long phoneNum) {
        users.remove(phoneNum);
    }

    public void removeDriver(long phoneNum) {
        drivers.remove(phoneNum);
    }
}
