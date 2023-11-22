import java.util.*;

public class Banking2 {
    // https://www.1point3acres.com/bbs/thread-1014912-1-1.html
    // https://www.1point3acres.com/bbs/thread-1015655-1-1.html
    long oneDay = (long)846000000;
    class User{
        String account;
        long balance;
        Deque<String> unfinishedTransfer = new LinkedList<>();
        long getBalance(int timeStamp){
            // find if there are expired transfer
            while(!unfinishedTransfer.isEmpty() && (long)timeStamp - oneDay>= transfers.get(unfinishedTransfer.peekFirst()).expire){
                // Remove expired
                String id = unfinishedTransfer.pollFirst();
                Trans t = transfers.get(id);
                // find if the transer not success
                if(!t.fulfill)
                    balance += t.amount;
            }
            return balance;
        }
    }
    class Trans{
        String from;
        String to;
        long expire;
        long amount;
        boolean fulfill;
    }
    Map<String, Trans> transfers = new HashMap<>();
    Map<String, User> users = new HashMap<>();
    String transfer(){
        Trans t = new Trans();
        t.from = "senderID";
        t.to = "receiverId";
        t.expire = (long)0;
        t.fulfill = false;
        t.amount = 1000;
        transfers.put("Transder" + (transfers.size() + 1), t);
        return "Transder" + transfers.size();
    }

    String acceptTransfer(){
        String key = "transID";
        User receiver = users.get("receiverId");
        Trans t = transfers.get(key);
        t.fulfill = true;
        receiver.balance += t.amount;
    }
}
