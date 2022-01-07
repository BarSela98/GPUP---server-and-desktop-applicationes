package component.page;

import component.api.AdminCommands;
import component.chat.ChatAreaRefresher;
import component.chat.model.ChatLinesWithVersion;
import component.graphAdmin.missionAdminController;
import component.mainApp.AdminAppMainController;
import component.usersList.UsersListController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import java.net.URL;
import java.util.Timer;
import java.util.stream.Collectors;

import static util.Constants.*;


public class AdminPageController implements AdminCommands, Closeable {
    @FXML private UsersListController usersListComponentController;
    @FXML private BorderPane usersListComponent;
    @FXML private Text nameOfAdmin;
    @FXML private TabPane tabPanAdmin;
    @FXML private ToggleButton autoScrollButton;
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    @FXML private Label chatVersionLabel;
    private AdminAppMainController adminAppMainController;
    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private Timer timer;
    private ChatAreaRefresher chatAreaRefresher;

    public AdminPageController() {
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
      //  chatAreaAdminComponentController.startListRefresher();
    }
    public void setInActive() {
        try {
            close();
            usersListComponentController.close();
        } catch (Exception ignored) {}
    }
    public void setAppMainController(AdminAppMainController adminAppMainController) {
        this.adminAppMainController = adminAppMainController;
    }

    @FXML void quitButton(ActionEvent event) {
        logout();
    }
    @FXML void loadButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(MISSION_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        missionAdminController controller = fxmlLoader.getController();
        Tab tab1 = new Tab("Planes", root);
        tabPanAdmin.getTabs().add(tab1);
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
    @Override public void close() throws IOException {
        chatVersion.set(0);
        chatLineTextArea.clear();
        if (chatAreaRefresher != null && timer != null) {
            chatAreaRefresher.cancel();
            timer.cancel();
        }
    }

}
