package component.graph.path;

import engine.Target;
import ODT.TargetTable;
import ODT.information.Information;
import component.graph.main.MainGraphController;
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
import utility.Utility;

import java.util.ArrayList;
import java.util.List;


public class pathController {
    private MainGraphController mainController;
    private SimpleBooleanProperty isSourceSelected;
    private SimpleBooleanProperty isDestinationSelected;
    private ObservableSet<CheckBox> selectedCheckBoxes = FXCollections.observableSet();
    private ObservableSet<CheckBox> unselectedCheckBoxes = FXCollections.observableSet();
    private IntegerBinding numCheckBoxesSelected = Bindings.size(selectedCheckBoxes);
    private final int maxNumSelected =  2;
    public pathController(){
        isSourceSelected = new SimpleBooleanProperty(false);
        isDestinationSelected = new SimpleBooleanProperty(false);
    }
    @FXML public void initialize() {
        setDefault();
        setBinding();
        setTableCol();
        /// Default is path toggle selected
        toggleButtonWhatIf.setOpacity(0.3);
        toggleButtonCycle.setOpacity(0.3);
    }
    /**
     * manage the toggle, only one select
     */
    public void groupToggle(){
        toggleButtonWhatIf.selectedProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue) {
                toggleButtonWhatIf.setOpacity(1);
                toggleButtonPath.setSelected(false);
                toggleButtonCycle.setSelected(false);
            }
            else {
                toggleButtonWhatIf.setOpacity(0.3);
            }
        }) ;
        toggleButtonCycle.selectedProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue) {
                toggleButtonCycle.setOpacity(1);
                toggleButtonPath.setSelected(false);
                toggleButtonWhatIf.setSelected(false);
            }
            else {
                toggleButtonCycle.setOpacity(0.3);
            }
        }) ;
        toggleButtonPath.selectedProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue) {
                toggleButtonPath.setOpacity(1);
                toggleButtonWhatIf.setSelected(false);
                toggleButtonCycle.setSelected(false);
            }
            else {
                toggleButtonPath.setOpacity(0.3);
            }
        }) ;
    }

    public void setTableCol(){
        nameTableCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeTableCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dataTableCol.setCellValueFactory(new PropertyValueFactory<>("userData"));
        directRequiredForTableCol.setCellValueFactory(new PropertyValueFactory<>("directRequiredForTableCol"));
        directDependsOnTableCol.setCellValueFactory(new PropertyValueFactory<>("directDependsOnTableCol"));
        totalRequiredForTableCol.setCellValueFactory(new PropertyValueFactory<>("totalRequiredForTableCol"));
        totalDependsOnTableCol.setCellValueFactory(new PropertyValueFactory<>("totalDependsOnTableCol"));
        remarkTableCol.setCellValueFactory(new PropertyValueFactory<>("checkBoxPath"));
    }
    public void setDefault(){
        toggleButtonPath.setSelected(true);
        toggleButtonWhatIf.setSelected(false);
        toggleButtonCycle.setSelected(false);
        // add all the option for the depends-on comboBox
        depenceComboBox.getItems().add(Utility.Dependence.REQUIRED_FOR);
        depenceComboBox.getItems().add(Utility.Dependence.DEPENDS_ON);
        depenceComboBox.setValue(Utility.Dependence.DEPENDS_ON);
    }

    /**
     * manage the display run button
     */
    public void setBinding(){
        ////// show destinationText only if toggleButtonPath is selected
        destinationText.opacityProperty().bind(
                Bindings.when(
                                toggleButtonPath.selectedProperty())
                        .then(1)
                        .otherwise(0.1)
        );
        ////// enable run button only if the source selected
        runButton.disableProperty().bind(isSourceSelected.not());

        ///// select only 2 checkBox from the table
        numCheckBoxesSelected.addListener((obs, oldSelectedCount, newSelectedCount) -> {
            if (newSelectedCount.intValue() >= maxNumSelected)
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(true));
            else
                unselectedCheckBoxes.forEach(cb -> cb.setDisable(false));
        });
    }
    public void setMainController(MainGraphController mainController) {
        this.mainController = mainController;
       // setTableCol();
        groupToggle();
    }
    public void updatePath() {
        tableView.setItems(mainController.getItems());
        for (int i = 0 ; i< mainController.getItems().size();++i)
            configureCheckBox(mainController.getItems().get(i).getCheckBoxPath());
    }


    /**
     * configure checkBox - when it selects add to list, unselect remove from list
     * @param checkBox
     */
    private void configureCheckBox(CheckBox checkBox) {

        if (checkBox.isSelected()) {
            selectedCheckBoxes.add(checkBox);
        } else {
            unselectedCheckBoxes.add(checkBox);
        }

        checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                unselectedCheckBoxes.remove(checkBox);
                selectedCheckBoxes.add(checkBox);
            } else {
                selectedCheckBoxes.remove(checkBox);
                unselectedCheckBoxes.add(checkBox);
            }

        });
    }


