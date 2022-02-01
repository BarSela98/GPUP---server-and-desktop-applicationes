package gpup.servlets;

import engine.Mission;
import engine.Target;

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
            throw new Exception("graph name not exist");
        return missionList.get(name);
    }
    public synchronized void setStatusOfMissionByName(String name, String status)throws Exception{
        if(!missionList.containsKey(name))
            throw new Exception("graph name not exist");
        missionList.get(name).setStatus(status);
    }

    public synchronized void signForMissionByName(String worker, String missionNameFromParameter) {
        missionList.get(missionNameFromParameter).workerSign(worker);
    }

    public synchronized void removeWorkerForMissionByName(String workerName, String missionNameFromParameter) {
        missionList.get(missionNameFromParameter).removeWorkerFromMission(workerName);
    }

    public synchronized void updateTarget(Target tar){
        Mission m = missionList.get(tar.getMission());
        m.updateTarget(tar);
    }

    public synchronized String incrementalSize(String name){
        int s =0 ;
        if(!missionList.containsKey(name))
            return "0";

        for (String na : missionList.keySet()){
            if (na.equals(name) && s == 0 )
                    return "1";
            else if (na.length()-1 == name.length() ){
                if (na.startsWith(name) && Character.isDigit(na.charAt(na.length()-1)))
                    if (s <  Integer.parseInt(na.substring(na.length()-1)))
                        s =  Integer.parseInt(na.substring(na.length()-1));
            }
        }
        return String.valueOf(s+1);
    }

    public boolean isMissionNameExists(String name) {
        if(missionList.containsKey(name))
            return true;
        else return false;
    }
}
