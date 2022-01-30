package component.graph.general;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utility.Constants.GRAPH_LIST;
import static utility.Constants.GSON_INSTANCE;

public class GraphListRefresher extends TimerTask {
    private final Consumer<List<String>> graphListConsumer;
    private final BooleanProperty shouldUpdate;


    public GraphListRefresher(BooleanProperty shouldUpdate, Consumer<List<String>> graphListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.graphListConsumer = graphListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(GRAPH_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Graph List Refresher" + e.getMessage()));
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> System.out.println("GraphListRefresher - Response code: "+response.code()+"\nResponse body: "+responseBody));
                }
                else{
                        String jsonArrayOfGraphsNames = response.body().string();
                        String[] graphsNames = GSON_INSTANCE.fromJson(jsonArrayOfGraphsNames, String[].class);
                        graphListConsumer.accept(Arrays.asList(graphsNames));
                }
            }
        });
    }
}
