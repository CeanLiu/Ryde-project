import java.util.ArrayList;

public class UserThread implements Runnable{
        Database db;
        User user;
        long threadID; // use the user's phone number as the thread id

        public UserThread(Database db, long phoneNum){
            this.db = db;
            this.threadID = phoneNum;
            this.user = db.getUser(phoneNum);
        }

        @Override
        public void run() {
            // move the user along the roads
            try {
                user.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(true){
                String msg = user.receive();
                if(msg != null){
                    db.update(msg);
                }
                user.displayInfoGUI();
            //pause thread execution for the duration of one video frame
            try{Thread.sleep(15);} catch (Exception e){e.printStackTrace();}
            }
        }
        
}
