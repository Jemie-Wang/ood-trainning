import java.util.*;

public class MemoryWithBackUp {
    class Record{
        Map<String, String> fields;
        // Only at 3
        Map<String, Integer> TTL;
        Record(){
            fields = new HashMap<>();
            // Only at 3
            TTL = new HashMap<>();
        }
        Record(String field, String value){
            fields = new HashMap<>();
            fields.put(field, value);
            // Only at 3
            TTL = new HashMap<>();
        }
    }
    Map<String, Record> records = new HashMap<>();

    class BackUp{
        int timeStamp;
        Map<String, Record> records = new HashMap<>();
    }
    List<BackUp> backUps = new ArrayList<>();
    String[] solution(String[][] args){
        String[] res = new String[args.length];
        int index = 0;
        for(String[] arg: args){
            String cmd = arg[0];
            if(cmd.equals("SET")){
                res[index ++] = set(arg);
            }
            else if(cmd.equals("COMPARE_AND_SET")){
                res[index ++] = compareAndSet(arg);
            }
            else if(cmd.equals("COMPARE_AND_DELETE")){
                res[index ++] = compareAndDelete(arg);
            }
            else if(cmd.equals("GET")){
                res[index ++] = get(arg);
            }
            else if(cmd.equals("SCAN")){
                res[index ++] = scan(arg);
            }
            else if(cmd.equals("SCAN_BY_PREFIX")){
                res[index ++] = scanByPrefix(arg);
            }
            else if(cmd.equals("SET_WITH_TTL")){
                res[index ++] = setWithTTL(arg);
            }
            else if(cmd.equals("COMPARE_AND_SET_WITH_TTL")){
                res[index ++] = compareAndSetWithTTL(arg);
            }
            else if(cmd.equals("GET_WHEN")){
                res[index ++] = getWhen(arg);
            }
            else if(cmd.equals("BACKUP")){
                res[index ++] = backUpData(arg);
            }
            else if(cmd.equals("RESTORE")){
                res[index ++] = restoreData(arg);
            }

        }
        return res;
    }

    private String set(String[] arg){
        String key = arg[2];
        String field = arg[3];
        String value = arg[4];
        Record record = records.computeIfAbsent(key, k -> new Record(field, value));
        record.fields.put(field, value);
        return "";
    }

    private String compareAndSet(String[] arg){
        String key = arg[2];
        String field = arg[3];
        String oldValue = arg[4];
        String newValue = arg[5];
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)
                || !records.get(key).fields.get(field).equals(oldValue)
                || oldValue.equals(newValue)){
            return "false";
        }
        else{
            records.get(key).fields.put(field, newValue);
            return "true";
        }
    }
    private String compareAndDelete(String[] arg){
        String key = arg[2];
        String field = arg[3];
        String oldValue = arg[4];
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)
                || !records.get(key).fields.get(field).equals(oldValue)){
            return "false";
        }
        else{
            records.get(key).fields.remove(field);
            return "true";
        }
    }

    private String get(String[] arg){
        String key = arg[2];
        String field = arg[3];
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)){
            return "";
        }
        else{
            return records.get(key).fields.get(field);
        }
    }

    private String scan(String[] arg){
        String key = arg[2];
        if(!records.containsKey(key)) return "";
        List<String> fieldList = new ArrayList<>(records.get(key).fields.keySet());
        Collections.sort(fieldList);
        Record record = records.get(key);
        StringBuilder sb = new StringBuilder();
        for(String field : fieldList){
            if(sb.length() != 0) sb.append(", ");
            sb.append(field + "(" + record.fields.get(field) + ")");
        }
        return sb.toString();
    }

    private String scanByPrefix(String[] arg){
        String key = arg[2];
        String prefix = arg[3];
        if(!records.containsKey(key)) return "";
        List<String> fieldList = new ArrayList<>(records.get(key).fields.keySet());
        Collections.sort(fieldList);
        Record record = records.get(key);
        StringBuilder sb = new StringBuilder();
        for(String field : fieldList){
            if(field.startsWith(prefix)){
                if(sb.length() != 0) sb.append(", ");
                sb.append(field + "(" + record.fields.get(field) + ")");
            }
        }
        return sb.toString();
    }
    private String setWithTTL(String[] arg){
        int timeStamp = Integer.parseInt(arg[1]);
        String key = arg[2];
        String field = arg[3];
        String value = arg[4];
        int TTL = Integer.parseInt(arg[5]);
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)
        ){
            return "";
        }
        else{
            Record record = records.get(key);
            record.fields.put(field, value);
            record.TTL.put(field, timeStamp + TTL);
        }
        return "";
    }
    private String compareAndSetWithTTL(String[] arg){
        int timeStamp = Integer.parseInt(arg[1]);
        String key = arg[2];
        String field = arg[3];
        String oldValue = arg[4];
        String newValue = arg[5];
        int TTL = Integer.parseInt(arg[6]);
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)
                || !records.get(key).fields.get(field).equals(oldValue)
                || oldValue.equals(newValue)
                || timeStamp + TTL == records.get(key).TTL.get(field)
        ){
            return "false";
        }
        else{
            Record record = records.get(key);
            record.fields.put(field, newValue);
            record.TTL.put(field, timeStamp + TTL);
            return "true";
        }
    }
    private String getWhen(String[] arg){
        String key = arg[2];
        String field = arg[3];
        int atTime = Integer.parseInt(arg[4]);
        if(atTime == 0) return get(arg);
        if(!records.containsKey(key)
                || !records.get(key).fields.containsKey(field)
                || records.get(key).TTL.get(field) < atTime
        ){
            return "";
        }
        else{
            return records.get(key).fields.get(field);
        }
    }
    private String backUpData(String[] arg){
        int time = Integer.parseInt(arg[1]);
        BackUp bu = new BackUp();
        bu.records = new HashMap<>();
        // NEED TO DO DEEP COPY!!!!!!!!!!!!!!!!
        for(String key : records.keySet()){
            Record r = records.get(key);
            Record newR = new Record();
            newR.TTL = new HashMap<>(r.TTL);
            newR.fields = new HashMap<>(r.fields);
            bu.records.put(key, newR);
        }
        bu.timeStamp = time;
        backUps.add(bu);
        return "";
    }
    private String restoreData(String[] arg){
        int time = Integer.parseInt(arg[1]);
        int backUptime = Integer.parseInt(arg[2]);
        BackUp bu = null;
        for(int i = backUps.size() - 1; i >= 0; i--){
            if(backUps.get(i).timeStamp <= backUptime){
                bu = backUps.get(i);
                break;
            }
        }
        int realBackUpTime = bu.timeStamp;
        // backup at backUptime, rest TTL == TTL - backUptime, new TTL = TTL - backUptime + time
        records = bu.records;
        for(String key : records.keySet()){
            Map<String, Integer> TTL = records.get(key).TTL;
            for(String field : TTL.keySet()){
                int prevTime = TTL.get(field);
                TTL.put(field, prevTime - realBackUpTime + time);
            }
        }
        return "";
    }
}

class SolutionDBBackUp{
    public static void main(String[] agrs){
        MemoryWithBackUp db = new  MemoryWithBackUp();
        String[] res = db.solution(new String[][]{
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
