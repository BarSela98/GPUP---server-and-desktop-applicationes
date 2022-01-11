package object;

import ODT.Target;
import com.google.gson.Gson;
import error.errorMain;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utility.Constants.UPDATE_TARGET_IN_MISSION;

public class WorkerObject {
    private String nameOfWorker;
    private int threadsNum;
    private int curThreads;
    private List<String> tasks;
    private List<Target> targetsToExecute;

    public WorkerObject(String nameOfWorker, int threadsNum) {
        this.nameOfWorker = nameOfWorker;
        this.threadsNum = threadsNum;
        curThreads = 0;
        tasks = new ArrayList<>();
        targetsToExecute = new ArrayList<>();
    }


    public String getNameOfWorker() {
        return nameOfWorker;
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

    public List<Target> getTargetsToExecute() {
        return targetsToExecute;
    }

    public void setTargetsToExecute(List<Target> targetsToExecute) {
        this.targetsToExecute = targetsToExecute;
    }



    public void connectToTask(String s){
        tasks.add(s);
    }
    public void pullTargets(){
        while (true){
            if(!targetsToExecute.isEmpty()){
                execute(targetsToExecute.get(0));
                targetsToExecute.remove(0);
            }
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
    public boolean isAvailable(){
        return threadsNum>curThreads;
    }
    public void addTargetToList(Target t){
        targetsToExecute.add(t);
    }
    public void updateTargetInMission(Target t){
        String json = new Gson().toJson(t);
        String finalUrl = HttpUrl
                .parse(UPDATE_TARGET_IN_MISSION)
                .newBuilder()
                .build()
                .toString();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> new errorMain(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain(new Exception("Response code: "+response.code()+"\nResponse body: "+responseBody)));
                }
                else{
                    Platform.runLater(() -> {Platform.runLater(() -> System.out.println("update target"));});
                }
            }
        });
    }

    /*
    Thread askForTargets=new Thread("askForTargets"){
            public void run(){
                pullTargets();
            }
        };
     */


}
