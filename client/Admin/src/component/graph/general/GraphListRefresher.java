package component.graph.general;

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

        HttpClientUtil.runAsync(Constants.GRAPH_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("----------------------------------------------------------------------------------------");
                String jsonArrayOfUsersNames = response.body().string();
                String[] graphsNames = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, String[].class);
                graphListConsumer.accept(Arrays.asList(graphsNames));
            }
        });
    }
}
