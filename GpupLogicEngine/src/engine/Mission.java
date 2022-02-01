package engine;

import ODT.Compilation;
import ODT.Simulation;
import ODT.TargetToWorker;
import ODT.WorkerStatus;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;
import utility.Utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utility.Constants.SEND_TARGET_TO_MISSION;

public class Mission {

    public enum statusOfMission {PAUSE , DONE , WAITING, INPROGRESS , STOP}
    final Object lock = new Object();
    private String nameOfMission;
    private String nameOfCreator;
    private String nameOfGraph;
    private int amountOfCompleteTarget = 0;
    private int amountOfTarget;
    private int amountOfRoot;
    private int amountOfMiddle;
    private int amountOfIndependents;
    private int amountOfLeaf;
    private int priceOfMission;
    private int priceOfAllMission;
    private Utility.WhichTask whichTask;
    private int count=0;
    private statusOfMission statusOfMission;
    private List<Target> targets;
    private List<Target> waitingTargetToExecute;
    private Utility.TypeOfRunning  typeOfRunning;
    private Simulation simulation;
    private Compilation compilation;
    private Map<String, WorkerStatus> workerList;
    private int availableWorker;
    private int signWorkerSize;
    private String progress;
    private int targetWaiting = 0;
    private int targetInProgress = 0;


    public Mission(Mission m){
        this.nameOfMission = m.getNameOfMission();
        this.nameOfCreator  = m.getNameOfCreator();
        this.nameOfGraph = m.nameOfGraph;
        this.amountOfTarget = m.getAmountOfTarget();
        this.amountOfRoot = m.getAmountOfRoot();
        this.amountOfMiddle = m.getAmountOfMiddle();
        this.amountOfIndependents = m.getAmountOfIndependents();
        this.amountOfLeaf = m.getAmountOfLeaf();
        this.priceOfMission = m.getPriceOfMission();
        this.priceOfAllMission = m.getPriceOfAllMission();
        this.whichTask = m.getWhichTask();
        this.statusOfMission = m.getStatusOfMission();
        this.targets = m.getTargets();
        this.waitingTargetToExecute = m.getWaitingTargetToExecute();
        this.typeOfRunning = m.getTypeOfRunning();
        this.simulation = m.getSimulation();
        this.compilation = m.getCompilation();
        this.workerList = m.getWorkerList();
        this.availableWorker = m.getAvailableWorker();
        this.count=m.getCount();
        this.signWorkerSize=m.getSignWorkerSize();

        this.progress =m.getProgress();
        this.targetWaiting = m.getTargetWaiting();
        this.targetInProgress = m.getTargetInProgress();
    }
    public Mission(Mission m,boolean fromScratch,int c){
        this.nameOfMission = m.getNameOfMission();
        this.nameOfCreator  = m.getNameOfCreator();
        this.nameOfGraph = m.nameOfGraph;
        this.amountOfTarget = m.getAmountOfTarget();
        this.amountOfRoot = m.getAmountOfRoot();
        this.amountOfMiddle = m.getAmountOfMiddle();
        this.amountOfIndependents = m.getAmountOfIndependents();
        this.amountOfLeaf = m.getAmountOfLeaf();
        this.priceOfMission = m.getPriceOfMission();
        this.priceOfAllMission = m.getPriceOfAllMission();
        this.whichTask = m.getWhichTask();
        this.statusOfMission = statusOfMission.WAITING;
        this.targets = m.getTargets();
        this.waitingTargetToExecute = m.getWaitingTargetToExecute();
        this.typeOfRunning = m.getTypeOfRunning();
        this.simulation = m.getSimulation();
        this.compilation = m.getCompilation();
        this.workerList = m.getWorkerList();
        this.availableWorker = m.getAvailableWorker();
        this.nameOfMission=m.nameOfMission+ c;
        this.signWorkerSize=m.getSignWorkerSize();
        this.progress =m.getProgress();
        this.targetWaiting = m.getTargetWaiting();
        this.targetInProgress = m.getTargetInProgress();

        if(fromScratch){
            for(Target t:targets){
                t.setStatus(Target.Status.Frozen);
            }
        }
        else{
            for(Target t:targets){
                if(t.getStatus()==Target.Status.Failure||t.getStatus()==Target.Status.Skipped)
                    t.setStatus(Target.Status.Frozen);
            }
        }
    }
    public Mission(String nameOfMission, String nameOfCreator,String nameOfGraph, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Compilation compilation) {
        this.compilation = compilation;
        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.nameOfGraph = nameOfGraph;
        this.targets = targets;
        this.priceOfMission = compilation.getPriceOfCompilation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;
        missionBuilder();
        compilationSetUp();

    }
    public Mission(String nameOfMission, String nameOfCreator,String nameOfGraph, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Simulation simulation) {
        this.simulation = simulation;

        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.nameOfGraph = nameOfGraph;
        this.targets = targets;
        this.priceOfMission = simulation.getPriceOfSimulation();
        this.whichTask = whichTask;
        this.typeOfRunning = typeOfRunning;
        missionBuilder();
        simulationSetUp();
    }
    public void missionBuilder() {
        this.workerList = new HashMap<>();
        this.waitingTargetToExecute = new ArrayList<>();
        availableWorker = 0;
        amountOfTarget = 0;
        amountOfRoot = 0;
        amountOfMiddle = 0;
        amountOfIndependents = 0;
        amountOfLeaf = 0;
        this.signWorkerSize=0;
        this.statusOfMission = statusOfMission.WAITING;
        this.progress = "-";
        this.targetWaiting = 0;
        this.targetInProgress = 0;

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
        priceOfAllMission = priceOfMission * amountOfTarget;

    }


