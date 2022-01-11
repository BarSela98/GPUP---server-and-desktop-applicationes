package component.mainApp;

import component.login.loginController;
import component.page.AdminPageController;
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

import static utility.Constants.*;

public class AdminAppMainController {
    @FXML private AnchorPane mainPanel;
    private final StringProperty currentUserName;
    private Parent loginComponent;
    private loginController logicController;
    private Stage primaryStage;
    private Parent adminPageComponent;
    private AdminPageController adminPageController;

    @FXML public void initialize() {
        // userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));
        // prepare components
        loadLoginPage();
        loadAdminPage();
    }
    public AdminAppMainController() {
        currentUserName = new SimpleStringProperty(JHON_DOE);
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
    private void loadAdminPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL adminPageUrl = getClass().getResource(ADMIN_PAGE_FXML_RESOURCE_LOCATION);
            fxmlLoader.setLocation(adminPageUrl);
            adminPageComponent = fxmlLoader.load(adminPageUrl.openStream());
            adminPageController = fxmlLoader.getController();
            adminPageController.setAppMainController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void switchToAdminPage() {
        //setMainPanelTo(adminPageComponent);
        Scene scene = new Scene(adminPageComponent,1200,800);
        primaryStage.setScene(scene);
        adminPageController.setActive();
    }
    public void switchToLogin() {
        Platform.runLater(() -> {
            currentUserName.set(JHON_DOE);
            adminPageController.setInActive();
            //setMainPanelTo(loginComponent);
            Scene scene = new Scene(loginComponent,500,300);
            primaryStage.setScene(scene);
        });
    }
    public void updateUserName(String userName) {
        currentUserName.set(userName);
        adminPageController.setNameOfAdmin(userName);
    }
    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
       // mainPanel.resize(1000,500);
    }
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
