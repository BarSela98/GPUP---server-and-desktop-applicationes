package component.page;

import ODT.Mission;
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

public class MissionListRefresher extends TimerTask {
    private final Consumer<List<Mission>> MissionsListConsumer;
    private final BooleanProperty shouldUpdate;


    public MissionListRefresher(BooleanProperty shouldUpdate, Consumer<List<Mission>> MissionsListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.MissionsListConsumer = MissionsListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(Constants.MISSION_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfMissionsNames = response.body().string();
                Mission[] MissionsNames = GSON_INSTANCE.fromJson(jsonArrayOfMissionsNames, Mission[].class);
                MissionsListConsumer.accept(Arrays.asList(MissionsNames));
            }
        });
    }
}
