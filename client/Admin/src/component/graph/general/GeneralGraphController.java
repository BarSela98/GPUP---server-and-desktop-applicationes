package component.graph.general;

import ODT.Graph;
import component.graph.main.MainGraphController;
import error.errorMain;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static utility.Constants.*;


public class GeneralGraphController {
    private MainGraphController mainController;

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;

    private final IntegerProperty totalGraph;
    @FXML private ChoiceBox<String> choiceBoxGraph;
    @FXML private Text nameOfGraphText;
    @FXML private Text creatorText;


    @FXML public void initialize(){
        choiceBoxGraph.valueProperty().addListener((a,b,c)->{
            if(c == null)
                return;

            if(b != null &&  b.equals(c))
                return;

            String selectedItem = choiceBoxGraph.getSelectionModel().getSelectedItem();
            String finalUrl = HttpUrl
                    .parse(GRAPH)
                    .newBuilder()
                    .addQueryParameter("grapname", selectedItem)
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //Platform.runLater(() -> System.out.println("choose graph from comboBox - error -"+e.getMessage()));
                    Platform.runLater(() -> new errorMain("choose graph from comboBox - error -"+e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> new errorMain("choose graph from comboBox - Response code: "+response.code()+"\nResponse body: "+responseBody));
                       // Platform.runLater(() -> System.out.println("choose graph from comboBox - Response code: "+response.code()+"\nResponse body: "+responseBody));
                    } else{
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
    public void setMainController(MainGraphController mainController) {
        this.mainController = mainController;
    }

    public void updateGraphOnScene(Graph graph) {
        nameOfGraphText.setText(graph.getGraphName());
        creatorText.setText(graph.getNameOfCreator());
    }
    @FXML void loadGraphButton(ActionEvent event) {

        File file = new FileChooser().showOpenDialog(new Stage());
        String finalUrl = HttpUrl
                .parse(LOAD_XML_FILE)
                .newBuilder()
                .build()
                .toString();

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
             //   Platform.runLater(() -> System.out.println("Failed to load file - error -"+e.getMessage()));
                Platform.runLater(() -> new errorMain("Failed to load file - error -"+e.getMessage()));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain("loadGraphButton - admin - Response code: "+response.code()+"\nResponse body: "+responseBody));
                 //   Platform.runLater(() -> System.out.println("loadGraphButton - admin - Response code: "+response.code()+"\nResponse body: "+responseBody));
                }
            }
        });
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
}
