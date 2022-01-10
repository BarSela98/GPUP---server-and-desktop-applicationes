package component.graph.excute;

import ODT.*;
import com.google.gson.Gson;
import component.graph.main.MainGraphController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;
import utility.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static util.Constants.ADD_MISSION;

public class missionAdminController {
    private final SimpleBooleanProperty runTask;
    private MainGraphController mainController;
    private final SimpleBooleanProperty isCompiler;
    private final ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private final IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);

    ////private static final Object Lock = new Object();

    @FXML public void initialize() {
        setComboBox();
        executeButton.setDisable(true);
        simulationToggle.setOpacity(0.3);
        simulationBox.disableProperty().bind(isCompiler);
        compilerBox.disableProperty().bind(isCompiler.not());
        numCheckBoxesSelected.addListener((obs, oldSelectedCount, newSelectedCount) -> displayRunButton());
        isCompiler.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            if (isCompiler.getValue()) {
                compilerToggle.setStyle(mainController.getToggleColor());
                compilerToggle.setOpacity(1);
                simulationToggle.setOpacity(0.3);
            }
            else {
                simulationToggle.setStyle(mainController.getToggleColor());
                simulationToggle.setOpacity(1);
                compilerToggle.setOpacity(0.3);
            }
        });
        selectAll.selectedProperty().addListener((obs, oldSelectedCount, newSelectedCount)->{
            if (newSelectedCount){
                ObservableList<TargetTable> data = tableView.getItems();
                for (TargetTable p : data)
                    p.getCheckBoxTask().setSelected(true);
            }
        });
    }
    public missionAdminController(){
    isCompiler = new SimpleBooleanProperty(true);
    runTask = new SimpleBooleanProperty(false);
}
    /**
     * manage when the run button show -
     * if the compiler selected - only when the target and source folder choose and target selected
     * if the simulation selected - only if target selected
     */
    public void displayRunButton(){
        if (compilerToggle.isSelected()) {
            executeButton.setDisable(sourceFolderText.getText().equals("") || targetFolderText.getText().equals("") || selectedCheckBoxes.size() == 0);
        }
        else executeButton.setDisable(selectedCheckBoxes.size() == 0);
    }

    /**
     * manage the simulation and compiler Toggles
     * @param event
     */
    @FXML void compilerToggleSelected(ActionEvent event) {
        isCompiler.set(true);
        compilerToggle.setSelected(true);
        simulationToggle.setSelected(false);
        displayRunButton();
    }
    @FXML void simulationToggleSelected(ActionEvent event) {
        isCompiler.set(false);
        simulationToggle.setSelected(true);
        compilerToggle.setSelected(false);
        displayRunButton();
    }
    /**
     * choose source folder from directory chooser
     * @param event
     */
    @FXML void sourceFolderChooserTask(ActionEvent event) {
        File selectedDirectory = new DirectoryChooser().showDialog(new Stage());
        sourceFolderText.setText(selectedDirectory.getPath());
        displayRunButton();
    }
    /**
     * choose target folder from directory chooser
     * @param event
     */
    @FXML void targetFolderChooserTask(ActionEvent event) {
        File selectedDirectory = new DirectoryChooser().showDialog(new Stage());
        targetFolderText.setText(selectedDirectory.getPath());
        displayRunButton();
    }

    public void setMainController(MainGraphController mainController) {
        this.mainController = mainController;
        setTableCol();
        setToggles();
        setSpinner();
    }

    public void setComboBox(){
        scratchOrIncremental.getItems().add("Scratch");
        scratchOrIncremental.getItems().add("Incremental");
        scratchOrIncremental.setValue("Scratch");
    }
    public void setToggles(){
        compilerToggle.setSelected(true);
        compilerToggle.setStyle("-fx-background-color: linear-gradient(#2A5058, #61a2b1)");
        simulationToggle.setStyle("-fx-background-color: linear-gradient(#2A5058, #61a2b1)"); ///////////////
    }
    public void setSpinner(){
        ProcessingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 1000));
        successSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 100));
        successWithWarningSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spinnerManuallyTyping(ProcessingTimeSpinner);
        spinnerManuallyTyping(successSpinner);
        spinnerManuallyTyping(successWithWarningSpinner);
    }
    public void spinnerManuallyTyping (Spinner <Integer> spinner){
        spinner.setEditable(true);
        TextFormatter formatter3 = new TextFormatter(spinner.getValueFactory().getConverter(), spinner.getValueFactory().getValue());
        spinner.getEditor().setTextFormatter(formatter3);
        spinner.getValueFactory().valueProperty().bindBidirectional(formatter3.valueProperty());
    }

    @FXML void clearAction(ActionEvent event) {
        selectAll.setSelected(false);
        ObservableList<TargetTable> data = tableView.getItems();
        for (TargetTable p : data)
        {
            if(p.getCheckBoxTask().isSelected())
                p.getCheckBoxTask().setSelected(false);
        }
    }
    /**
     * if "root" target - select all the target with the dependence
     * @param d
     */
    public void getAllTargetWith(Utility.Dependence d ){
        List<String> newList = new ArrayList();
        try{
            for (int i = 0 ; i < mainController.getItems().size();++i) {
                if (mainController.getItems().get(i).getCheckBoxTask().isSelected()) {
                    mainController.getGraph().getEngine().whatIf(mainController.getItems().get(i).getName(), newList, d);
                    for (int k = 0; k < mainController.getItems().size(); ++k)
                        if (newList.contains(mainController.getItems().get(k).getName())) {
                            mainController.getItems().get(k).getCheckBoxTask().setSelected(true);
                        }
                }
            }
        }
        catch (Exception e){
          //  new errorMain(e);
        }
    }

    @FXML void executeMission(ActionEvent event) {
        Utility.WhichTask whichTask;
        Utility.TypeOfRunning typeOfRunning;
        Compilation compilation;
        Simulation simulation;
        Mission mission;
        if (nameOfMissionText.getText().equals(""))
            return;

        if (scratchOrIncremental.getValue().equals("scratch"))
            typeOfRunning = Utility.TypeOfRunning.SCRATCH;
        else
            typeOfRunning = Utility.TypeOfRunning.INCREMENTAL;

        List<Target> targetsToRun=new ArrayList<>();
        // list of targets that choose
        for(TargetTable t:tableView.getItems()){
            if(t.getCheckBoxTask().isSelected()){
                targetsToRun.add(mainController.getGraph().getTargetMap().get(t.getName()));
            }
        }

        if (compilerToggle.isSelected()) {
            whichTask =  Utility.WhichTask.COMPILATION;
            int price = Integer.parseInt(compilationPrice.getText());
            compilation = new Compilation(price,sourceFolderText.getText(),targetFolderText.getText());
            mission = new Mission(nameOfMissionText.getText(),mainController.getGraph().getNameOfCreator(),targetsToRun,whichTask,typeOfRunning,compilation);

        } else{
            whichTask = Utility.WhichTask.SIMULATION;
            int price = Integer.parseInt(simulationPrice.getText());
            simulation = new Simulation(price,ProcessingTimeSpinner.getValue(),randomCheckBox.isSelected(), (float) successSpinner.getValue()/100, (float)successWithWarningSpinner.getValue()/100);
            mission = new Mission(nameOfMissionText.getText(),mainController.getGraph().getNameOfCreator(),targetsToRun,whichTask,typeOfRunning,simulation);
        }

        String json = new Gson().toJson(mission);
//////////////////////////////////////////////////////////////////////////////////////////
        String finalUrl = HttpUrl
                .parse(ADD_MISSION)
                .newBuilder()
                .build()
                .toString();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        System.out.println("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();

                    Platform.runLater(() ->
                            System.out.println("Something went wrong: " + responseBody)
                    );

                } else {
                    Platform.runLater(() -> {
                        try {
                            System.out.println(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    public void updateTable() {
        compilationPrice.setText(String.valueOf(mainController.getGraph().getPriceForCompilation()));
        simulationPrice.setText(String.valueOf(mainController.getGraph().getPriceForSimulation()));
        tableView.setItems(mainController.getItems());
        for (int i = 0 ; i< mainController.getItems().size();++i)
            configureCheckBoxTask(mainController.getItems().get(i).getCheckBoxTask());
    }
    public void setTableCol(){
        nameTableCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeTableCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        directRequiredForTableCol.setCellValueFactory(new PropertyValueFactory<>("directRequiredForTableCol"));
        directDependsOnTableCol.setCellValueFactory(new PropertyValueFactory<>("directDependsOnTableCol"));
        totalRequiredForTableCol.setCellValueFactory(new PropertyValueFactory<>("totalRequiredForTableCol"));
        totalDependsOnTableCol.setCellValueFactory(new PropertyValueFactory<>("totalDependsOnTableCol"));
        dataTableCol.setCellValueFactory(new PropertyValueFactory<>("userData"));
        remarkTableCol.setCellValueFactory(new PropertyValueFactory<>("checkBoxTask"));
    }
    private void configureCheckBoxTask(CheckBox checkBox) {
        if (checkBox.isSelected())
            selectedCheckBoxes.add(checkBox);

        checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                selectedCheckBoxes.add(checkBox);
                if (withDepend.isSelected())
                    getAllTargetWith(Utility.Dependence.DEPENDS_ON);
                if (withRequired.isSelected())
                    getAllTargetWith(Utility.Dependence.REQUIRED_FOR);
            }
            else
                selectedCheckBoxes.remove(checkBox);
        });
    }

    @FXML private GridPane gridPaneTAsk;
    @FXML private HBox hboxTask;
    @FXML private Button clearButton;
    @FXML private Label taskMangerTitle;
    @FXML private GridPane gridPaneTask2;
    @FXML private HBox hbox2;
    @FXML private ToggleButton compilerToggle;
    @FXML private ToggleButton simulationToggle;
    @FXML private VBox hbox3;
    @FXML private ComboBox<String> scratchOrIncremental;
    @FXML private Button executeButton;
    @FXML private HBox simulationBox;
    @FXML private VBox vbox1;
    @FXML private CheckBox randomCheckBox;
    @FXML private VBox vbox2;
    @FXML private Spinner<Integer> ProcessingTimeSpinner;
    @FXML private Spinner<Integer> successSpinner;
    @FXML private Spinner<Integer> successWithWarningSpinner;
    @FXML private VBox compilerBox;
    @FXML private Button sourceFolderChooser;
    @FXML private Button targetFolderChooser;
    @FXML private Text sourceFolderText;
    @FXML private Text targetFolderText;
    @FXML private VBox vbox11;
    @FXML private Text simulationPrice;
    @FXML private Text compilationPrice;
/// table
    @FXML private TableView<TargetTable> tableView;
    @FXML private TableColumn<TargetTable,Boolean> remarkTableCol;
    @FXML private TableColumn<TargetTable, String> nameTableCol;
    @FXML private TableColumn<TargetTable, Target.Type> typeTableCol;
    @FXML private TableColumn<TargetTable, Integer> directRequiredForTableCol;
    @FXML private TableColumn<TargetTable, Integer> totalRequiredForTableCol;
    @FXML private TableColumn<TargetTable, Integer> directDependsOnTableCol;
    @FXML private TableColumn<TargetTable, Integer> totalDependsOnTableCol;
    @FXML private TableColumn<TargetTable, String> dataTableCol;
    @FXML private TableColumn<TargetTable, Integer> dependsOnTableCol;
    @FXML private TableColumn<TargetTable, String> requiredForTableCol;

    @FXML private TextField nameOfMissionText;
    @FXML private CheckBox withRequired;
    @FXML private CheckBox withDepend;

    ////
    @FXML private CheckBox selectAll;

}
