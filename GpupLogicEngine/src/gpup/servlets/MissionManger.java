package gpup.servlets;

import ODT.Mission;

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
}
