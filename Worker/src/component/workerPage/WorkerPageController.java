package component.workerPage;

import component.api.WorkerCommands;
import component.mainApp.WorkerAppMainController;
import component.usersList.UsersListController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;


public class WorkerPageController implements WorkerCommands {


    private WorkerAppMainController workerAppMainController;
    @FXML private UsersListController usersListComponentController;
    @FXML private BorderPane usersListComponent;


    @FXML public void initialize() {
        if (usersListComponentController != null)
            usersListComponentController.setMainController(this);
    }

    public void setActive() {
        usersListComponentController.startListRefresher();
    }

    public void setInActive() {
        try {
            usersListComponentController.close();
        } catch (Exception ignored) {}
    }

    public void setAppMainController(WorkerAppMainController workerAppMainController) {
        this.workerAppMainController = workerAppMainController;
    }

    @FXML void doMission(ActionEvent event) {}

    @FXML void pauseMission(ActionEvent event) {}

    @FXML void quitButton(ActionEvent event) {
        logout();
    }
    @Override
    public void logout() {
        workerAppMainController.switchToLogin();
    }
    @FXML void resumeMisson(ActionEvent event) {
    }
    @FXML void stopMission(ActionEvent event) {
    }

    @FXML private TableView<?> tableViewUser;
    @FXML private Text nameOfWorker;
    @FXML private Text amountOfResources;
    @FXML private Text yourCradit;
    @FXML private TableView<?> missionTable;
    @FXML private TableColumn<?, ?> checkBoxTableMission;
    @FXML private TableColumn<?, ?> nameOfMissionCol;
    @FXML private TableColumn<?, ?> TaskCol;
    @FXML private TableColumn<?, ?> WorkerCol;
    @FXML private TableColumn<?, ?> ProgressCol;
    @FXML private TableColumn<?, ?> yourDoneCol;
    @FXML private TableColumn<?, ?> craditsCol;
    @FXML private TableColumn<?, ?> yourStatusCol;
    @FXML private TableView<?> executeTargetTable;
    @FXML private TableColumn<?, ?> nameOfMissionCol_target;
    @FXML private TableColumn<?, ?> nameOfTaskCol_target;
    @FXML private TableColumn<?, ?> nameOfTargetCol;
    @FXML private TableColumn<?, ?> targetStatusCol;
    @FXML private TableColumn<?, ?> targetCraditsCol;

}
