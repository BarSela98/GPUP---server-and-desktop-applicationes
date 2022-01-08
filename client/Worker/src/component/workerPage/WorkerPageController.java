package component.workerPage;

import component.api.WorkerCommands;
import component.chat.ChatAreaRefresher;
import component.chat.model.ChatLinesWithVersion;
import component.mainApp.WorkerAppMainController;
import component.usersList.UsersListController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.util.Timer;
import java.util.stream.Collectors;

import static util.Constants.CHAT_LINE_FORMATTING;
import static util.Constants.REFRESH_RATE;


public class WorkerPageController implements WorkerCommands , Closeable{
    ///bar
    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private Timer timer;
    private ChatAreaRefresher chatAreaRefresher;
    private WorkerAppMainController workerAppMainController;

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
    }
    public void setActive() {
        usersListComponentController.startListRefresher();
        startListRefresher();
    }
    public void setInActive() {
        try {
            usersListComponentController.close();
            close();
        } catch (Exception ignored) {}
    }
    public void setAppMainController(WorkerAppMainController workerAppMainController) {
        this.workerAppMainController = workerAppMainController;
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
    public String getNameOfWorker() {
        return nameOfWorker.getText();
    }
    public void setNameOfWorker(String nameOfWorker) {
        this.nameOfWorker.setText(nameOfWorker);
    }
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    @FXML void doMission(ActionEvent event) {}
    @FXML void pauseMission(ActionEvent event) {}
    @FXML void quitButton(ActionEvent event) {
        logout();
    }
    @FXML void resumeMisson(ActionEvent event) {
    }
    @FXML void stopMission(ActionEvent event) {
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
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
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


    @FXML private UsersListController usersListComponentController;
    @FXML private BorderPane usersListComponent;
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
    // chat
    @FXML private ToggleButton autoScrollButton;
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    @FXML private Label chatVersionLabel;





}
