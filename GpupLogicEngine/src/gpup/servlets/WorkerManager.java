package gpup.servlets;

import object.WorkerObject;

import java.util.HashMap;
import java.util.Map;


public class WorkerManager {

    private Map<String, WorkerObject> workerMap;
    
    public WorkerManager() {
       workerMap = new HashMap<>();
    }

    public synchronized void addWorker(String workerName, int Thread) {
       workerMap.put(workerName,new WorkerObject(workerName,Thread));
    }

    public synchronized void removeWorker(String workerName) {
       workerMap.remove(workerName);
    }

    public boolean isWorkerObjectExists(String workerName) {
        return workerMap.containsKey(workerName);
    }

    public WorkerObject getWorkerByName(String workerName) {
        return workerMap.get(workerName);
    }
}
