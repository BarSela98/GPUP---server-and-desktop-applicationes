package component.graph.table;

import ODT.Graph;
import engine.Target;
import ODT.TargetTable;
import component.graph.main.MainGraphController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class tableController {
    private MainGraphController mainController;

    @FXML public void initialize() {
        setTableCol();
    }
    public void setMainController(MainGraphController mainController) {
        this.mainController = mainController;

    }
    public void updateTable() {
        Graph graph = mainController.getGraph();
        tableView.setItems(mainController.getItems());
        priceSimulationText.setText(String.valueOf(graph.getPriceForSimulation()));
        priceCompilationText.setText(String.valueOf(graph.getPriceForCompilation()));
        targetNumberText.setText(String.valueOf(graph.getAmountOfTargets()));
        middleNumberText.setText(String.valueOf(graph.getAmountOfMiddles()));
        rootsNumberText.setText(String.valueOf(graph.getAmountOfRoots()));
        indepNumberText.setText(String.valueOf(graph.getAmountOfIndependents()));
        leavesNumberText.setText(String.valueOf(graph.getAmountOfLevies()));
    }

    public void setTableCol(){
        nameTableCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeTableCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dataTableCol.setCellValueFactory(new PropertyValueFactory<>("userData"));
        directRequiredForTableCol.setCellValueFactory(new PropertyValueFactory<>("directRequiredForTableCol"));
        directDependsOnTableCol.setCellValueFactory(new PropertyValueFactory<>("directDependsOnTableCol"));
        totalRequiredForTableCol.setCellValueFactory(new PropertyValueFactory<>("totalRequiredForTableCol"));
        totalDependsOnTableCol.setCellValueFactory(new PropertyValueFactory<>("totalDependsOnTableCol"));
    }

    ////// fxml member

    @FXML private TableView<TargetTable> tableView;
    @FXML private TableColumn<TargetTable, String> nameTableCol;
    @FXML private TableColumn<TargetTable, Target.Type> typeTableCol;
    @FXML private TableColumn<TargetTable, Integer> directRequiredForTableCol;
    @FXML private TableColumn<TargetTable, Integer> totalRequiredForTableCol;
    @FXML private TableColumn<TargetTable, Integer> directDependsOnTableCol;
    @FXML private TableColumn<TargetTable, Integer> totalDependsOnTableCol;
    @FXML private TableColumn<TargetTable, String> dataTableCol;

    @FXML private Text priceCompilationText;
    @FXML private Text priceSimulationText;
    @FXML private Text targetNumberText;
    @FXML private Text leavesNumberText;
    @FXML private Text middleNumberText;
    @FXML private Text rootsNumberText;
    @FXML private Text indepNumberText;
    @FXML private VBox vboxTable;
    @FXML private HBox hboxTable;
    @FXML private BorderPane borderPaneTable;
    @FXML private GridPane gridPaneTable;
}


