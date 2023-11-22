import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankSystem {
    // https://www.1point3acres.com/bbs/thread-1018421-1-1.html
    // https://www.1point3acres.com/bbs/thread-1017385-1-1.html

    // 这个不是，但是很类似
    // https://www.1point3acres.com/bbs/thread-1014558-1-1.html

    class User{
        String account;
        long balance;
        List<Integer> paymentTime;
        // time, cashback
        Deque<long[]> notChashedBack;
        long getBalance(int timeStamp){
            while(!notChashedBack.isEmpty() && (long)timeStamp >= notChashedBack.peekFirst()[0]){
                long[] info = notChashedBack.pollFirst();
                balance += 0.2 * info[1];
            }
            return balance;
        }


    }
    Map<String, User> users = new HashMap<>();
    public String[] solution(String[][] args){
        String[] res = new String[args.length];
        int index = 0;
        for(String[] arg: args){
            String cmd = arg[0];
        }
        return res;
    }
}
class SolutionBank{
    public static void main(String[] agrs){
        BankSystem bank = new  BankSystem();
        String[] res = bank.solution(new String[][]{
                {"SET", "1", "A", "B", "C"},
                {"SET_WITH_TTL", "1", "A", "B", "C", "10"},
                {"SET", "4", "A", "D", "E"},
                {"BACKUP", "3"},
                {"SET", "5", "A", "D", "G"},
                {"RESTORE", "6", "4"},
                {"GET", "6", "A", "D"}
        });
        for(String r : res){
            System.out.println(r);
        }
        System.out.println("END");
    }
}