package component.page;

import ODT.MissionInTable;
import component.api.AdminCommands;
import component.chat.ChatAreaRefresher;
import component.chat.model.ChatLinesWithVersion;
import component.graph.main.MainGraphController;
import component.mainApp.AdminAppMainController;
import component.usersList.UsersListController;
import error.errorMain;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

import static utility.Constants.*;



public class AdminPageController implements AdminCommands, Closeable {
    private ObservableList<MissionInTable> missionItem = FXCollections.observableArrayList();
    private AdminAppMainController adminAppMainController;
    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private Timer timerMissionRefresher;
    private Timer timerChatRefresher;
    private ChatAreaRefresher chatAreaRefresher;
    private final BooleanProperty autoUpdateMission;
    private MissionListRefresher missionRefresher;
    private final int maxNumSelected =  1;
    private ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private ObservableSet<CheckBox> unselectedCheckBoxes = FXCollections.observableSet();
    private IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);
    private SimpleBooleanProperty showButton;

    public AdminPageController() {
        chatVersion = new SimpleIntegerProperty();
        autoScroll = new SimpleBooleanProperty();
        autoUpdate = new SimpleBooleanProperty(true);
        autoUpdateMission = new SimpleBooleanProperty(true);
        showButton = new SimpleBooleanProperty(true);
    }
    @FXML public void initialize() {
        if (usersListComponentController != null && graphAdminComponentController != null) {
            usersListComponentController.setMainController(this);
            graphAdminComponentController.setMainController(this);
        }
        autoScroll.bind(autoScrollButton.selectedProperty());
        chatVersionLabel.textProperty().bind(Bindings.concat("Chat Version: ", chatVersion.asString()));
        setTableCol();
        tableViewMission.setItems(missionItem);

        numCheckBoxesSelected.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            if (newSelectedCount.intValue() >= maxNumSelected){
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(true));
                showButton.set(false);
            }
            else{
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(false));
                showButton.set(true);
            }
        });
        stop.disableProperty().bind(showButton);
        play.disableProperty().bind(showButton);
        resume.disableProperty().bind(showButton);
        pause.disableProperty().bind(showButton);
    }
    public void setAppMainController(AdminAppMainController adminAppMainController) {
        this.adminAppMainController = adminAppMainController;
    }
    public synchronized void changeStatusOfMission(String status){
        String finalUrl = HttpUrl
                .parse(CHANGE_STATUS_OF_MISSION)
                .newBuilder()
                .addQueryParameter("missionname", getSelectedMission().getNameOfMission())
                .addQueryParameter("missionstatus", status)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("error in changeStatusOfMission in worker "+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {////
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain(new Exception("Response code: "+response.code()+"\nResponse body: "+responseBody)));
                }
                else{
                    String responseBody = response.body().string();
                    //Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
    }
    @FXML void quitButton(ActionEvent event) {
        logout();
    }
    @FXML void playButton(ActionEvent event) {
        changeStatusOfMission("run");
    }
    @FXML void stopButton(ActionEvent event) {
        changeStatusOfMission("stop");
    }
    @FXML void pauseButton(ActionEvent event) {
        changeStatusOfMission("pause");
    }
    @FXML void resumeButton(ActionEvent event) {
        changeStatusOfMission("resume");
    }
    @Override public void logout() {
        String finalUrl = HttpUrl
                .parse(LOGOUT_PAGE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("error - admin logout"+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("admin logout  - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println(responseBody);
                        adminAppMainController.switchToLogin();
                        setInActive();
                        HttpClientUtil.removeCookiesOf(BASE_DOMAIN);
                    });
                }
            }
        });
    }
    public void setNameOfAdmin(String userName) {
        nameOfAdmin.setText(userName);
    }

/// table
    private void configureCheckBox(CheckBox checkBox) {
    if (checkBox.isSelected())
        selectedCheckBoxes.add(checkBox);
     else
        unselectedCheckBoxes.add(checkBox);
    checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
        if (isNowSelected) {
            unselectedCheckBoxes.remove(checkBox);
            selectedCheckBoxes.add(checkBox);
        } else {
            selectedCheckBoxes.remove(checkBox);
            unselectedCheckBoxes.add(checkBox);
        }

    });
}
    private MissionInTable getSelectedMission(){
        for(MissionInTable m : tableViewMission.getItems()){
            if (m.getCheckBox().isSelected()) {
                return m;
            }
        }
        return null;
    }
    public void setTableCol(){
        nameOfMissionCol.setCellValueFactory(new PropertyValueFactory<>("nameOfMission"));
        nameOfCreatorCol.setCellValueFactory(new PropertyValueFactory<>("nameOfCreator"));
        rootCol.setCellValueFactory(new PropertyValueFactory<>("amountOfRoot"));
        middleCol.setCellValueFactory(new PropertyValueFactory<>("amountOfMiddle"));
        leafCol.setCellValueFactory(new PropertyValueFactory<>("amountOfLeaf"));
        independentsCol.setCellValueFactory(new PropertyValueFactory<>("amountOfIndependents"));
        priceOfAllMissionCol.setCellValueFactory(new PropertyValueFactory<>("priceOfAllMission"));
        workersCol.setCellValueFactory(new PropertyValueFactory<>("workerListSize"));
       // workersCol.setCellValueFactory(new PropertyValueFactory<>("isRunning"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        ProgressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        StatusOfMissionCol.setCellValueFactory(new PropertyValueFactory<>("statusOfMission"));
        nameOfGraphCol.setCellValueFactory(new PropertyValueFactory<>("nameOfGraph"));
    }

