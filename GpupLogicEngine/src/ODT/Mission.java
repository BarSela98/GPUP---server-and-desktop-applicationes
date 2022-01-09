package ODT;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    public enum StatusOfMission {Done,Waiting,inProgress}

    private String nameOfMission;
    private String nameOfCreator;
    private int amountOfTarget;
    private int amountOfRoot;
    private int amountOfMiddle;
    private int amountOfIndependents;
    private int priceOfMission;
    private int workers;
    private StatusOfMission statusOfMission;
    private List<Target> targetToExecute = new ArrayList<>();

    public String getNameOfMission() {
        return nameOfMission;
    }
    public void setNameOfMission(String nameOfMission) {
        this.nameOfMission = nameOfMission;
    }

    public String getNameOfCreator() {
        return nameOfCreator;
    }
    public void setNameOfCreator(String nameOfCreator) {
        this.nameOfCreator = nameOfCreator;
    }

    public int getAmountOfTarget() {
        return amountOfTarget;
    }
    public void setAmountOfTarget(int amountOfTarget) {
        this.amountOfTarget = amountOfTarget;
    }

    public int getAmountOfRoot() {
        return amountOfRoot;
    }
    public void setAmountOfRoot(int amountOfRoot) {
        this.amountOfRoot = amountOfRoot;
    }

    public int getAmountOfMiddle() {
        return amountOfMiddle;
    }
    public void setAmountOfMiddle(int amountOfMiddle) {
        this.amountOfMiddle = amountOfMiddle;
    }

    public int getAmountOfIndependents() {
        return amountOfIndependents;
    }
    public void setAmountOfIndependents(int amountOfIndependents) {
        this.amountOfIndependents = amountOfIndependents;
    }

    public int getPriceOfMission() {
        return priceOfMission;
    }
    public void setPriceOfMission(int priceOfMission) {
        this.priceOfMission = priceOfMission;
    }

    public int getWorkers() {
        return workers;
    }
    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public StatusOfMission getStatusOfMission() {
        return statusOfMission;
    }
    public void setStatusOfMission(StatusOfMission statusOfMission) {
        this.statusOfMission = statusOfMission;
    }

    private void fixTargetsStatues(){
        boolean done;
        do {
           done=true;
           for (Target t : targetToExecute) {
               if (t.getStatus() == Target.Status.Frozen) {
                   for (String s : t.getSetDependsOn()) {
                       for (int i = 0; i < targetToExecute.size(); i++) {
                           if (targetToExecute.get(i).getName().equals(s) && (targetToExecute.get(i).getStatus() == Target.Status.Success || targetToExecute.get(i).getStatus() == Target.Status.Warning)) {
                               t.setStatus(Target.Status.Waiting);
                               done = false;
                           } else if (targetToExecute.get(i).getName().equals(s) && (targetToExecute.get(i).getStatus() == Target.Status.Failure || targetToExecute.get(i).getStatus() == Target.Status.Skipped)) {
                               t.setStatus(Target.Status.Skipped);
                               done = false;
                           }
                       }
                   }
               }
           }
        }while (!done);
    }
    public boolean checkIfMissionDone(){
        for(Target t : targetToExecute){
            if(t.getStatus()==Target.Status.Waiting||t.getStatus()==Target.Status.Frozen){
                return false;
            }
        }
        return true;
    }
}
