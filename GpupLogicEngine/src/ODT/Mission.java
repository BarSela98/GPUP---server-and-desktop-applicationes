package ODT;

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
    private List<Target> targetToExecute;
    private Utility.TypeOfRunning typeOfRunning;
    private Simulation simulation;
    private Compilation compilation;
    public Mission(String nameOfMission, String nameOfCreator, List<Target> targetToExecute, Utility.WhichTask whichTask,Utility.TypeOfRunning typeOfRunning ,Compilation compilation) {
        this.compilation = compilation;
        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targetToExecute = targetToExecute;
        this.priceOfMission = compilation.getPriceOfCompilation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;


        amountOfTarget = 0;
        amountOfRoot = 0;
        amountOfMiddle = 0;
        amountOfIndependents = 0;
        amountOfLeaf = 0;

        for (Target t: targetToExecute) {
            Target.Type type = t.getType();
            amountOfTarget++;
            if (type == Target.Type.ROOT)
                amountOfRoot++;
            else if (type == Target.Type.INDEPENDENTS)
                amountOfIndependents++;
            else if (type == Target.Type.MIDDLE)
                amountOfMiddle++;
            else if (type == Target.Type.LEAF)
                amountOfLeaf++;
        }
        priceOfAllMission = priceOfMission * amountOfTarget;

    }

    public int getPriceOfAllMission() {
        return priceOfAllMission;
    }

    public void setPriceOfAllMission(int priceOfAllMission) {
        this.priceOfAllMission = priceOfAllMission;
    }

    public Mission(String nameOfMission, String nameOfCreator, List<Target> targetToExecute, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning , Simulation simulation) {
        this.simulation = simulation;

        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targetToExecute = targetToExecute;
        this.priceOfMission = simulation.getPriceOfSimulation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;

        amountOfTarget = 0;
        amountOfRoot = 0;
        amountOfMiddle = 0;
        amountOfIndependents = 0;
        amountOfLeaf = 0;

        for (Target t: targetToExecute) {
            Target.Type type = t.getType();
            amountOfTarget++;
            if (type == Target.Type.ROOT)
                amountOfRoot++;
            else if (type == Target.Type.INDEPENDENTS)
                amountOfIndependents++;
            else if (type == Target.Type.MIDDLE)
                amountOfMiddle++;
            else if (type == Target.Type.LEAF)
                amountOfLeaf++;
        }
        priceOfAllMission = priceOfMission * amountOfTarget;

    }

    @Override
    public String toString() {
        return "Mission{" +
                "nameOfMission='" + nameOfMission + '\'' +
                ", nameOfCreator='" + nameOfCreator + '\'' +
                ", amountOfTarget=" + amountOfTarget +
                ", amountOfRoot=" + amountOfRoot +
                ", amountOfMiddle=" + amountOfMiddle +
                ", amountOfIndependents=" + amountOfIndependents +
                ", priceOfMission=" + priceOfMission +
                ", workers=" + workers +
                ", statusOfMission=" + statusOfMission +
                ", targetToExecute=" + targetToExecute +
                '}';
    }

    public int getAmountOfLeaf() {
        return amountOfLeaf;
    }

    public void setAmountOfLeaf(int amountOfLeaf) {
        this.amountOfLeaf = amountOfLeaf;
    }

    public List<Target> getTargetToExecute() {
        return targetToExecute;
    }

    public void setTargetToExecute(List<Target> targetToExecute) {
        this.targetToExecute = targetToExecute;
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
