package component.usersList;

import ODT.User;
import component.page.AdminPageController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static utility.Constants.*;

public class UsersListController implements Closeable {
   AdminPageController adminPageController;
    @FXML private TableView<User> tableViewUsers;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> roleCol;
    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalUsers;
    private final ObservableList <User> newItems = FXCollections.observableArrayList();

    @FXML public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        tableViewUsers.setItems(newItems);
    }
    public void setMainController(AdminPageController adminPageController) {
        this.adminPageController = adminPageController;
    }
    public UsersListController() {
        autoUpdate = new SimpleBooleanProperty(true);
        totalUsers = new SimpleIntegerProperty(0);
    }
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }
    private void updateUsersList(List<User> usersNames) {
        Platform.runLater(() -> {
            ObservableList<User> items = tableViewUsers.getItems();
            items.clear();
            items.addAll(usersNames);
            totalUsers.set(usersNames.size());
        });
    }
    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate, this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }
    @Override public void close() {
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
        tableViewUsers.getItems().clear();
        totalUsers.set(0);
    }
}
