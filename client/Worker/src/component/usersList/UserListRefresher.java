package component.usersList;

import ODT.User;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.Constants.GSON_INSTANCE;

public class UserListRefresher extends TimerTask {
    private final Consumer<List<User>> usersListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;


    public UserListRefresher(BooleanProperty shouldUpdate, Consumer<List<User>> usersListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.usersListConsumer = usersListConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        HttpClientUtil.runAsync(Constants.USERS_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("User List Refresher Worker " + e.getMessage()));
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("User List Refresher Worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                }
                else{
                    String jsonArrayOfUsersNames = response.body().string();
                    User[] usersNames = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, User[].class);
                    usersListConsumer.accept(Arrays.asList(usersNames));
                }
            }
        });
    }
}