    public synchronized void workerSign(String worker) {
        workerList.put(worker,new WorkerStatus());
        availableWorker++;
        signWorkerSize++;
    }
    public synchronized void removeWorkerFromMission(String workerName) {
        if (workerList.containsKey(workerName)){
            workerList.remove(workerName);
            availableWorker--;
            signWorkerSize--;
        }
    }
    public void updateTarget(Target tar) {
            for (Target t : targets) {
                if (t.getName().equals(tar.getName())) {
                    t.updateInfo(tar);
                    amountOfCompleteTarget++;
                    progress = String.valueOf((float) amountOfTarget / (float) amountOfCompleteTarget);
                }
            }
    }
    public void doMission(){
        Thread doMissionThread = new Thread(()->{
            while (statusOfMission != statusOfMission.STOP && statusOfMission != statusOfMission.DONE) {
                while (statusOfMission == statusOfMission.INPROGRESS && statusOfMission != statusOfMission.STOP && statusOfMission != statusOfMission.PAUSE && statusOfMission != statusOfMission.DONE) {
                    fixTargetsStatues();
                    sendTargetToAvailableWorker();

                }
                checkIfMissionDone();
                while (statusOfMission != statusOfMission.STOP && statusOfMission != statusOfMission.DONE && statusOfMission == statusOfMission.PAUSE) {
                    sendTargetToAvailableWorker();
                }
            }
        });
        doMissionThread.start();
    }

    private void sendTargetToAvailableWorker() {
        for (String worker : workerList.keySet()) {
            if (availableWorker != 0 && workerList.get(worker).getStatus() && waitingTargetToExecute.size() != 0) {
                targetInProgress++;
                Target t = waitingTargetToExecute.get(0);
                waitingTargetToExecute.remove(0);
                sendTargetToWorker(worker, t);
                workerList.get(worker).setStatus(false);
                availableWorker--;
            }
        }
        checkIfMissionDone();
    }

