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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ObservableList<MissionInTable> missionItem = FXCollections.observableArrayList();


    private AdminAppMainController adminAppMainController;
    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private Timer timer;
    private ChatAreaRefresher chatAreaRefresher;

    private final BooleanProperty autoUpdateMission;

    private MissionListRefresher missionRefresher;


    public AdminPageController() {
        chatVersion = new SimpleIntegerProperty();
        autoScroll = new SimpleBooleanProperty();
        autoUpdate = new SimpleBooleanProperty(true);
        autoUpdateMission = new SimpleBooleanProperty(true);
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
    }
    public void setActive() {
        usersListComponentController.startListRefresher();
        graphAdminComponentController.startGraphListRefresher();
        starChatRefresher();
        starMissionRefresher();
    }
    public void setInActive() {
        try {
            close();
            usersListComponentController.close();
            graphAdminComponentController.close();
            close();
        } catch (Exception ignored) {}
    }
    public void setAppMainController(AdminAppMainController adminAppMainController) {
        this.adminAppMainController = adminAppMainController;
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

    public synchronized void changeStatusOfMission(String status){
        String name = "";

        for (MissionInTable m :tableViewMission.getItems()){
            if (m.getCheckBox().isSelected())
                name = m.getNameOfMission();
        }

        String finalUrl = HttpUrl
                .parse(CHANGE_STATUS_OF_MISSION)
                .newBuilder()
                .addQueryParameter("missionname", tableViewMission.getItems().get(0).getNameOfMission())
                .addQueryParameter("missionstatus", status)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> new errorMain(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {////
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain(new Exception("Response code: "+response.code()+"\nResponse body: "+responseBody)));
                }
                else{
                    Platform.runLater(() -> System.out.println("changeStatusOfMission "));
                }
            }
        });
    }


    @Override public void logout() {
        adminAppMainController.switchToLogin();
    }

    /// chat
    public void setNameOfAdmin(String userName) {
        nameOfAdmin.setText(userName);
    }
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }
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
                Platform.runLater(() -> new errorMain(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain(new Exception("Response code: "+response.code()+"\nResponse body: "+responseBody)));
                }
            }
        });

        chatLineTextArea.clear();
    }
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
        timer = new Timer();
        timer.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
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
        //workersCol.setCellValueFactory(new PropertyValueFactory<>("isRunning"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }
    public void starMissionRefresher() {
        missionRefresher = new MissionListRefresher(autoUpdate, this::updateMissionLines);
        timer = new Timer();
        timer.schedule(missionRefresher, 5000, 5000);
    }
    private synchronized void updateMissionLines(List<MissionInTable> missions) {
        Platform.runLater(() -> {
            ObservableList<MissionInTable> items = tableViewMission.getItems();
            for(int i = 0 ; i < items.size() ; ++i) /// update check box
                missions.get(i).changeInformation(items.get(i));

            items.clear();
            items.addAll(missions);
        });
    }
    /**
     * close Mission Refresher
     * @throws IOException
     */
    @Override public void close() throws IOException {
        if ( timer != null) {
            missionRefresher.cancel();
            timer.cancel();
        }
    }

}
