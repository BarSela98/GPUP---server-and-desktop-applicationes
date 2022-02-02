package component.workerPage;

import ODT.MissionInTable;
import ODT.MissionTableWorker;
import ODT.TargetInWorkerAndAmountOfThread;
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
import static utility.Constants.*;


public class WorkerPageController implements WorkerCommands , Closeable{

    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private Timer timerChatAreaRefresher;
    private Timer timerCompleteTarget;
    private Timer timerMissionRefresher;
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
                MissionTableWorker m = getSelectedMission();
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
        TaskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        WorkerCol.setCellValueFactory(new PropertyValueFactory<>("signWorkerSize"));
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("priceOfAllMission"));
        ProgressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        ////////////////////////////////////////////////////////////////////////////////////////
        nameOfMissionCol_target.setCellValueFactory(new PropertyValueFactory<>("Mission"));
        nameOfTaskCol_target.setCellValueFactory(new PropertyValueFactory<>("nameOfTask"));
        nameOfTargetCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        yourStatusCol.setCellValueFactory(new PropertyValueFactory<>("statusOfWorkerInMission"));
        yourDoneCol.setCellValueFactory(new PropertyValueFactory<>("targetComplete"));
        creditsPerTarget.setCellValueFactory(new PropertyValueFactory<>("priceOfMission"));
        StatusCol.setCellValueFactory(new PropertyValueFactory<>("statusOfMission"));
        nameOfCreatorCol.setCellValueFactory(new PropertyValueFactory<>("nameOfCreator"));
        rootCol.setCellValueFactory(new PropertyValueFactory<>("amountOfRoot"));
        middleCol.setCellValueFactory(new PropertyValueFactory<>("amountOfMiddle"));
        leafCol.setCellValueFactory(new PropertyValueFactory<>("amountOfLeaf"));
        independentsCol.setCellValueFactory(new PropertyValueFactory<>("amountOfIndependents"));
        targetStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        targetCreditsCol.setCellValueFactory(new PropertyValueFactory<>("price"));
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
    private MissionTableWorker getSelectedMission(){
        for(MissionTableWorker m : missionTable.getItems()){
            if (m.getCheckBox().isSelected()) {
                return m;
            }
        }
        return null;
    }

