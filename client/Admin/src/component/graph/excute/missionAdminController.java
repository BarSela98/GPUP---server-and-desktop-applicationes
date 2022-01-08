package component.graph.excute;

import ODT.TargetTable;
import component.graph.main.MainGraphController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
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

import java.io.File;

public class missionAdminController {
    private final SimpleBooleanProperty runTask;
    private MainGraphController mainController;
    private final SimpleBooleanProperty isCompiler;
    private final ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private final IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);
    private boolean showFinish;
    private Thread thread;
    private static final Object Lock = new Object();

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
            if  (sourceFolderText.getText().equals("") || targetFolderText.getText().equals("") || selectedCheckBoxes.size() == 0)
                executeButton.setDisable(true);
            else
                executeButton.setDisable(false);
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

    @FXML public void initialize() {
        setComboBox();
        executeButton.setDisable(true);
        simulationBox.disableProperty().bind(isCompiler);
        compilerBox.disableProperty().bind(isCompiler.not());
        isCompiler.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            if (isCompiler.getValue()) {
                compilerToggle.setStyle("-fx-background-color: linear-gradient(#2A5058, #61a2b1)");
                compilerToggle.setOpacity(1);
                simulationToggle.setOpacity(0.3);
            }
            else {
                simulationToggle.setStyle("-fx-background-color: linear-gradient(#2A5058, #61a2b1)");
                simulationToggle.setOpacity(1);
                compilerToggle.setOpacity(0.3);
            }
        });
    }
    /// set all the details to run this task pane
    private void configureCheckBoxTask(CheckBox checkBox) {
    }
    public void setMainController(MainGraphController mainController) {
        this.mainController = mainController;
        setTableCol();
        setToggles();
        setSpinner();
    }


    public void setTableCol(){
        nameTableCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        CreatorTableCol.setCellValueFactory(new PropertyValueFactory<>("creator"));
        rootCol.setCellValueFactory(new PropertyValueFactory<>("root"));
        middleCol.setCellValueFactory(new PropertyValueFactory<>("middle"));
        leafCol.setCellValueFactory(new PropertyValueFactory<>("leaf"));
        IndependentsCol.setCellValueFactory(new PropertyValueFactory<>("independents"));
        workersCol.setCellValueFactory(new PropertyValueFactory<>("workers"));
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

    @FXML
    void clearAction(ActionEvent event) {

    }

    @FXML
    void executeMission(ActionEvent event) {

    }
    public void updateTable() {
        tableView.setItems(mainController.getItems());
    }

    @FXML private GridPane gridPaneTAsk;
    @FXML private TableView<TargetTable> tableView;
    @FXML private TableColumn<TargetTable, CheckBox> remarkTableCol;
    @FXML private TableColumn<TargetTable, String> nameTableCol;
    @FXML private TableColumn<TargetTable, String> CreatorTableCol;
    @FXML private TableColumn<TargetTable, Integer> rootCol;
    @FXML private TableColumn<TargetTable, Integer> middleCol;
    @FXML private TableColumn<TargetTable, Integer> leafCol;
    @FXML private TableColumn<TargetTable, Integer> IndependentsCol;
    @FXML private TableColumn<TargetTable, Integer> workersCol;
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
}
