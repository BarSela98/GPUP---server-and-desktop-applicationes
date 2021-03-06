package component.login;

import component.mainApp.WorkerAppMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;


public class loginController {
    @FXML public TextField userNameTextField;
    @FXML private Button loginButton;
    @FXML private Button quitButton;
    @FXML public Label errorMessageLabel;
    @FXML public Spinner<Integer> amountOfThreadSpinner;
    private WorkerAppMainController workerAppMainController;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        amountOfThreadSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1));
    }
    public void setAppMainController(WorkerAppMainController workerAppMainController) {
        this.workerAppMainController = workerAppMainController;
    }
    @FXML void loginButtonAction(ActionEvent event) {

        String userName = userNameTextField.getText();
        String[] asd={"asd","rthtr","dfgr"};
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("role", "Worker")
                .addQueryParameter("threadSize", String.valueOf(amountOfThreadSpinner.getValue()))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

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
                        workerAppMainController.updateUser(userName,amountOfThreadSpinner.getValue());
                        workerAppMainController.switchToWorkerPage();
                        try {
                            String responseBody = response.body().string();
                            System.out.println(responseBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
