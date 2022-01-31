package component.workerPage;

import ODT.MissionTableWorker;
import engine.Mission;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utility.Constants.GSON_INSTANCE;
import static utility.Constants.MISSION_LIST;


public class MissionListRefresherForWorker extends TimerTask {
    private final Consumer<List<MissionTableWorker>> MissionsListConsumer;
    private final BooleanProperty shouldUpdate;


    public MissionListRefresherForWorker(BooleanProperty shouldUpdate, Consumer<List<MissionTableWorker>> MissionsListConsumer) {
         this.shouldUpdate = shouldUpdate;
        this.MissionsListConsumer = MissionsListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(MISSION_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Mission List Refresher For Worker " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("Mission List Refresher For Worker - Response code: "+response.code()+"\nResponse body: "+responseBody));
                } else {
                    String jsonArrayOfMissionsNames = response.body().string();

                    if (jsonArrayOfMissionsNames.length() != 3) {
                        Mission[] MissionsNames = GSON_INSTANCE.fromJson(jsonArrayOfMissionsNames, Mission[].class);

                        List<MissionTableWorker> table = new ArrayList<>();
                        for(int i= 0 ; i < MissionsNames.length ; ++i)
                            table.add(new MissionTableWorker(MissionsNames[i]));
                        MissionsListConsumer.accept(table);
                    }
                }
            }
        });
    }
}
