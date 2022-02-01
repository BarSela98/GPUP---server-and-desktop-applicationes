package component.workerPage;

import ODT.TargetInWorkerAndAmountOfThread;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utility.Constants.GSON_INSTANCE;
import static utility.Constants.TARGET_COMPLETE_LIST;


public class CompleteTargetListRefresher extends TimerTask {
    private final Consumer<TargetInWorkerAndAmountOfThread> completeTargetsListConsumer;
    private final BooleanProperty shouldUpdate;


    public CompleteTargetListRefresher(BooleanProperty shouldUpdate, Consumer<TargetInWorkerAndAmountOfThread> completeTargetsListConsumer) {
         this.shouldUpdate = shouldUpdate;
        this.completeTargetsListConsumer = completeTargetsListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(TARGET_COMPLETE_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    System.out.println("Mission list - Response code: "+response.code()+"\nResponse body: "+responseBody);
                } else {
                    String jsonArrayOfMissionsNames = response.body().string();
                    if (jsonArrayOfMissionsNames.length() != 3) {
                        TargetInWorkerAndAmountOfThread targetInWorkerAndAmountOfThread = GSON_INSTANCE.fromJson(jsonArrayOfMissionsNames, TargetInWorkerAndAmountOfThread.class);


                      //  Target[] completeTargets = GSON_INSTANCE.fromJson(jsonArrayOfMissionsNames, Target[].class);

                       // List<Target> l = Arrays.asList(completeTargets);
                        completeTargetsListConsumer.accept(targetInWorkerAndAmountOfThread);
                    }
                }
            }
        });
    }
}
