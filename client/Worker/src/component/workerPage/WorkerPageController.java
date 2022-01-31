package component.workerPage;

import ODT.MissionInTable;
import component.api.WorkerCommands;
import component.chat.ChatAreaRefresher;
import component.chat.model.ChatLinesWithVersion;
import component.mainApp.WorkerAppMainController;
import component.usersList.UsersListController;
import engine.Target;
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
import util.Constants;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

import static util.Constants.CHAT_LINE_FORMATTING;
import static util.Constants.REFRESH_RATE;
import static utility.Constants.CHANGE_WORKER_STATUS_IN_MISSION;


public class WorkerPageController implements WorkerCommands , Closeable{

    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private Timer timer;
    private Timer timerCompleteTarget;
    private ChatAreaRefresher chatAreaRefresher;
    private CompleteTargetListRefresher completeTargetListRefresher;
    private WorkerAppMainController workerAppMainController;
    private MissionListRefresherForWorker missionRefresher;
    private final int maxNumSelected =  1;
    private ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private ObservableSet<CheckBox> unselectedCheckBoxes = FXCollections.observableSet();
    private IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);


    public WorkerPageController(){
    chatVersion = new SimpleIntegerProperty();
    autoScroll = new SimpleBooleanProperty();
    autoUpdate = new SimpleBooleanProperty(true);
    }
    @FXML public void initialize() {
        if (usersListComponentController != null) {
            usersListComponentController.setMainController(this);
        }
        autoScroll.bind(autoScrollButton.selectedProperty());
        chatVersionLabel.textProperty().bind(Bindings.concat("Chat Version: ", chatVersion.asString()));
        setTableCol();
        doButton.disableProperty().bind(signUpButton.disableProperty().not());
        stopButton.disableProperty().bind(signUpButton.disableProperty().not());
        pauseButton.disableProperty().bind(signUpButton.disableProperty().not());
        resumeButton.disableProperty().bind(signUpButton.disableProperty().not());

        numCheckBoxesSelected.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            if (newSelectedCount.intValue() >= maxNumSelected){
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(true));
                MissionInTable m = getSelectedMission();
                if(m!=null)
                    signUpButton.setDisable(m.getSign());
            }
            else{
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(false));
                signUpButton.setDisable(false);
            }
        });
    }
    public void setAppMainController(WorkerAppMainController workerAppMainController) {
        this.workerAppMainController = workerAppMainController;
    }
/// table
    public void setTableCol(){
        nameOfMissionCol.setCellValueFactory(new PropertyValueFactory<>("nameOfMission"));
        checkBoxTableMission.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        //TaskCol.setCellValueFactory(new PropertyValueFactory<>("nameOfCreator"));
        WorkerCol.setCellValueFactory(new PropertyValueFactory<>("workerListSize"));
        craditsCol.setCellValueFactory(new PropertyValueFactory<>("priceOfAllMission"));
        ////////////////////////////////////////////////////////////////////////////////////////
        nameOfMissionCol_target.setCellValueFactory(new PropertyValueFactory<>("Mission"));
       // nameOfTaskCol_target.setCellValueFactory(new PropertyValueFactory<>("priceOfAllMission"));
        nameOfTargetCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        yourStatusCol.setCellValueFactory(new PropertyValueFactory<>("name"));
       // targetStatusCol.setCellValueFactory(new PropertyValueFactory<>("priceOfAllMission"));
       // targetCraditsCol.setCellValueFactory(new PropertyValueFactory<>("priceOfAllMission"));
    }
    private void configureCheckBox(CheckBox checkBox) {

        if (checkBox.isSelected()) {
            selectedCheckBoxes.add(checkBox);
        } else {
            unselectedCheckBoxes.add(checkBox);
        }

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
        for(MissionInTable m : missionTable.getItems()){
            if (m.getCheckBox().isSelected()) {
                return m;
            }
        }
        return null;
    }

