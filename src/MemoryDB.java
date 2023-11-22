import java.util.*;

public class MemoryDB {
    class Record{
        Map<String, String> fields;
        // 第三部才有
        Map<String, Integer> TTL;
        Record(String field, String value){
            fields = new HashMap<>();
            fields.put(field, value);
            // Only at 3
            TTL = new HashMap<>();
        }
    }
    static Map<String, Record> records = new HashMap<>();
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
}

class SolutionDB{
    public static void main(String[] agrs){
        MemoryDB db = new MemoryDB();
        String[] res = db.solution(new String[][]{
                {"SET", "1", "food", "fruit", "apple"},
                {"COMPARE_AND_SET", "2", "food", "fruit", "apple", "peach"},
                {"COMPARE_AND_SET", "3", "food", "fruit", "apple", "pear"},
                {"SET", "4", "food", "fruit", "orange"},
                {"SET", "5", "food", "vege", "sprout"},
                {"GET", "6", "food", "fruit"},
                {"GET", "7", "food", "vege"},
                {"COMPARE_AND_DELETE", "8", "food", "vege", "sprout"},
                {"GET", "9", "food", "vege"}
        });
        for(String r : res){
            System.out.println(r);
        }
        System.out.println("END");
    }
}
