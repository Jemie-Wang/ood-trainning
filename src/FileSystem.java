import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class FileSystem {
    class File{
        String name;
        String size;
        File(String name, String size){
            this.name = name;
            this.size = size;
        }
    }

    static Map<String, File> fileByName = new HashMap<>();
    public String[] solution(String[][] queries){
        String[] res = new String[queries.length];
        int index = 0;
        for(String[] query : queries){
            String cmd = query[0];
            // find which function to call
            if(cmd.equals("ADD_FILE")){
                res[index ++] =  addFile(query);
            }
            else if(cmd.equals("GET_FILE_SIZE")){
                res[index ++] = getFileSize(query);
            }
            else if(cmd.equals("MOVE_FILE")){
                res[index ++] = moveFile(query);
            }
            else if(cmd.equals("GET_LARGEST_N")){
                res[index ++] = findLargestN(query);
            }
        }
        return res;
    }

    private String addFile(String[] query){
        String fileName = query[1];
        String size = query[2];
        if(fileByName.containsKey(fileName)){
            fileByName.get(fileName).size = size;
            return "Overwrite";
        }
        else {
            File file = new File(fileName, size);
            fileByName.put(fileName, file);
            return "Create";

        }
    }

    private String getFileSize(String[] query){
        String fileName = query[1];
        if(fileByName.containsKey(fileName)){
            return fileByName.get(fileName).size;
        }
        else {
            return "";
        }
    }

    private String moveFile(String[] query){
        String nameFrom = query[1];
        String nameTo = query[2];
        if(fileByName.containsKey(nameTo) || !fileByName.containsKey(nameFrom)) return "false";
        else {
           File file = fileByName.get(nameFrom);
           file.name = nameTo;
           fileByName.remove(nameFrom);
           fileByName.put(nameTo, file);
           return "true";
        }
    }

    private String findLargestN(String[] query){
        // 可能要讨论n是不是数字
        String prefix = query[1];
        PriorityQueue<File> heap = new PriorityQueue<>((o1, o2) -> (Integer.parseInt(o1.size) == Integer.parseInt(o2.size)? o1.name.compareTo(o2.name) : Integer.parseInt(o2.size) - Integer.parseInt(o1.size)));
        int n = Integer.parseInt(query[2]);
        for(String name : fileByName.keySet()){
            if(name.startsWith(prefix)){
                heap.add(fileByName.get(name));
            }
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        while(count < n && heap.size() != 0){
            if(sb.length() != 0) sb.append(", ");
            File f = heap.poll();
            sb.append(f.name + "(" + f.size + ")");
            count ++;
        }
        return sb.toString();
    }

}

class Solution{
    public static void main(String[] args){
        FileSystem fs = new FileSystem();
        String[] res = fs.solution(new String[][]{
                {"ADD_FILE", "/dir/filea", "5"},
                {"ADD_FILE", "/dir/fileb", "20"},
                {"ADD_FILE", "/dir/deeper/filec", "9"},
                {"GET_LARGEST_N", "/dir", "2"},
                {"GET_LARGEST_N", "/dir/file", "3"},
                {"GET_LARGEST_N", "/diff", "3"},
        });

        for(String r : res){
            System.out.println(r);
        }
    }
}

