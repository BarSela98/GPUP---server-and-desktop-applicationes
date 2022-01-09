package gpup.servlets;

import ODT.Mission;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MissionManger {

    private  Map<String,Mission> missionList = new HashMap<>();

    public synchronized void addMission(Mission newMission){missionList.put(newMission.getNameOfMission(),newMission);}

    public synchronized Set<Mission> getMissionList() {
        return (Set<Mission>) missionList.values();
    }
    public synchronized Mission getMissionByName(String name)throws Exception{
        if(!missionList.containsKey(name))
            throw new Exception("graph name not exsit");
        return missionList.get(name);
    }
}
