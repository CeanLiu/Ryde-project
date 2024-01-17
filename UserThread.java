import java.util.ArrayList;

public class UserThread implements Runnable{

        ArrayList<User> users;
        User user;
        int threadID; // use the user's phone number as the thread id

        public UserThread(ArrayList<User> users, String start, String destination, int phoneNum){
            this.users = users;
            this.threadID = phoneNum;
            this.user = new User(phoneNum,start,destination);
            users.add(user);
        }

        @Override
        public void run() {
            // move the user along the roads
            try {
                user.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //pause thread execution for the duration of one video frame
            try{Thread.sleep(15);} catch (Exception e){e.printStackTrace();}
        }

        public static void main (String[] args) throws Exception{ 
            ArrayList<User> users = new ArrayList<>();
            String start = "location 1";
            String end = "location 2";
            int number = 1234567890;

            Thread userThread = new Thread(new UserThread(users, start, end, number));
            userThread.start();                                       //after completing the communication close the streams and the sockets
        }
        
}
