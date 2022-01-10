package main;

import component.mainApp.AdminAppMainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import static utility.Constants.*;
//import static util.Constants.MAIN_PAGE_ADMIN_FXML_RESOURCE_LOCATION;

public class gpupAdmin extends Application {


    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("G.P.U.P - Admin");
      //  import static util.Constants.*;
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(MAIN_PAGE_ADMIN_FXML_RESOURCE_LOCATION);


        //URL url = getClass().getResource(ADMIN_PAGE_FXML_RESOURCE_LOCATION);


        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        AdminAppMainController controller = fxmlLoader.getController();


        //AdminPageController controller = fxmlLoader.getController();



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