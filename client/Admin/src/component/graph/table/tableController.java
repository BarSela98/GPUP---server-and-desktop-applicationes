package component.graph.table;

import ODT.Target;
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
        tableView.setItems(mainController.getItems());
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



    @FXML private Text targetNumberText;
    @FXML private Text leavesNumberText;
    @FXML private Text middleNumberText;
    @FXML private Text rootsNumberText;
    @FXML private Text indepNumberText;
    @FXML private Text serialSetText;
    @FXML private VBox vboxTable;
    @FXML private HBox hboxTable;
    @FXML private BorderPane borderPaneTable;
    @FXML private GridPane gridPaneTable;
}


