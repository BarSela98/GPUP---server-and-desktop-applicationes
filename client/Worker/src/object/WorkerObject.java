package object;

import com.google.gson.Gson;
import engine.Target;
import error.errorMain;
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
    private Map<String,StatusOfWorkerInMission> statusOfWorkerInMissionMap;
    private List<Target> targetsInProgress;
    private int total=0;
    private boolean b= false;

    public WorkerObject(String nameOfWorker, int threadsNum) {
        this.nameOfWorker = nameOfWorker;
        this.threadsNum = threadsNum;
        curThreads = 0;
        completeTarget = new ArrayList<>();
        tasks = new ArrayList<>();
        targetsToExecute = new ArrayList<>();
        targetsInProgress = new ArrayList<>();
        statusOfWorkerInMissionMap = new HashMap<>();
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


    public List<Target> getTargetInProgress() {
        return targetsInProgress;
    }
    public List<Target> getCompleteTarget() {
        return completeTarget;
    }

    public void addToCompleteTarget(Target t) {
        completeTarget.add(t);
    }

    public Map<String, StatusOfWorkerInMission> getStatusOfWorkerInMissionMap() {
        return statusOfWorkerInMissionMap;
    }
    public void changeStatusOfWorkerInMission(String statusOfWorkerInMission , String nameOfMission) {
            this.statusOfWorkerInMissionMap.remove(nameOfMission);
            this.statusOfWorkerInMissionMap.put(nameOfMission, StatusOfWorkerInMission.valueOf(statusOfWorkerInMission));
    }

    public String getNameOfWorker() {
        return nameOfWorker;
    }

    public void addTargetToList(Target t){
        targetsToExecute.add(t);
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
                Platform.runLater(() -> new errorMain(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain("updateTargetInMission - WorkerObject - Response code: "+response.code()+"\nResponse body: "+responseBody));
                }
                else{
                    String responseBody = response.body().string();
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
            targetsInProgress.add(targetsToExecute.get(0));
            targetsToExecute.remove(0);
            execute(targetsInProgress.get(0));
        }
    }
    public void execute(Target t){
        Thread thread = new Thread(()->{
            curThreads++;
            t.run();
            while (t.isRunning()){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }
            total+=t.getPrice();
            updateTargetInMission(t);
            targetsInProgress.remove(0);
            curThreads--;
        });
        thread.start();
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
    public void setStatusOfWorkerInMissionMap(Map<String, StatusOfWorkerInMission> statusOfWorkerInMission) {
        this.statusOfWorkerInMissionMap = statusOfWorkerInMission;
    }
    public void setCompleteTarget(List<Target> completeTarget) {
        this.completeTarget = completeTarget;
    }
    public boolean isAvailable(String missionName){
        if(threadsNum-curThreads-targetsToExecute.size() > 0 && statusOfWorkerInMissionMap.get(missionName) == StatusOfWorkerInMission.DO)
            return true;
        else
            return false;
    }


}