/// Chat
    @FXML void sendButtonClicked(ActionEvent event) {
        String chatLine = chatLineTextArea.getText();
        String finalUrl = HttpUrl
                .parse(SEND_CHAT_LINE)
                .newBuilder()
                .addQueryParameter("userstring", chatLine)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("error in sendButtonClicked in admin "+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("sendButtonClicked  - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
        chatLineTextArea.clear();
    }

///Refresher
    public void setActive() {
        usersListComponentController.startListRefresher();
        graphAdminComponentController.startGraphListRefresher();
        starChatRefresher();
        starMissionRefresher();
    }
    /**
     * close Refresher
     * @throws IOException
     */
    public void setInActive() {
        try {
            close();
            usersListComponentController.close();
            graphAdminComponentController.close();
        } catch (Exception ignored) {}
    }
    public void close() throws IOException {
        if (timerChatRefresher != null) {
            chatAreaRefresher.cancel();
            timerChatRefresher.cancel();
        }
        if (timerMissionRefresher != null) {
            missionRefresher.cancel();
            timerMissionRefresher.cancel();
        }
        chatVersion.set(0);
        chatLineTextArea.clear();
        tableViewMission.getItems().clear();
    }
/// Chat Refresher
    private void updateChatLines(ChatLinesWithVersion chatLinesWithVersion) {
    if (chatLinesWithVersion.getVersion() != chatVersion.get()) {
        String deltaChatLines = chatLinesWithVersion
                .getEntries()
                .stream()
                .map(singleChatLine -> {
                    long time = singleChatLine.getTime();
                    return String.format(CHAT_LINE_FORMATTING, time, time, time, singleChatLine.getUsername(), singleChatLine.getChatString());
                }).collect(Collectors.joining());

        Platform.runLater(() -> {
            chatVersion.set(chatLinesWithVersion.getVersion());

            if (autoScroll.get()) {
                mainChatLinesTextArea.appendText(deltaChatLines);
                mainChatLinesTextArea.selectPositionCaret(mainChatLinesTextArea.getLength());
                mainChatLinesTextArea.deselect();
            } else {
                int originalCaretPosition = mainChatLinesTextArea.getCaretPosition();
                mainChatLinesTextArea.appendText(deltaChatLines);
                mainChatLinesTextArea.positionCaret(originalCaretPosition);
            }
        });
    }
}
    public void starChatRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(chatVersion, autoUpdate, this::updateChatLines);
        timerChatRefresher = new Timer();
        timerChatRefresher.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
    }
/// Mission Refresher
    public void starMissionRefresher() {
    missionRefresher = new MissionListRefresher(autoUpdate, this::updateMissionLines);
    timerMissionRefresher = new Timer();
    timerMissionRefresher.schedule(missionRefresher, REFRESH_RATE, REFRESH_RATE);
}
    private synchronized void updateMissionLines(List<MissionInTable> missions) {
        Platform.runLater(() -> {
            unselectedCheckBoxes.clear();
            selectedCheckBoxes.clear();
            ObservableList<MissionInTable> items = tableViewMission.getItems();
            for (MissionInTable mission : missions) { /// update check box
                configureCheckBox(mission.getCheckBox());
            }

            for(int i = 0 ; i < items.size() ; ++i) { /// update check box
                missions.get(i).changeMissionInformationAdmin(items.get(i));
            }


            items.clear();
            items.addAll(missions);
        });
    }

/// fxml member
@FXML private UsersListController usersListComponentController;
    @FXML private BorderPane usersListComponent;
    @FXML private MainGraphController graphAdminComponentController;
    @FXML private BorderPane graphAdminComponent;
    @FXML private Text nameOfAdmin;
    @FXML private TabPane tabPanAdmin;
    @FXML private ToggleButton autoScrollButton;
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    @FXML private Label chatVersionLabel;
    @FXML private TableColumn<MissionInTable, String> nameOfMissionCol;
    @FXML private TableColumn<MissionInTable, String> nameOfCreatorCol;
    @FXML private TableColumn<MissionInTable, String> rootCol;
    @FXML private TableColumn<MissionInTable, String> middleCol;
    @FXML private TableColumn<MissionInTable, String> leafCol;
    @FXML private TableColumn<MissionInTable, String> independentsCol;
    @FXML private TableColumn<MissionInTable, Boolean> workersCol;   ////////////////////////////
    @FXML private TableColumn<MissionInTable, String> priceOfAllMissionCol;
    @FXML private TableColumn<MissionInTable, CheckBox> remarkCol;
    @FXML private TableView<MissionInTable> tableViewMission;
    @FXML private TableColumn<MissionInTable, String> ProgressCol;
    @FXML private TableColumn<MissionInTable, String> StatusOfMissionCol;
    @FXML private TableColumn<MissionInTable, String> nameOfGraphCol;
    @FXML private Button stop;
    @FXML private Button play;
    @FXML private Button resume;
    @FXML private Button pause;
}
