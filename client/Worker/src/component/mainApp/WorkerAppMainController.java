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
    private boolean logout = false;

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
        if (logout)
            loadWorkerPage();
        Scene scene = new Scene(workerPageComponent,1200,800);
        primaryStage.setScene(scene);
        workerPageController.setActive();
        logout = true;
    }
    public void switchToLogin() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
                fxmlLoader.setLocation(url);
                Parent root = fxmlLoader.load(url.openStream());
                WorkerAppMainController controller = fxmlLoader.getController();
                controller.setStage(primaryStage);
                Scene scene = new Scene(root, 500, 300);
                primaryStage.setScene(scene);
                loadLoginPage();
            } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void updateUser(String userName , int amountOfResources) {
        currentUserName.set(userName);
        workerPageController.setNameOfWorker(userName);
        workerPageController.setAmountOfResources(String.valueOf(amountOfResources));
        workerPageController.setYourCredit(String.valueOf(0));
    }
}
