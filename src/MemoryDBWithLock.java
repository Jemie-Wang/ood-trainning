import java.util.*;

public class MemoryDBWithLock {
    class Record{
        Map<String, Integer> fields;
        int operation;
        Record(){
            fields = new HashMap<>();
            operation = 0;
        }
    }
    // 不能放在record里因为可能record删掉了但是lock还在
    Map<String, Deque<String>> locks = new HashMap<>();
    Map<String, Record> records = new HashMap<>();
    String[] solution(String[][] args){
        String[] res = new String[args.length];
        int index = 0;
        for(String[] arg: args){
            String cmd = arg[0];
            if(cmd.equals("SET_OR_INC")){
                res[index ++] = setOrInc(arg);
            }
            else if(cmd.equals("GET")){
                res[index ++] = get(arg);
            }
            else if(cmd.equals("DELETE")){
                res[index ++] = delete(arg);
            }
            else if(cmd.equals("TOP_N_KEY")){
                res[index ++] = topNKey(arg);
            }
            if(cmd.equals("SET_OR_INC_BY_CALLER")){
                res[index ++] = setOrIncByCaller(arg);
            }
            else if(cmd.equals("DELETE_BY_CALLER")){
                res[index ++] = deleteByCaller(arg);
            }
            else if(cmd.equals("LOCK")){
                res[index ++] = lock(arg);
            }
            else if(cmd.equals("UNLOCK")){
                res[index ++] = unlock(arg);
            }

        }
        return res;
    }

    private String setOrInc(String[] arg){
        String key = arg[1];
        String field = arg[2];
        int value = Integer.parseInt(arg[3]);

        Record record = records.computeIfAbsent(key, k -> new Record());
        record.operation += 1;
        // FOR TEST 3
        if(locks.computeIfAbsent(key, k -> new LinkedList<String>()).peekFirst() != null) return "";
        record.fields.put(field, record.fields.getOrDefault(field, 0) + value);
        return "" + record.fields.get(field);
    }

    private String get(String[] arg){
        String key = arg[1];
        String field = arg[2];
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)
        ){
            return "";
        }
        else{
            return "" + records.get(key).fields.get(field);
        }
    }

    private String delete(String[] arg){
        String key = arg[1];
        String field = arg[2];
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)){
            return "false";
        }
        // FOR TEST 3
        if(locks.computeIfAbsent(key, k -> new LinkedList<String>()).peekFirst() != null) return "false";
        else{
            Record record = records.get(key);
            record.fields.remove(field);
            record.operation += 1;
            if(record.fields.size() == 0) records.remove(key);
            return "true";
        }
    }
    private String topNKey(String[] arg){
        int n = Integer.parseInt(arg[1]);
        List<String> list = new ArrayList<>(records.keySet());
        Collections.sort(list, (o1, o2) -> (records.get(o1).operation == records.get(o2).operation
                                                ? o1.compareTo(o2)
                                                : records.get(o2).operation - records.get(o1).operation));
        StringBuilder sb = new StringBuilder();
        int index = 0;
        while(index ++ < n){
            if(sb.length() != 0) sb.append(", ");
            sb.append(list.get(index) + "(" + records.get(index).operation + ")");
        }
        return sb.toString();
    }
    private String setOrIncByCaller(String[] arg){
        String key = arg[1];
        String field = arg[2];
        String caller = arg[4];
        if(records.containsKey(key) && !caller.equals(locks.get(key).peekFirst())){
            return records.get(key).fields.containsKey(field)?  "" + records.get(key).fields.get(field) : "";
        }
        else{
            int value = Integer.parseInt(arg[3]);
            Record record = records.computeIfAbsent(key, k -> new Record());
            record.operation += 1;
            record.fields.put(field, record.fields.getOrDefault(field, 0) + value);
            return "" + record.fields.get(field);
        }
    }

    private String deleteByCaller(String[] arg){
        String key = arg[1];
        String field = arg[2];
        String caller = arg[3];
        if(records.containsKey(key)
                && (locks.get(key).peekFirst() == null
                    || locks.get(key).peekFirst().equals(caller))
        ){
            Record record = records.get(key);
            record.operation += 1;
            record.fields.remove(field);
            record.operation += 1;
            if(record.fields.size() == 0) records.remove(key);
            return "true";
        }
        else{
            return "false";
        }
    }
    private String lock(String[] arg){
        //  初始化record的时候也需要在lock里添加对应deque linkedinlist！！！！！！！！
        String key = arg[2];
        String caller = arg[1];
        if(!records.containsKey(key)
        ){
            return "invalid_request";
        }
        Record record = records.get(key);
        if(locks.get(key).contains(caller)){
            return "";
        }
        else if(locks.get(key).isEmpty()){
            locks.get(key).addLast(caller);
            return "accquired";
        }
        else{
            locks.get(key).addLast(caller);
            return "wait";
        }
    }
    private String unlock(String[] arg){
        String key = arg[1];
        if(!locks.containsKey(key)){
            return "invalid_request";
        }
        Deque<String> lock = locks.get(key);
        if(lock.isEmpty()) return "";
        if(!records.containsKey(key)){
            locks.remove(key);
            return "released";

        }
        else{
            lock.pollFirst();
            return "released";
        }
    }

}

class SolutionWithLock{
    public static void main(String[] args){
        MemoryDBWithLock db = new MemoryDBWithLock();
        String[] res = db.solution(new String[][]{
                {"SET_OR_INC", "A", "B", "4"},
                {"UNLOCK", "A"},
                {"LOCK", "user1", "A"},
                {"LOCK", "user2", "A"},
                {"LOCK", "user3", "B"},
                {"UNLOCK", "B"},
                {"SET_OR_INC", "A", "C", "5"},
                {"DELETE", "A", "B"},
                {"SET_OR_INC_BY_CALLER", "A", "B", "3", "user2"},
                {"GET", "A", "B"},
                {"DELETE_BY_CALLER", "A", "B", "user3"},
                {"SET_OR_INC_BY_CALLER", "A", "B", "5", "user1"},
                {"UNLOCK", "A"},
                {"SET_OR_INC_BY_CALLER", "A", "B", "2", "user1"},
                {"SET_OR_INC_BY_CALLER", "A", "B", "1", "user2"},
                {"LOCK", "user3", "A"},
                {"DELETE_BY_CALLER", "A", "B", "user2"},
                {"UNLOCK", "A"}
        });
        for(String r : res){
            System.out.println(r);
        }
        System.out.println("END");
    }
}
