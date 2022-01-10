package ODT;

import main.gpupWorker;
import utility.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mission {
    public enum StatusOfMission {Done, Waiting, inProgress}

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
    private List<gpupWorker> workerList;
    private int workerListSize;
    private boolean isRunning;
    private boolean isPause;
    private boolean isStop;

    public void setStatus(String statusOfMission) {
        switch (statusOfMission) {
            case "run", "resume" -> this.setRunning(true);
            case "pause" -> this.setPause(true);
            case "stop" -> this.setStop(true);
        }
    }





    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        if (isRunning){
            isPause = false;
            isStop = false;
        }
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
        if (isPause){
            isRunning = false;
            isStop = false;
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop){
            isPause = false;
            isRunning = false;
        }
    }



    public Mission(String nameOfMission, String nameOfCreator, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Compilation compilation) {
        this.compilation = compilation;
        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targets = targets;
        this.priceOfMission = compilation.getPriceOfCompilation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;
        missionBuilder();
        compilationSetUp();
    }

    public Mission(String nameOfMission, String nameOfCreator, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Simulation simulation) {
        this.simulation = simulation;

        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targets = targets;
        this.priceOfMission = simulation.getPriceOfSimulation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;
        missionBuilder();
        simulationSetUp();
    }

    public void missionBuilder() {
        this.workerList = new ArrayList<>();
        this.waitingTargetToExecute = new ArrayList<>();
        workerListSize = 0;
        amountOfTarget = 0;
        amountOfRoot = 0;
        amountOfMiddle = 0;
        amountOfIndependents = 0;
        amountOfLeaf = 0;

        for (Target t : targets) {
            t.setMission(nameOfMission);
            Target.Type type = t.getType();
            amountOfTarget++;
            if (type == Target.Type.ROOT)
                amountOfRoot++;
            else if (type == Target.Type.INDEPENDENTS){
                amountOfIndependents++;
            }
            else if (type == Target.Type.MIDDLE)
                amountOfMiddle++;
        else if (type == Target.Type.LEAF) {
            amountOfLeaf++;
        }
    }
    if(typeOfRunning==Utility.TypeOfRunning.SCRATCH){
        missionSetUp(true);
    }
    else{
        missionSetUp(false);
    }
    priceOfAllMission =priceOfMission *amountOfTarget;

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
        Thread doMissionThread = new Thread(()->{
            while (!isStop){
                while(isRunning && !isStop && !isPause){
                    fixTargetsStatues();
                    for (gpupWorker worker : workerList)
                    {
                        if (worker.isAvailable() && waitingTargetToExecute.size() != 0){
                            worker.addTargetToList(waitingTargetToExecute.get(0));
                            waitingTargetToExecute.remove(0);
                        }
                    }
                }
                while(!isStop && isPause){
                    for (gpupWorker worker : workerList)
                    {
                        if (worker.isAvailable() && waitingTargetToExecute.size() != 0){
                            worker.addTargetToList(waitingTargetToExecute.get(0));
                            waitingTargetToExecute.remove(0);
                        }
                    }
                }
            }
        });
    }

    public void missionSetUp(boolean formScratch){
        if(formScratch){
            for(Target t : targets){
                t.setStatus(Target.Status.Frozen);
            }
        }
        else{
            for(Target t : targets){
                if(t.getStatus()==Target.Status.Failure||t.getStatus()==Target.Status.Skipped)
                    t.setStatus(Target.Status.Frozen);
            }
        }
        for(Target t : targets){
            if(checkIfToTurnWait(t)){
                t.setStatus(Target.Status.Waiting);
                t.setStartWaitingTime(System.currentTimeMillis());
                waitingTargetToExecute.add(t);
            }
        }
    }
    private void fixTargetsStatues(){
        boolean done;
        do {
           done=true;
           for (Target t : targets) {
               if (t.getStatus() == Target.Status.Frozen) {
                   if(checkIfToTurnWait(t)){
                       t.setStatus(Target.Status.Waiting);
                       t.setStartWaitingTime(System.currentTimeMillis());
                       waitingTargetToExecute.add(t);
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
            for (int i = 0; i < targets.size(); i++){
                if (targets.get(i).getName().equals(s) && !(targets.get(i).getStatus() == Target.Status.Success || targets.get(i).getStatus() == Target.Status.Warning)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkIfToTurnSkipped(Target t){
        for (String s : t.getSetDependsOn()){
            for (int i = 0; i < targets.size(); i++){
                if (targets.get(i).getName().equals(s) && (targets.get(i).getStatus() == Target.Status.Failure || targets.get(i).getStatus() == Target.Status.Skipped)) {
                    t.setStatus(Target.Status.Skipped);
                    t.setSimTimeString("00:00:00:00");
                    try {
                        File f = new File(t.getPath());
                        f.createNewFile();
                        FileWriter w = new FileWriter(t.getPath());
                        w.write("Target name: " + t.getName() + "\n\r" +
                                "Target result: " + t.getStatus().name() + "\n\r" +
                                "Target time : 00:00:00:00 \n\r");
                        w.close();
                    }
                    catch (Exception e){}
                    return true;
                }
            }
        }
        return false;
    }
    public boolean checkIfMissionDone(){
        for(Target t : targets){
            if(t.getStatus()==Target.Status.Waiting||t.getStatus()==Target.Status.Frozen){
                return false;
            }
        }
        return true;
    }
    private void simulationSetUp(){
        int randomTime;
        Random r = new Random();
        String path="c\\gpup-working-dir";//temp value
        try {
            path= openDir("simulation");
        }
        catch (Exception e){}
        for(Target t:targets){
            if(simulation.isRandom())
                randomTime=r.nextInt(simulation.getTime())+1;
            else
                randomTime =simulation.getTime();
            t.setCompile(false);
            t.setRunTime(randomTime);
            t.setSuccessChance(simulation.getSuccess());
            t.setWarningChance(simulation.getWarning());
            t.setPath(path);
        }
    }
    private void compilationSetUp(){
        String path="c\\gpup-working-dir";//temp value
        try {
            path= openDir("simulation");
        }
        catch (Exception e){}
        for(Target t:targets){
            t.setCompile(true);
            t.setSource(compilation.getSourceFolder());
            t.setCompileDest(compilation.getTargetFolder());
            t.setPath(path);
        }
    }
    private String openDir(String taskType) throws IOException {//doesnt have path yet,this func create directory for simulation task
        Path path= Paths.get("c\\gpup-working-dir");
        Files.createDirectories(path);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.mm.yyyy HH.MM.SS");
        LocalDateTime now = LocalDateTime.now();
        String s =taskType+" "+dtf.format(now);
        s= "c\\gpup-working-dir"+"\\"+s;
        Path innerPath=Paths.get(s);
        Files.createDirectories(innerPath);
        return s;
    }
    public void updateTarget(Target tar){
        for(Target t:targets){
            if(t.getName().equals(tar.getName())){
                t=tar;
            }
        }
    }
}
