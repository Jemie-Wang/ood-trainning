import java.util.*;

public class WorkSystem {
    // https://www.1point3acres.com/bbs/thread-1016265-1-1.html
    class Worker{
        String wordkerId;
        Position currPos;
        Position nextPos;
        long timeInOffice = 0;

        // compensation, time slot
        Map<Long, List<long[]>> slotsInOffice = new HashMap<>();

    }
    class Position{
        long activeTime;
        long componsentation;
        String positionName;
    }

    Map<String, Worker> workersById = new HashMap<>();
    Map<String, List<Worker>> workerByPos = new HashMap<>();
    // wordId, startTime
    Map<String, Long> inOffice = new HashMap<>();

    private String register(String[] arg){
        long currTime = Long.parseLong(arg[0]);
        String workerId = arg[1];
        if(workersById.containsKey(workerId)) return "invalid_request";
        Worker w = workersById.get(workerId);
        if(!inOffice.containsKey(workerId)){
            // Register
            if(w.nextPos != null && w.nextPos.activeTime <= currTime){
                workerByPos.get(w.currPos.positionName).remove(w);
                w.currPos = w.nextPos;
                w.nextPos = null;
                workerByPos.computeIfAbsent(w.currPos.positionName, key-> new LinkedList<>()).add(w);
            }
            inOffice.put(workerId, currTime);
            return "register";
        }
        else {
            Long startTime = inOffice.get(workerId);
            inOffice.remove(workerId);
            w.timeInOffice += (currTime - startTime);
            w.slotsInOffice.computeIfAbsent(w.currPos.componsentation, key -> new ArrayList<>()).add(new long[]{startTime, currTime});
            return "register";
        }

    }
    private String add(String[] arg){
        String workerId = arg[1];
        String posName = arg[2];
        int com = Integer.parseInt(arg[3]);
        if(workersById.containsKey(workerId)) return "false";
        Position p = new Position();
        p.activeTime = 0;
        p.positionName = posName;
        p.componsentation =com;
        Worker w = new Worker();
        w.wordkerId = workerId;
        w.currPos = p;
        return "true";
    }

    private String topN(String[] arg){
        int n = Integer.parseInt(arg[1]);
        String pos = arg[2];
        List<Worker> worker = workerByPos.get(pos);
        Collections.sort(worker, (o1, o2) -> (o1.timeInOffice == o2.timeInOffice ? o1.wordkerId.compareTo(o2.wordkerId) : (int)(o1.timeInOffice - o2.timeInOffice)));
        return "";
    }
}
