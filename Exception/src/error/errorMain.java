package error;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class errorMain {

    public final static String ERROR_FXML = "/error/erroe.fxml";

    public errorMain(Exception e) {
        try {
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Exception");
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource(ERROR_FXML);
            fxmlLoader.setLocation(url);
            Parent root = fxmlLoader.load(url.openStream());
            Scene scene = new Scene(root, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.show();
            errorController controller = fxmlLoader.getController();
            controller.error(e);
        }
        catch (Exception ex){}
    }
    public errorMain(String e) {
        try {
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Exception");
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource(ERROR_FXML);
            fxmlLoader.setLocation(url);
            Parent root = fxmlLoader.load(url.openStream());
            Scene scene = new Scene(root, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.show();
            errorController controller = fxmlLoader.getController();
            controller.error(e);
        }
        catch (Exception ex){}
    }

}
