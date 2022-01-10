package ODT;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Worker;
import utility.Utility;

import java.util.List;

public class Mission {
    public enum StatusOfMission {Done,Waiting,inProgress}

    private String nameOfMission;
    private String nameOfCreator;
    private int amountOfTarget;
    private int amountOfRoot;
    private int amountOfMiddle;
    private int amountOfIndependents;
    private int amountOfLeaf;
    private int priceOfMission;
    private int priceOfAllMission;
    private int workers;
    private Utility.WhichTask whichTask;
    private StatusOfMission statusOfMission;
    private List<Target> targets;
    private List<Target> waitingTargetToExecute;
    private Utility.TypeOfRunning typeOfRunning;
    private Simulation simulation;
    private Compilation compilation;
    private List<Worker> workerList;
    private int workerListSize;
    private boolean isRunning;






    public Mission(String nameOfMission, String nameOfCreator, List<Target> targets, Utility.WhichTask whichTask,Utility.TypeOfRunning typeOfRunning ,Compilation compilation) {
        this.compilation = compilation;
        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targets = targets;
        this.priceOfMission = compilation.getPriceOfCompilation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;
        missionBuilder();
    }
    public Mission(String nameOfMission, String nameOfCreator, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning , Simulation simulation) {
        this.simulation = simulation;

        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targets = targets;
        this.priceOfMission = simulation.getPriceOfSimulation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;
        missionBuilder();
    }
    public void missionBuilder(){
        workerListSize = 0;
        amountOfTarget = 0;
        amountOfRoot = 0;
        amountOfMiddle = 0;
        amountOfIndependents = 0;
        amountOfLeaf = 0;

        for (Target t: targets) {
            Target.Type type = t.getType();
            amountOfTarget++;
            if (type == Target.Type.ROOT)
                amountOfRoot++;
            else if (type == Target.Type.INDEPENDENTS){
                amountOfIndependents++;
                waitingTargetToExecute.add(t);
            }
            else if (type == Target.Type.MIDDLE)
                amountOfMiddle++;
            else if (type == Target.Type.LEAF){
                amountOfLeaf++;
                waitingTargetToExecute.add(t);
            }
        }
        priceOfAllMission = priceOfMission * amountOfTarget;
    }

    public void addWorkerToMission(Worker worker) {
        if(!workerList.contains(worker)) {
            workerList.add(worker);
            workerListSize++;
        }
    }
    public void removeWorkerFromMission(Worker worker) {
        if(workerList.contains(worker)){
            workerList.remove(worker);
            workerListSize--;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getWorkerListSize() {
        return workerListSize;
    }
    public void setWorkerListSize(int workerListSize) {
        this.workerListSize = workerListSize;
    }

    public int getPriceOfAllMission() {
        return priceOfAllMission;
    }
    public void setPriceOfAllMission(int priceOfAllMission) {
        this.priceOfAllMission = priceOfAllMission;
    }

    public int getAmountOfLeaf() {
        return amountOfLeaf;
    }
    public void setAmountOfLeaf(int amountOfLeaf) {
        this.amountOfLeaf = amountOfLeaf;
    }

    public List<Target> getWaitingTargetToExecute() {
        return waitingTargetToExecute;
    }
    public void setWaitingTargetToExecute(List<Target> targetToExecute) {
        this.waitingTargetToExecute = waitingTargetToExecute;
    }

    public List<Target> getTargets() {
        return targets;
    }
    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

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

    public void doMission(){
        while(isRunning){
            for (Worker worker : workerList)
            {
                if (worker.isAvailable() && waitingTargetToExecute.size() != 0){
                    worker.addTargetToList(waitingTargetToExecute.get(0));
                    waitingTargetToExecute.remove(0);
                }
            }
        }
    }

    public void missionSetUp(boolean formScratch){
        if(formScratch){
            for(Target t : targetToExecute){
                t.setStatus(Target.Status.Frozen);
            }
        }
        else{
            for(Target t : targetToExecute){
                if(t.getStatus()==Target.Status.Failure||t.getStatus()==Target.Status.Skipped)
                    t.setStatus(Target.Status.Frozen);
            }
        }
        for(Target t : targetToExecute){
            if(checkIfToTurnWait(t)){
                t.setStatus(Target.Status.Waiting);
            }
        }
    }
    private void fixTargetsStatues(){
        boolean done;
        do {
           done=true;
           for (Target t : targetToExecute) {
               if (t.getStatus() == Target.Status.Frozen) {
                   if(checkIfToTurnWait(t)){
                       t.setStatus(Target.Status.Waiting);
                       done=false;
                   }
                   else if(checkIfToTurnSkipped(t)){
                       t.setStatus(Target.Status.Skipped);
                       done=false;
                   }
               }
           }
        }while (!done);
    }
    private boolean checkIfToTurnWait(Target t){
        for (String s : t.getSetDependsOn()){
            for (int i = 0; i < targetToExecute.size(); i++){
                if (targetToExecute.get(i).getName().equals(s) && !(targetToExecute.get(i).getStatus() == Target.Status.Success || targetToExecute.get(i).getStatus() == Target.Status.Warning)) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkIfToTurnSkipped(Target t){
        for (String s : t.getSetDependsOn()){
            for (int i = 0; i < targetToExecute.size(); i++){
                if (targetToExecute.get(i).getName().equals(s) && (targetToExecute.get(i).getStatus() == Target.Status.Failure || targetToExecute.get(i).getStatus() == Target.Status.Skipped)) {
                    return true;
                }
            }
        }
        return false;
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