    private void sendTargetToWorker(String worker, Target target) {
        TargetToWorker targetToWorker = new TargetToWorker(target,worker);
        String json = new Gson().toJson(targetToWorker);        // to gson
        String finalUrl = HttpUrl
                .parse(SEND_TARGET_TO_MISSION)
                .newBuilder()
                .build()
                .toString();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {

            @Override
            public void onFailure(@org.jetbrains.annotations.NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {
                System.out.println(e.getStackTrace());
                //Platform.runLater(() -> new errorMain(e));
            }

            @Override
            public void onResponse(@org.jetbrains.annotations.NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
               //     Platform.runLater(() -> new errorMain(new Exception("Response code: "+response.code()+"\nResponse body: "+responseBody)));
                }
                else{

                    Boolean bool = Boolean.valueOf(response.body().string());
                    workerList.get(worker).setStatus(bool);
                    if (bool)
                        availableWorker++;
                    else{
                        if (availableWorker != 0)
                            availableWorker--;
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
        targetWaiting = waitingTargetToExecute.size();
    }
    private synchronized void fixTargetsStatues(){
        boolean done;
        do {
            done=true;
            synchronized(lock) {
                for (Target t : targets) {
                    if (t.getStatus() == Target.Status.Frozen) {
                        if (checkIfToTurnWait(t)) {
                            t.setStatus(Target.Status.Waiting);
                            t.setStartWaitingTime(System.currentTimeMillis());
                            waitingTargetToExecute.add(t);
                            done = false;
                        } else if (checkIfToTurnSkipped(t)) {
                            t.setStatus(Target.Status.Skipped);
                            done = false;
                        }
                    }
                }
                targetWaiting = waitingTargetToExecute.size();
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
        boolean statusOfMissionBool = true;
        int IncompleteTargets = 0;

        for(Target t : targets){
            if(t.getStatus()==Target.Status.Waiting||t.getStatus()==Target.Status.Frozen){
                statusOfMissionBool = false;
                IncompleteTargets++;
            }
        }
        if (statusOfMissionBool)
            statusOfMission = statusOfMission.DONE;

        double d= ((double)(targets.size() - IncompleteTargets) / (double)targets.size()) * 100 ;
        progress = d + " %";

        return statusOfMissionBool;
    }
    private void simulationSetUp(){
        int randomTime;
        Random r = new Random();
        String path="C:\\gpup-working-dir";//temp value
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
            t.setNameOfTask("simulation");
        }
    }
    private void compilationSetUp(){
        String path="C:\\gpup-working-dir";//temp value
        try {
            path= openDir("compilation");
        }
        catch (Exception e){}
        for(Target t:targets){
            t.setCompile(true);
            t.setSource(compilation.getSourceFolder());
            t.setCompileDest(compilation.getTargetFolder());
            t.setPath(path);
            t.setNameOfTask("compilation");

        }
    }
    private String openDir(String taskType) throws IOException {//doesnt have path yet,this func create directory for simulation task
        Path path= Paths.get("C:\\gpup-working-dir");
        Files.createDirectories(path);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.mm.yyyy HH.MM.SS");
        LocalDateTime now = LocalDateTime.now();
        String s =taskType+" "+dtf.format(now);
        s= "C:\\gpup-working-dir"+"\\"+s;
        Path innerPath=Paths.get(s);
        Files.createDirectories(innerPath);
        return s;
    }

    public String getProgress() {
        return progress;
    }
    public void setProgress(String progress) {
        this.progress = progress;
    }

    public Map<String, WorkerStatus> getWorkerList() {
        return workerList;
    }

    public void setWorkerList(Map<String, WorkerStatus> workerList) {
        this.workerList = workerList;
        availableWorker = workerList.size();
    }

    public int getAvailableWorker() {
        return availableWorker;
    }
    public void setAvailableWorker(int availableWorker) {
        this.availableWorker = availableWorker;
    }

    public Utility.WhichTask getWhichTask() {
        return whichTask;
    }
    public void setWhichTask(Utility.WhichTask whichTask) {
        this.whichTask = whichTask;
    }

    public Utility.TypeOfRunning getTypeOfRunning() {
        return typeOfRunning;
    }
    public void setTypeOfRunning(Utility.TypeOfRunning typeOfRunning) {
        this.typeOfRunning = typeOfRunning;
    }

    public Simulation getSimulation() {
        return simulation;
    }
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Compilation getCompilation() {
        return compilation;
    }
    public void setCompilation(Compilation compilation) {
        this.compilation = compilation;
    }

    public String getNameOfGraph() {
        return nameOfGraph;
    }
    public void setNameOfGraph(String nameOfGraph) {
        this.nameOfGraph = nameOfGraph;
    }

/*
    public boolean getIsRunning() {
        return isRunning;
    }
    public void setIsRunning(boolean running) {
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


     */
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

    public Mission.statusOfMission getStatusOfMission() {
        return statusOfMission;
    }

    public void setStatusOfMission(Mission.statusOfMission statusOfMission) {
        this.statusOfMission = statusOfMission;
    }
    public void setStatus(String newStatusOfMission) {
        switch (newStatusOfMission) {
            case "run":
                this.statusOfMission = statusOfMission.INPROGRESS;
                break;
            case "pause":
                this.statusOfMission = statusOfMission.PAUSE;
                break;
            case "resume":
                this.statusOfMission = statusOfMission.INPROGRESS;
                break;
            case "stop":
                this.statusOfMission = statusOfMission.STOP;
                break;
        }
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public int getSignWorkerSize() {
        return signWorkerSize;
    }
    public void setSignWorkerSize(int signWorkerSize) {
        this.signWorkerSize = signWorkerSize;
    }

    public int getTargetWaiting() {
        return targetWaiting;
    }
    public void setTargetWaiting(int targetWaiting) {
        this.targetWaiting = targetWaiting;
    }

    public int getTargetInProgress() {
        return targetInProgress;
    }
    public void setTargetInProgress(int targetInProgress) {
        this.targetInProgress = targetInProgress;
    }
}
