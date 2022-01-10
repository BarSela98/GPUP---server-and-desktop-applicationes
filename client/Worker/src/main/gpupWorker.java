package main;

import ODT.Target;
import component.mainApp.WorkerAppMainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.http.HttpClientUtil;
import com.google.gson.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static util.Constants.MAIN_PAGE_FXML_RESOURCE_LOCATION;

public class gpupWorker extends Application {

    int threadsNum;
    int curThreads;
    ArrayList<String> tasks;
    ArrayList<Target> targetsToExecute;
    public final static Gson gson = new Gson();
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("G.P.U.P - Worker");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root,500,300);
        primaryStage.setScene(scene);
        WorkerAppMainController controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    //    workerAppMainController.close();
    }
    public void connectToTask(String s){
        tasks.add(s);
    }
    public void pullTargets(int num){
        if(num>threadsNum-curThreads){
            //invalid request
            return;
        }
       // ArrayList<Target> targets=requestTargets(num);
    }
    /*
    public ArrayList<Target> requestTargets(int num){
        String[] t=new String[tasks.size()];
        for(int i=0;i<tasks.size();i++){
            t[i]=tasks.get(i);
        }
        //send request with t and num
        //get back array of json string targets and the number of targets sent n
        int n=5;
        String[] JsonTargets={"asd"};
        Target[] tars=new Target[n];
        for(int i=0;i<n;i++){
            tars[i]=gson.fromJson(JsonTargets[i],Target.class);
        }
        Thread thread=new Thread("task"){
            public void run(){
                for(int i=0;i<n;i++){
                    execute(tars[i]);
                }
            }
        }


    }

     */
    public void execute(Target t){
        curThreads++;
        Thread thread =new Thread(t);
        thread.start();
        while (thread.isAlive());
        curThreads--;
        //send back to engine when done
        //if you get to this comment then your done
    }
    public static void main(String[] args) {
        launch(args);
    }
    public boolean isAvailable(){
        return threadsNum>curThreads;
    }
    public void addTargetToList(Target t){
        targetsToExecute.add(t);
    }
}