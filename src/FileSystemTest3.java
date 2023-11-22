import java.util.*;

public class FileSystemTest3 {
    // https://www.1point3acres.com/bbs/thread-933116-1-1.html

    class File{
        String name;
        List<String> size;
        File(String name, String size){
            this.size = new LinkedList<>(Arrays.asList(size));
            this.name = name;
        }

        public String lastVersion(){
            return this.size.get(this.size.size() - 1);
        }

        public String getVersion(int index){
            return this.size.get(index - 1);
        }

        public void deleteVersion(int index){
            this.size.remove(index - 1);
        }
        public void addVersion(String version){
            this.size.add(version);
        }
    }

    static class Trash{
        static Map<String, File> fileName = new HashMap<>();
        public static boolean contains(String name){
            return fileName.containsKey(name);
        }
        public static void remove(String name){
            fileName.remove(name);
        }
        public static void add(File file){
            fileName.put(file.name, file);
        }
    }
    private static Map<String, File> fileByName = new HashMap<>();
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
            else if(cmd.equals("GET_VERSION")){
                res[index ++] = getVersion(query);
            }
            else if(cmd.equals("DELETE_VERSION")){
                res[index ++] = deleteVersion(query);
            }
            else if(cmd.equals("DELETE_FILES")){
                res[index ++] = deleteFiles(query);
            }
            else if(cmd.equals("RESTORE_FILES")){
                res[index ++] = restoreFiles(query);
            }
        }
        return res;
    }

    private String addFile(String[] query){
        String fileName = query[1];
        String size = query[2];
        if(fileByName.containsKey(fileName)){
            fileByName.get(fileName).addVersion(size);
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
            return fileByName.get(fileName).lastVersion();
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
        PriorityQueue<File> heap = new PriorityQueue<>((o1, o2) -> (Integer.parseInt(o1.lastVersion()) == Integer.parseInt(o2.lastVersion())? o1.name.compareTo(o2.name) : Integer.parseInt(o2.lastVersion()) - Integer.parseInt(o1.lastVersion())));
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

    private String getVersion(String[] query){
        int version = Integer.parseInt(query[2]);
        String fileName = query[1];
        if(fileByName.containsKey(fileName) && fileByName.get(fileName).size.size() >= version){
            return fileByName.get(fileName).getVersion(version);
        }
        else {
            return "";
        }
    }

    private String deleteVersion(String[] query){
        int version = Integer.parseInt(query[2]);
        String fileName = query[1];
        if(fileByName.containsKey(fileName) && fileByName.get(fileName).size.size() >= version){
            fileByName.get(fileName).deleteVersion(version);
            return "true";
        }
        else {
            return "false";
        }
    }

    private String deleteFiles(String[] query){
        String prefix = query[1];
        int count = 0;
        Set<String> removed = new HashSet<>();
        for(String name : fileByName.keySet()){
            if(name.startsWith(prefix)){
                count ++;
               if(Trash.contains(name)){
                   Trash.remove(name);
               }
               Trash.add(fileByName.get(name));
               removed.add(name);
            }
        }
        for(String trashName : removed){
            fileByName.remove(trashName);
        }
        return "" + count;
    }

    private String restoreFiles(String[] query){
        String prefix = query[1];
        int count = 0;
        Set<String> restored = new HashSet<>();
        for(String name : Trash.fileName.keySet()){
            if(name.startsWith(prefix)){
                count ++;
                File file = Trash.fileName.get(name);
                restored.add(name);
                if(fileByName.containsKey(name)){
                    fileByName.remove(name);
                }
                fileByName.put(name, file);
            }
        }
        for(String restoreName : restored){
            Trash.remove(restoreName);
        }
        return "" + count;
    }
}

class SolutionTest3{
    public static void main(String[] args){
        FileSystemTest3 fs = new FileSystemTest3();
        String[] res = fs.solution(new String[][]{
                {"ADD_FILE", "/dir/file1a", "3"},
                {"ADD_FILE", "/dir/fileb", "1"},
                {"DELETE_FILES", "/dir"},
                {"GET_FILE_SIZE", "/dir/file1a"},
                {"RESTORE_FILES", "/dir/file1"},
                {"GET_FILE_SIZE", "/dir/file1a"}
        });

        for(String r : res){
            System.out.println(r);
        }
    }
}

