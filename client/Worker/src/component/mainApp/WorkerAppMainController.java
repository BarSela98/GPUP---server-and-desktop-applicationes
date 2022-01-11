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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static util.Constants.*;

public class WorkerAppMainController {

    @FXML private AnchorPane mainPanel;
    private final StringProperty currentUserName;
    private Parent loginComponent;
    private loginController logicController;
    private Stage primaryStage;
    private Parent workerPageComponent;
    private WorkerPageController workerPageController;

    public WorkerAppMainController() {
        currentUserName = new SimpleStringProperty(JHON_DOE);
    }
    @FXML public void initialize() {
        loadLoginPage();
        loadWorkerPage();
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
            workerPageComponent = fxmlLoader.load(workerPageUrl.openStream());
            workerPageController = fxmlLoader.getController();
            workerPageController.setAppMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void switchToWorkerPage() {
        //setMainPanelTo(workerPageComponent);
        workerPageController.setActive();
        Scene scene = new Scene(workerPageComponent,1100,800);
        primaryStage.setScene(scene);
    }
    public void switchToLogin() {
        Platform.runLater(() -> {
            currentUserName.set(JHON_DOE);
            workerPageController.setInActive();
            Scene scene = new Scene(loginComponent,500,300);
            primaryStage.setScene(scene);
           // setMainPanelTo(loginComponent);
        });
    }
    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
        mainPanel.resize(1000,500);
        mainPanel.setPrefHeight(500);
        mainPanel.setPrefWidth(1000);
        mainPanel.setMinHeight(500);
        mainPanel.setMinWidth(1000);
        mainPanel.setMaxWidth(1000);
    }
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
        workerPageController.setNameOfWorker(userName);
    }
}
