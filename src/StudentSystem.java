import java.lang.annotation.Inherited;
import java.util.*;

public class StudentSystem {
    // https://www.1point3acres.com/bbs/thread-1014351-1-1.html
    // https://www.1point3acres.com/bbs/thread-1014482-1-1.html
    class Course{
        int courseid;
        String coursename;
        int credits;
        List<Student> studentList;
        // level 3
        boolean PFgraded;
    }

    class Student{
        Set<Course> courses = new HashSet<>();
        String stdId;
        int creditLeft = 24;
        // <CourseId, <Type, Score>>
        Map<String, Map<String, Integer>> score;
    }
    // <Name, courese>
    Map<String, Course> courseById = new HashMap<>();
    Set<String> courseName = new HashSet<>();

    Map<String, Student> students = new HashMap<>();
    String private creatClass(){
        String name = "name";
        String id = "id";
        ...
    }

    String sameCourse(){
        Set<String> pairs = new HashSet<>();
        for(String courseId : courseById.keySet()){
            Course c = courseById.get(courseId);
            // Think about this!!!!!!!!!!!!!!
            if(c.PFgraded) continue;
            List<Student> stdList = c.studentList;
            Collections.sort(stdList, (o1, o2) -> (o1.stdId.compareTo(o2.stdId)));
            for(int i = 0; i < stdList.size(); i++){
                for(int j = i + 1; j < stdList.size(); j++){
                    pairs.add("[" + stdList.get(i) + ", " + stdList.get(j) + "]");
                }
            }
        }
        List<String> pairList = new ArrayList<>(pairs);
        Collections.sort(pairList);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(String str : pairList){
            if(sb.length() != 1) sb.append(", ");
            sb.append(str);
        }
        sb.append("]");
        return sb.toString();
    }

    String aveGPA(){
        String std = "id";
        Student student = students.get(std);
        int failCount = 0;
        int passCount = 0;
        int stdCredit = 0;
        int totalScore = 0;
        for(Course c : student.courses){
            if(!student.score.containsKey(c.coursename) || student.score.get(c.coursename).size() != 3) return "";
            Map<String, Integer> scores = student.score.get(c.coursename);
            int currScore = 0;
            for(String i : scores.keySet()){
                currScore += scores.get(i);
            }
            if(!c.PFgraded){
                totalScore += currScore * c.credits;
                stdCredit += c.credits;
            }
            else if(currScore < 66){
                failCount += 1;
            }
            else{
                passCount += 1;
            }
        }
        int stdAvg = totalScore / stdCredit;
    }

}
