package object;

import engine.Target;
import com.google.gson.Gson;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utility.Constants.UPDATE_TARGET_IN_MISSION;

public class WorkerObject {
    public enum StatusOfWorkerInMission {DO, PAUSE, STOP}
    private String nameOfWorker;
    private int threadsNum;
    private int curThreads;
    private List<String> tasks;
    private List<Target> targetsToExecute;
    private List<Target> completeTarget;
    private Map<String,StatusOfWorkerInMission> statusOfWorkerInMission;
    private boolean b= false;

    public WorkerObject(String nameOfWorker, int threadsNum) {
        this.nameOfWorker = nameOfWorker;
        this.threadsNum = threadsNum;
        curThreads = 0;
        completeTarget = new ArrayList<>();
        tasks = new ArrayList<>();
        targetsToExecute = new ArrayList<>();
        statusOfWorkerInMission = new HashMap<>();
        Thread doTask = new Thread(()->{
            try {
                while (true) {
                    Thread.sleep(50);
                    pullTargets();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        doTask.start();
    }


    public List<Target> getCompleteTarget() {
        return completeTarget;
    }
    public void addToCompleteTarget(Target t) {
        completeTarget.add(t);
        System.out.println("add complete");
    }

    public Map<String, StatusOfWorkerInMission> getStatusOfWorkerInMission() {
        return statusOfWorkerInMission;
    }
    public void changeStatusOfWorkerInMission(String statusOfWorkerInMission , String nameOfMission) {
        this.statusOfWorkerInMission.remove(nameOfMission);
        this.statusOfWorkerInMission.put(nameOfMission, StatusOfWorkerInMission.valueOf(statusOfWorkerInMission));
    }

    public String getNameOfWorker() {
        return nameOfWorker;
    }

    public void addTargetToList(Target t){
        targetsToExecute.add(t);
        System.out.println("add target "+t+" targetsToExecute in "+ this);
    }
    public void updateTargetInMission(Target t){
        String json = new Gson().toJson(t);
        String finalUrl = HttpUrl
                .parse(UPDATE_TARGET_IN_MISSION)
                .newBuilder()
                .addQueryParameter("username", nameOfWorker)
                .build()
                .toString();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    System.out.println("updateTargetInMission - WorkerObject - Response code: "+response.code()+"\nResponse body: "+responseBody);
                }
                else{
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
    }

/// Execute
    public List<Target> getTargetsToExecute() {
        return targetsToExecute;
    }
    public void pullTargets(){
        if(targetsToExecute.size() != 0){
            execute(targetsToExecute.get(0));
            targetsToExecute.remove(0);
        }
    }
    public void execute(Target t){
        curThreads++;

        Thread thread =new Thread(t);
        thread.start();
        try {
            Thread.sleep(10);
            while (t.isRunning()){
                Thread.sleep(10);
            }
        }
        catch (Exception e){}
        curThreads--;
        updateTargetInMission(t);
        //send back to engine when done
        //if you get to this comment then your done
    }




    public void setTargetsToExecute(List<Target> targetsToExecute) {
        this.targetsToExecute = targetsToExecute;
    }
    public void connectToTask(String s){
        tasks.add(s);
    }
    public void setNameOfWorker(String nameOfWorker) {
        this.nameOfWorker = nameOfWorker;
    }
    public int getThreadsNum() {
        return threadsNum;
    }
    public void setThreadsNum(int threadsNum) {
        this.threadsNum = threadsNum;
    }
    public int getCurThreads() {
        return curThreads;
    }
    public void setCurThreads(int curThreads) {
        this.curThreads = curThreads;
    }
    public List<String> getTasks() {
        return tasks;
    }
    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
    public void setStatusOfWorkerInMission(Map<String, StatusOfWorkerInMission> statusOfWorkerInMission) {
        this.statusOfWorkerInMission = statusOfWorkerInMission;
    }
    public void setCompleteTarget(List<Target> completeTarget) {
        this.completeTarget = completeTarget;
    }
    public boolean isAvailable(String missionName){
        if(threadsNum>curThreads && statusOfWorkerInMission.get(missionName) == StatusOfWorkerInMission.DO)
            return true;
        else
            return false;
    }


}
