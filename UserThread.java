import java.util.ArrayList;

public class UserThread implements Runnable{

        ArrayList<User> users;
        User user;
        long threadID; // use the user's phone number as the thread id

        public UserThread(ArrayList<User> users, long phoneNum){
            this.users = users;
            this.threadID = phoneNum;
            this.user = new User(phoneNum);
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
        
}
