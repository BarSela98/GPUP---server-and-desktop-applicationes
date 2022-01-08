package component.page;

import component.api.AdminCommands;
import component.chat.ChatAreaRefresher;
import component.chat.model.ChatLinesWithVersion;
import component.graph.main.MainGraphController;
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
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.stream.Collectors;

import static util.Constants.*;


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
        if (usersListComponentController != null && graphAdminComponentController != null) {
            usersListComponentController.setMainController(this);
            graphAdminComponentController.setMainController(this);
        }
        autoScroll.bind(autoScrollButton.selectedProperty());
        chatVersionLabel.textProperty().bind(Bindings.concat("Chat Version: ", chatVersion.asString()));
    }
    public void setActive() {
        usersListComponentController.startListRefresher();
       // mainGraphController.startGraphListRefresher();
        graphAdminComponentController.startGraphListRefresher();
        starChatRefresher();

      //  chatAreaAdminComponentController.startListRefresher();
    }
    public void setInActive() {
        try {
            close();
            usersListComponentController.close();
            graphAdminComponentController.close();
        } catch (Exception ignored) {}
    }
    public void setAppMainController(AdminAppMainController adminAppMainController) {
        this.adminAppMainController = adminAppMainController;
    }

    @FXML void quitButton(ActionEvent event) {
        logout();
    }
    @FXML void loadButton(ActionEvent event) throws Exception {

        File file = new FileChooser().showOpenDialog(new Stage());

        if (file != null) {
            String finalUrl = HttpUrl
                    .parse(LOAD_XML_FILE)
                    .newBuilder()
                    .build()
                    .toString();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getPath())
                    .build();

            HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    Platform.runLater(() ->
                            //errorMessageProperty.set("Something went wrong: " + e.getMessage())
                            System.out.println("error")
                    );


                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();

                        Platform.runLater(() ->
                                ///errorMessageProperty.set("Something went wrong: " + responseBody)
                                System.out.println(responseBody)
                        );


                    } else {
                        Platform.runLater(() -> {
                            System.out.println("good");
                        });
                    }
                }
            });
        }
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
    public void starChatRefresher() {
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
