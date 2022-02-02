package main;

import com.google.gson.Gson;
import component.mainApp.WorkerAppMainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static util.Constants.MAIN_PAGE_FXML_RESOURCE_LOCATION;
import static utility.Constants.BASE_DOMAIN;
import static utility.Constants.LOGOUT_PAGE;

public class gpupWorker extends Application {
    public final static Gson gson = new Gson();
    private WorkerAppMainController controller;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("G.P.U.P - Worker");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root,500,300);
        primaryStage.setScene(scene);
        controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        logout();
        HttpClientUtil.shutdown();
    }
    public static void main(String[] args) {
        launch(args);
    }

    public void logout() {
        String finalUrl = HttpUrl
                .parse(LOGOUT_PAGE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("-- error - worker logout"+e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("-- worker logout  - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println(responseBody);
                        controller.setInActive();
                        HttpClientUtil.removeCookiesOf(BASE_DOMAIN);
                    });
                }
            }
        });
    }
}