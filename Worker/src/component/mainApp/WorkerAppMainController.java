package component.mainApp;
import component.login.loginController;
import component.workerPage.WorkerPageController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import util.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import static util.Constants.*;

public class WorkerAppMainController {

    @FXML private AnchorPane mainPanel;
    private final StringProperty currentUserName;
    private Parent loginComponent;
    private loginController logicController;

    private Parent workerPageComponent;
    private WorkerPageController workerPageController;



   // private final StringProperty currentUserName;

    public WorkerAppMainController() {
        currentUserName = new SimpleStringProperty(JHON_DOE);
    }

    @FXML public void initialize() {
       // userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));
        // prepare components
        loadLoginPage();
       // loadChatRoomPage();
    }

    private void setPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }

    private void loadLoginPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load(loginPageUrl.openStream());
            logicController = fxmlLoader.getController();
            logicController.setAppMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadWorkerPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL workerPageUrl = getClass().getResource(WORKER_PAGE_FXML_RESOURCE_LOCATION);
            fxmlLoader.setLocation(workerPageUrl);
            loginComponent = fxmlLoader.load(workerPageUrl.openStream());
            workerPageController = fxmlLoader.getController();
            workerPageController.setAppMainController(this);
            setMainPanelTo(workerPageComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void switchToWorkerPage() {
        setMainPanelTo(workerPageComponent);
        workerPageController.setActive();
    }

    public void switchToLogin() {
        Platform.runLater(() -> {
            currentUserName.set(JHON_DOE);
            workerPageController.setInActive();
            setMainPanelTo(loginComponent);
        });
    }
    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }

}
