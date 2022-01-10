package component.login;

import component.mainApp.AdminAppMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;

import static utility.Constants.LOGIN_PAGE;


public class loginController {
    @FXML public TextField userNameTextField;
    @FXML private Button loginButton;
    @FXML private Button quitButton;
    @FXML public Label errorMessageLabel;
    private AdminAppMainController adminAppMainController;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }
    public void setAppMainController(AdminAppMainController adminAppMainController) {
        this.adminAppMainController = adminAppMainController;
    }
    @FXML void loginButtonAction(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("role", "Admin")
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
/*
OkHttpClient client = new OkHttpClient().newBuilder()
  .build();
Request request = new Request.Builder()
  .url("http://localhost:8080/GPUP/file/xml/load?=&name=bar")
  .method("GET", null)
  .build();
Response response = client.newCall(request).execute();
 */







            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                       adminAppMainController.updateUserName(userName);
                       adminAppMainController.switchToAdminPage();
                    });
                }
            }
        });
    }

    @FXML void quitButtonAction(ActionEvent event) {
        Platform.exit();
    }
    @FXML private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }
}
