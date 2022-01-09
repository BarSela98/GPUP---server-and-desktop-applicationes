package main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
//import static util.Constants.MAIN_PAGE_ADMIN_FXML_RESOURCE_LOCATION;

public class test extends Application {


    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("G.P.U.P - Admin");
        //  import static util.Constants.*;
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/component/page/adminPage.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        //AdminPageController controller = fxmlLoader.getController();
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