/// Refresher
    public void setActive() {
        autoUpdate.setValue(true);
        usersListComponentController.startListRefresher();
        startChatAreaRefresher();
        starMissionRefresher();
        starCompleteTargetRefresher();
    }
    public void setInActive() {
        try {
            autoUpdate.setValue(false);
            usersListComponentController.close();
            close();
        } catch (Exception ignored) {}
    }

    public void starCompleteTargetRefresher() {
        completeTargetListRefresher = new CompleteTargetListRefresher(autoUpdate, this::updateCompleteTargetLines);
        timerCompleteTarget = new Timer();
        timerCompleteTarget.schedule(completeTargetListRefresher, REFRESH_RATE, REFRESH_RATE);
    }
    private synchronized void updateCompleteTargetLines(TargetInWorkerAndAmountOfThread targetInWorkerAndAmountOfThread) {
        Platform.runLater(() -> {
            List<Target> targets = targetInWorkerAndAmountOfThread.getTargetInWorker();
            int credit = 0;
            ObservableList<Target> items = executeTargetTable.getItems();
            items.clear();
            for (Target t: targets){
                if (t.getStatus() == Target.Status.Success || t.getStatus() == Target.Status.Warning || t.getStatus() == Target.Status.Failure){
                    credit += t.getPrice();
                }
                else
                    t.setPrice(0);
            }
            yourCredit.setText(String.valueOf(credit));
            items.addAll(targets);
            availableThreadText.setText(String.valueOf(targetInWorkerAndAmountOfThread.getAvailableThread()));
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
    public void startChatAreaRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(chatVersion, autoUpdate, this::updateChatLines);
        timerChatAreaRefresher = new Timer();
        timerChatAreaRefresher.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void starMissionRefresher() {
        missionRefresher = new MissionListRefresherForWorker(autoUpdate, this::updateMissionLines);
        timerMissionRefresher = new Timer();
        timerMissionRefresher.schedule(missionRefresher, REFRESH_RATE, REFRESH_RATE);
        ///add close
    }
    private synchronized void updateMissionLines(List<MissionTableWorker> missions) {
        Platform.runLater(() -> {
            int size = 0;
            ObservableList<Target> targetComplete = executeTargetTable.getItems();
            unselectedCheckBoxes.clear();
            selectedCheckBoxes.clear();
            ObservableList<MissionTableWorker> items = missionTable.getItems();
            for (MissionTableWorker mission : missions) { /// update check box
                size = 0;
                configureCheckBox(mission.getCheckBox());
                for (Target t: targetComplete){
                    if ((t.getStatus() == Target.Status.Success || t.getStatus() == Target.Status.Warning || t.getStatus() == Target.Status.Failure) && t.getMission().equals(mission.getNameOfMission())){
                        ++size;
                    }
                 //   t.setNameOfTask(mission.getTask());
                }
                mission.setTargetComplete(size);
            }

            for(int i = 0 ; i < items.size() ; ++i) { /// update check box
                for (int j = 0 ; j < missions.size() ; ++j)
                    if(missions.get(j).getNameOfMission().equals(items.get(i).getNameOfMission()))
                        missions.get(j).changeInformation(items.get(i));
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
        String finalUrl = HttpUrl
                .parse(LOGOUT_PAGE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("error - worker logout"+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("worker logout  - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println(responseBody);
                        workerAppMainController.switchToLogin();
                        setInActive();
                        HttpClientUtil.removeCookiesOf(BASE_DOMAIN);
                    });
                }
            }
        });
    }


    @Override public void close() throws IOException {
        if (chatAreaRefresher != null && timerChatAreaRefresher != null) {
            chatAreaRefresher.cancel();
            timerChatAreaRefresher.cancel();
        }
        if (missionRefresher != null && timerMissionRefresher != null) {
            missionRefresher.cancel();
            timerMissionRefresher.cancel();
        }
        if (completeTargetListRefresher != null && timerCompleteTarget != null) {
            completeTargetListRefresher.cancel();
            timerCompleteTarget.cancel();
        }
        executeTargetTable.getItems().clear();
        missionTable.getItems().clear();
        chatVersion.set(0);
        chatLineTextArea.clear();
    }


    public synchronized void changeStatusOfWorkerInMission(String status)   {
        MissionInTable m = getSelectedMission();
        if (m == null)
            return;
        final String[] flag = {m.getNameOfMission()};

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
                flag[0] = null;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("changeStatusOfWorkerInMission - worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                    flag[0] = null;
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println(responseBody));
                }
            }
        });
        changeWorkerStatusInTable(flag[0], status);
        System.out.println(status);
    }

    public void changeWorkerStatusInTable(String name, String status){
        if (name != null)
            for (MissionTableWorker m :missionTable.getItems()){
                if(m.getNameOfMission().equals(name)){
                    switch (status) {
                        case "sign":
                            m.setStatusOfWorkerInMission(MissionTableWorker.StatusOfWorkerInMission.SIGNUP);
                            break;
                        case "remove":
                            m.setStatusOfWorkerInMission(MissionTableWorker.StatusOfWorkerInMission.UNSIGNED);
                            break;
                        case "DO":
                            m.setStatusOfWorkerInMission(MissionTableWorker.StatusOfWorkerInMission.DO);
                            break;
                        case "PAUSE":
                            m.setStatusOfWorkerInMission(MissionTableWorker.StatusOfWorkerInMission.PAUSE);
                            break;
                    }
                }
            }

    }


    public String getNameOfWorker() {
        return nameOfWorker.getText();
    }
    public void setNameOfWorker(String nameOfWorker) {
        this.nameOfWorker.setText(nameOfWorker);
    }

    public void signForMission(){
        MissionTableWorker m = getSelectedMission();
        if (m==null)
            return;
        final String[] flag = {m.getNameOfMission()};

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
        changeWorkerStatusInTable(flag[0], "sign");
    }
    public void removeFormMission(){
        MissionTableWorker m = getSelectedMission();
        if (m==null)
            return;
        final String[] flag = {m.getNameOfMission()};
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
        changeWorkerStatusInTable(flag[0], "remove");
    }

    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    public void setAmountOfResources(String amountOfResources) {
        this.amountOfResources.setText(amountOfResources);
    }

    public void setYourCredit(String yourCredit) {
        this.yourCredit.setText(yourCredit);
    }




//// fxml member
    @FXML private UsersListController usersListComponentController;
    @FXML private BorderPane usersListComponent;
    @FXML private TableView<?> tableViewUser;
    @FXML private Text nameOfWorker;
    @FXML private Text amountOfResources;
    @FXML private Text yourCredit;
//// table 1
    @FXML private TableView<MissionTableWorker> missionTable;
    @FXML private TableColumn<MissionTableWorker, CheckBox> checkBoxTableMission;
    @FXML private TableColumn<MissionTableWorker, String> nameOfMissionCol;
    @FXML private TableColumn<MissionTableWorker, String> TaskCol;
    @FXML private TableColumn<MissionTableWorker, Integer> WorkerCol;
    @FXML private TableColumn<MissionTableWorker, String> ProgressCol;
    @FXML private TableColumn<MissionTableWorker, Integer> yourDoneCol;
    @FXML private TableColumn<MissionTableWorker, Integer> creditsCol;
    @FXML private TableColumn<MissionTableWorker, String> yourStatusCol;

    @FXML private TableColumn<MissionTableWorker, Integer>  creditsPerTarget;
    @FXML private TableColumn<MissionTableWorker, String>  StatusCol;
    @FXML private TableColumn<MissionTableWorker, String> nameOfCreatorCol;
    @FXML private TableColumn<MissionTableWorker, String> rootCol;
    @FXML private TableColumn<MissionTableWorker, String> middleCol;
    @FXML private TableColumn<MissionTableWorker, String> leafCol;
    @FXML private TableColumn<MissionTableWorker, String> independentsCol;

//// table 2
    @FXML private TableView<Target> executeTargetTable;
    @FXML private TableColumn<Target, String> nameOfMissionCol_target;
    @FXML private TableColumn<Target, String> nameOfTaskCol_target;
    @FXML private TableColumn<Target, String> nameOfTargetCol;
    @FXML private TableColumn<Target, String> targetStatusCol;
    @FXML private TableColumn<Target, String> targetCreditsCol;
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
    @FXML private Text availableThreadText;

}