/// Refresher
    public void setActive() {
        usersListComponentController.startListRefresher();
        startListRefresher();
        starMissionRefresher();
        starCompleteTargetRefresher();
    }
    public void setInActive() {
        try {
            usersListComponentController.close();
            close();
        } catch (Exception ignored) {}
    }

    public void starCompleteTargetRefresher() {
        completeTargetListRefresher = new CompleteTargetListRefresher(autoUpdate, this::updateCompleteTargetLines);
        timerCompleteTarget = new Timer();
        timerCompleteTarget.schedule(completeTargetListRefresher, REFRESH_RATE, REFRESH_RATE);
        ///add close
    }
    private synchronized void updateCompleteTargetLines(List<Target> targets) {
        Platform.runLater(() -> {
            ObservableList<Target> items = executeTargetTable.getItems();
            items.clear();
            items.addAll(targets);
        });
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
    public void startListRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(chatVersion, autoUpdate, this::updateChatLines);
        timer = new Timer();
        timer.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void starMissionRefresher() {
        missionRefresher = new MissionListRefresherForWorker(autoUpdate, this::updateMissionLines);
        timer = new Timer();
        timer.schedule(missionRefresher, REFRESH_RATE, REFRESH_RATE);
        ///add close
    }
    private synchronized void updateMissionLines(List<MissionInTable> missions) {
        Platform.runLater(() -> {
            unselectedCheckBoxes.clear();
            selectedCheckBoxes.clear();
            ObservableList<MissionInTable> items = missionTable.getItems();
            for (MissionInTable mission : missions) { /// update check box
                configureCheckBox(mission.getCheckBox());
            }

            for(int i = 0 ; i < items.size() ; ++i) { /// update check box
                missions.get(i).changeInformation(items.get(i));
            }


            items.clear();
            items.addAll(missions);
        });
    }

/// button
    @FXML void signUpForMission(ActionEvent event) {
        if (numCheckBoxesSelected.get()==0)
            return;
        signUpButton.setDisable(true);
        signForMission();
    }
    @FXML void doMission(ActionEvent event) {
        changeStatusOfWorkerInMission("DO");
    }
    @FXML void pauseMission(ActionEvent event) {
        changeStatusOfWorkerInMission("PAUSE");
    }
    @FXML void quitButton(ActionEvent event) {
        logout();
    }
    @FXML void resumeMisson(ActionEvent event) {
        changeStatusOfWorkerInMission("DO");
    }
    @FXML void stopMission(ActionEvent event) {
        removeFormMission();
    }
    @FXML void sendButtonClicked(ActionEvent event) {
        String chatLine = chatLineTextArea.getText();
        String finalUrl = HttpUrl
                .parse(Constants.SEND_CHAT_LINE)
                .newBuilder()
                .addQueryParameter("userstring", chatLine)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("error in sendButtonClicked in worker "+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("sendButtonClicked - worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });

        chatLineTextArea.clear();
    }
    @Override public void logout() {
        workerAppMainController.switchToLogin();
    }
    @Override public void close() throws IOException {
        chatVersion.set(0);
        chatLineTextArea.clear();
        if (chatAreaRefresher != null && timer != null) {
            chatAreaRefresher.cancel();
            timer.cancel();
        }
    }


    public synchronized void changeStatusOfWorkerInMission(String status){
        MissionInTable m = getSelectedMission();
        if (m == null)
            return;


        String finalUrl = HttpUrl
                .parse(CHANGE_WORKER_STATUS_IN_MISSION)
                .newBuilder()
                .addQueryParameter("missionname",m.getNameOfMission())
                .addQueryParameter("workerstatus", status)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("changeStatusOfWorkerInMission - error -"+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("changeStatusOfWorkerInMission - worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
    }

    public String getNameOfWorker() {
        return nameOfWorker.getText();
    }
    public void setNameOfWorker(String nameOfWorker) {
        this.nameOfWorker.setText(nameOfWorker);
    }

    public void signForMission(){
        MissionInTable m = getSelectedMission();
        if (m==null)
            return;

        m.setSign(true);
        signUpButton.setDisable(true);
        String finalUrl = HttpUrl
                .parse(Constants.SIGN_FOR_MISSION)
                .newBuilder()
                .addQueryParameter("missionname", m.getNameOfMission())
                .addQueryParameter("sign", "sign")
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("signForMission - error -"+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("signForMission - worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
    }
    public void removeFormMission(){
        MissionInTable m = getSelectedMission();
        m.setSign(false);
        signUpButton.setDisable(false);
        String finalUrl = HttpUrl
                .parse(Constants.SIGN_FOR_MISSION)
                .newBuilder()
                .addQueryParameter("missionname", missionTable.getItems().get(0).getNameOfMission())
                .addQueryParameter("sign", "remove")
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("removeFormMission - error -"+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("removeFormMission - worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
    }

    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    public void setAmountOfResources(String amountOfResources) {
        this.amountOfResources.setText(amountOfResources);
    }

    public void setYourCradit(String yourCradit) {
        this.yourCradit.setText(yourCradit);
    }




//// fxml member
    @FXML private UsersListController usersListComponentController;
    @FXML private BorderPane usersListComponent;
    @FXML private TableView<?> tableViewUser;
    @FXML private Text nameOfWorker;
    @FXML private Text amountOfResources;
    @FXML private Text yourCradit;
//// table 1
    @FXML private TableView<MissionInTable> missionTable;
    @FXML private TableColumn<MissionInTable, CheckBox> checkBoxTableMission;
    @FXML private TableColumn<MissionInTable, String> nameOfMissionCol;
    @FXML private TableColumn<MissionInTable, String> TaskCol;
    @FXML private TableColumn<MissionInTable, Integer> WorkerCol;
    @FXML private TableColumn<MissionInTable, ?> ProgressCol;
    @FXML private TableColumn<MissionInTable, ?> yourDoneCol;
    @FXML private TableColumn<MissionInTable, Integer> craditsCol;
    @FXML private TableColumn<MissionInTable, String> yourStatusCol;
//// table 2
    @FXML private TableView<Target> executeTargetTable;
    @FXML private TableColumn<Target, String> nameOfMissionCol_target;
    @FXML private TableColumn<Target, String> nameOfTaskCol_target;
    @FXML private TableColumn<Target, String> nameOfTargetCol;
    @FXML private TableColumn<Target, String> targetStatusCol;
    @FXML private TableColumn<Target, String> targetCraditsCol;
    // chat
    @FXML private ToggleButton autoScrollButton;
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    @FXML private Label chatVersionLabel;
    @FXML private Button doButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private Button resumeButton;
    @FXML private Button signUpButton;

}
