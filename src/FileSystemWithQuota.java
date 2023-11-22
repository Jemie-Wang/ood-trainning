import java.util.*;

public class FileSystemWithQuota {

    FileSystemWithQuota(){
        // 定义一个foot user！！！！！！！
        User root = new User("/", Integer.MAX_VALUE);
        users.put("/", root);
    }

    class File{
        String name;
        String user;
        List<Integer> size;
        File(String name, int size, String user){
            this.size = new LinkedList<>(Arrays.asList(size));
            this.user = user;
            this.name = name;
        }

        public int lastVersion(){
            return this.size.get(this.size.size() - 1);
        }

        public int getVersion(int index){
            return this.size.get(index - 1);
        }

        public void deleteVersion(int index){
            this.size.remove(index - 1);
        }
        public void addVersion(int version){
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

    class User{
        String name;
        int capacity;
        List<File> files;
        User(String name, int capacity){
            this.name = name;
            this.capacity = capacity;
            files = new LinkedList<>();
        }

        public void removeFile(File file){
            this.files.remove(file);
        }
    }

    private static Map<String, User> users = new HashMap<>();
    private static Map<String, File> fileByName = new HashMap<>();


    public String[] solution(String[][] queries){
        String[] res = new String[queries.length];
        int index = 0;
        for(String[] query : queries){
            String cmd = query[0];
            // find which function to call
            if(cmd.equals("ADD_FILE")){
                res[index ++] = addFile(query);
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
                res[index ++] = deleteFiles(query[1]);
            }
            else if(cmd.equals("RESTORE_FILES")){
                res[index ++] = restoreFiles(query);
            }
            else if(cmd.equals("ADD_USER")){
                res[index ++] = addUser(query);
            }
            else if(cmd.equals("CHANGE_QUOTA")){
                res[index ++] = changeQuota(query);
            }
            else if(cmd.equals("MERGE_FILE")){
                res[index ++] = mergeFile(query);
            }
            else if(cmd.equals("UNMERGE_FILE")){
                res[index ++] = unmergeFile(query);
            }
        }
        return res;
    }

    private String addFile(String[] query){
        String fileName = query[1];
        int size = Integer.parseInt(query[2]);
        String userName = query.length == 3? "/" : query[3];
        if(!userName.equals("/") && !(users.containsKey(userName))){
            return "";
        }
        User user = users.get(userName);
        if(user.capacity < size) return "";
        user.capacity -= size;
        if(fileByName.containsKey(fileName)){
            fileByName.get(fileName).addVersion(size);
            return "Overwrite";
        }
        else {
            File file = new File(fileName, size, userName);
            fileByName.put(fileName, file);
            user.files.add(file);
            return "Create";

        }
    }

    private String getFileSize(String[] query){
        String fileName = query[1];
        if(fileByName.containsKey(fileName)){
            return "" +  fileByName.get(fileName).lastVersion();
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
        PriorityQueue<File> heap = new PriorityQueue<>((o1, o2) -> (o1.lastVersion() == o2.lastVersion()? o1.name.compareTo(o2.name) : o2.lastVersion() - o1.lastVersion()));
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
            return "" + fileByName.get(fileName).getVersion(version);
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

    private String deleteFiles(String prefix){
        int count = 0;
        Set<String> removed = new HashSet<>();
        for(String name : fileByName.keySet()){
            if(name.startsWith(prefix)){
                count ++;
                File file = fileByName.get(name);
                User user = users.get(file.user);
                user.removeFile(file);
                user.capacity += file.lastVersion();
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

    private String addUser(String[] query){
        String name = query[1];
        int capacity = Integer.parseInt(query[2]);
        if(users.containsKey(name)){
            return "false";
        }
        else{
            User user = new User(name, capacity);
            users.put(name, user);
            return "true";
        }
    }

    private String changeQuota(String[] query){
        String name = query[1];
        int capacity = Integer.parseInt(query[2]);
        if(!users.containsKey(name)){
            return "false";
        }
        else{
            User user = users.get(name);
            if(user.capacity > capacity){
                List<File> list = user.files;
                Collections.sort(list, (o1,o2) -> o1.lastVersion() == o2.lastVersion()? o2.name.compareTo(o1.name) : o2.lastVersion() - o1.lastVersion());
                while(list.size() > capacity){
                    this.deleteFiles(list.get(0).name);
                    list.remove(0);
                }
            }
            user.capacity = capacity;
            return "true";
        }


    }

    private String mergeFile(String[] query){
        String userId = query[2];
        String fileName = query[1];
        if(!fileByName.containsKey(fileName) || !fileByName.get(fileName).user.equals(userId)){
            return "";
        }
        else{
            File file = fileByName.get(fileName);
            fileByName.remove(fileName);
            fileByName.put(fileName + ".COMPOSED", file);
            file.name = fileName + ".COMPOSED";
            users.get(userId).capacity += file.lastVersion() / 2;
            file.addVersion(file.lastVersion() / 2);
            return "Merged";
        }
    }
    private String unmergeFile(String[] query){
        String userId = query[2];
        String fileName = query[1];
        String newFileName = fileName.split("\\.")[0];
        if(!fileByName.containsKey(fileName) || !fileByName.get(fileName).user.equals(userId) || fileByName.containsKey(newFileName)){
            return "";
        }
        else{
            File file = fileByName.get(fileName);
            fileByName.remove(fileName);
            fileByName.put(newFileName, file);
            file.name = newFileName;
            if(users.get(userId).capacity < file.lastVersion()) return "";
            users.get(userId).capacity -= file.lastVersion();
            file.addVersion(file.lastVersion() * 2);
            return "Unerged";
        }
    }
}

class SolutionQuota{
    public static void main(String[] args){
        FileSystemWithQuota fs = new  FileSystemWithQuota();
        String[] res = fs.solution(new String[][]{
                {"ADD_USER", "UserA", "4"},
                {"ADD_USER", "UserB", "1"},
                {"ADD_FILE", "/dir/file1a", "2", "UserA"},
                {"ADD_FILE", "/dir/fileb", "1", "UserA"},
                {"MERGE_FILE", "/dir/file1a", "UserA"},
                {"ADD_FILE", "/dir/filec", "2", "UserA"},
                {"UNMERGE_FILE", "/dir/file1a.COMPOSED", "UserA"},

        });

        for(String r : res){
            System.out.println(r);
        }
    }
}

