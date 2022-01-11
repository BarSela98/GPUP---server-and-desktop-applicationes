package gpup.servlets;

import ODT.Mission;
import object.WorkerObject;

import java.util.HashMap;
import java.util.Map;

public class MissionManger {

    private  Map<String,Mission> missionList;

    public MissionManger() {
        missionList = new HashMap<>();
    }

    public synchronized void addMission(Mission newMission){
        missionList.put(newMission.getNameOfMission(),newMission);
    }

    public synchronized Map<String,Mission> getMissionList() {
        return missionList;
    }
    public synchronized Mission getMissionByName(String name)throws Exception{
        if(!missionList.containsKey(name))
            throw new Exception("graph name not exsit");
        return missionList.get(name);
    }
    public synchronized void setStatusOfMissionByName(String name, String status)throws Exception{
        if(!missionList.containsKey(name))
            throw new Exception("graph name not exsit");
        missionList.get(name).setStatus(status);
    }

    public synchronized void signForMissionByName(WorkerObject worker, String missionNameFromParameter) {
        missionList.get(missionNameFromParameter).workerSign(worker);
    }

    public void removeWorkerForMissionByName(String workerName, String missionNameFromParameter) {
        missionList.get(missionNameFromParameter).removeWorkerFromMission(workerName);
    }
}
