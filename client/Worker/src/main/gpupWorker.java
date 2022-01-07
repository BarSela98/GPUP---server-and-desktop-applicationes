package main;

import component.mainApp.WorkerAppMainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static util.Constants.MAIN_PAGE_FXML_RESOURCE_LOCATION;

public class gpupWorker extends Application {


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

    public static void main(String[] args) {
        launch(args);
    }
}