////// set on action button

    @FXML void setForRun(ActionEvent event) {
        
        int x = 0;
        ObservableList<TargetTable> data = tableView.getItems();
        for (TargetTable p : data)
        {
            if(p.getCheckBoxPath().isSelected()) {
                if (x == 0) {
                    sourceText.setText(p.getName());
                    isSourceSelected.set(true);
                    x++;
                }
                else if (x==1) {
                    destinationText.setText(p.getName());
                    isDestinationSelected.set(true);
                    x++;
                }
            }
        }

        if (x==1) {
            destinationText.setText(sourceText.getText());
            isDestinationSelected.set(true);
        }
    }

    /**
     * clear all checkBox
     * @param event
     */
    @FXML void clearAction(ActionEvent event) {
    
        ObservableList<TargetTable> data = tableView.getItems();
        for (TargetTable p : data)
        {
            if(p.getCheckBoxPath().isSelected())
                p.getCheckBoxPath().setSelected(false);
        }
    }
    @FXML void switchAction(ActionEvent event) {
        if (isDestinationSelected.get() && isSourceSelected.get()) {
            String temp = (sourceText.getText());
            sourceText.setText(destinationText.getText());
            destinationText.setText(temp);
        }
    }
    @FXML void runButtonAction(ActionEvent event) {
        
        Information info = null;
        try {

            if (isDestinationSelected.get() && isSourceSelected.get() && toggleButtonPath.isSelected())
                info = mainController.getGraph().getEngine().findAPathBetweenTwoTargets(sourceText.getText(), destinationText.getText(), depenceComboBox.getValue());

            else if (isSourceSelected.get()){

                if (toggleButtonCycle.isSelected())
                    info = mainController.getGraph().getEngine().circuitDetection(sourceText.getText());

                if (toggleButtonWhatIf.isSelected()){
                    List<String> whatIfList = new ArrayList<>();
                    mainController.getGraph().getEngine().whatIf(sourceText.getText(),whatIfList , depenceComboBox.getValue());
                    pathListView.getItems().clear();
                    whatIfList.remove(0);
                    pathListView.getItems().add(whatIfList.toString());
                }

            }
            if (info != null) {
                pathListView.getItems().clear();
                pathListView.getItems().add(info.toString());
            }

        }
        catch (Exception e){}
    }



////// fxml member

    @FXML private TableView<TargetTable> tableView;
    @FXML private TableColumn<TargetTable,Boolean> remarkTableCol;
    @FXML private TableColumn<TargetTable, String> nameTableCol;
    @FXML private TableColumn<TargetTable, Target.Type> typeTableCol;
    @FXML private TableColumn<TargetTable, Integer> directRequiredForTableCol;
    @FXML private TableColumn<TargetTable, Integer> totalRequiredForTableCol;
    @FXML private TableColumn<TargetTable, Integer> directDependsOnTableCol;
    @FXML private TableColumn<TargetTable, Integer> totalDependsOnTableCol;
    @FXML private TableColumn<TargetTable, String> dataTableCol;
    @FXML private ComboBox<Utility.Dependence> depenceComboBox;

    @FXML private ListView<String> pathListView;

    @FXML private Text sourceText;
    @FXML private Text destinationText;
    @FXML private Button switchButton;
    @FXML private Button clearButton;
    @FXML private ToggleButton toggleButtonPath;
    @FXML private ToggleButton toggleButtonCycle;
    @FXML private ToggleButton toggleButtonWhatIf;
    @FXML private Button runButton;
    @FXML private Button setButton;
    @FXML private VBox vboxPath;
    @FXML private GridPane gridPanePath;
    @FXML private HBox hbox1;
    @FXML private HBox hbox2;
    @FXML private GridPane gridPaneTablePath;

}

