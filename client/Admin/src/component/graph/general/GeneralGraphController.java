package component.graph.general;

import ODT.Graph;
import component.graph.main.MainGraphController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.GSON_INSTANCE;
import static util.Constants.REFRESH_RATE;

public class GeneralGraphController {
    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalGraph;
    private MainGraphController mainController;

    @FXML public void initialize(){
        choiceBoxGraph.valueProperty().addListener((a,b,c)->{
            if(c == null)
                return;

            if(b != null &&  b.equals(c))
                return;

            String selectedItem = choiceBoxGraph.getSelectionModel().getSelectedItem();
            String finalUrl = HttpUrl
                    .parse(Constants.GRAPH)
                    .newBuilder()
                    .addQueryParameter("grapname", selectedItem)
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            System.out.println(e)
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() ->
                                        System.out.println(responseBody)
                        );
                    } else {
                        String jsonArrayOfUsersNames = response.body().string();
                        Graph graph = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, Graph.class);
                        Platform.runLater(() -> {
                            updateGraphOnScene(graph);
                            mainController.setGraph(graph);
                            mainController.addDataToTable();
                            mainController.updateTable();
                            mainController.updatePath();
                            mainController.updateExecute();
                        });
                    }
                }
            });
        });
    }
    public void updateGraphOnScene(Graph graph) {
        nameOfGraphText.setText(graph.getGraphName());
        creatorText.setText(graph.getNameOfCreator());
        priceSimulationText.setText(String.valueOf(graph.getPriceForSimulation()));
        priceCompilationText.setText(String.valueOf(graph.getPriceForCompilation()));
    }

    public GeneralGraphController(){
         autoUpdate = new SimpleBooleanProperty(true);
         totalGraph = new SimpleIntegerProperty(0);
     }
    private void updateGraphList(List<String> graphNames) {
        Platform.runLater(() -> {
            if (choiceBoxGraph.getItems().size() < graphNames.size())
            {
                for (int i=choiceBoxGraph.getItems().size(); i < graphNames.size(); ++i){
                        choiceBoxGraph.getItems().add(graphNames.get(i));
                }
            }
            totalGraph.set(graphNames.size());
        });
    }
    public void startGraphListRefresher() {
        listRefresher = new GraphListRefresher(
                autoUpdate, this::updateGraphList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }
    public void close() {
        choiceBoxGraph.getItems().clear();
        totalGraph.set(0);
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
    public void setMainController(MainGraphController mainController) {
        this.mainController = mainController;
    }




    @FXML private ChoiceBox<String> choiceBoxGraph;
    @FXML private Text nameOfGraphText;
    @FXML private Text creatorText;
    @FXML private Text priceSimulationText;
    @FXML private Text priceCompilationText;
}
