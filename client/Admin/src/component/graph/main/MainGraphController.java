package component.graph.main;

import ODT.Graph;
import ODT.TargetTable;
import component.graph.excute.missionAdminController;
import component.graph.general.GeneralGraphController;
import component.graph.path.pathController;
import component.graph.table.tableController;
import component.page.AdminPageController;
import engine.Target;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainGraphController {
    Graph graph;

    private AdminPageController mainController;
    private ObservableList<TargetTable> items = FXCollections.observableArrayList();

    @FXML public void initialize() {
        if(pathComponentController != null && tableComponentController != null && missionAdminComponentController != null && generalComponentController != null){
            pathComponentController.setMainController(this);
            tableComponentController.setMainController(this);
            missionAdminComponentController.setMainController(this);
            generalComponentController.setMainController(this);
        }
    }
    public void setMainController(AdminPageController mainController) {
        this.mainController = mainController;
    }

    public void startGraphListRefresher() {
          generalComponentController.startGraphListRefresher();
     }
     public void close() {
          generalComponentController.close();
     }

    public void updateTable() {
        tableComponentController.updateTable();
    }
    public void updateExecute() {missionAdminComponentController.updateTable();}
    public void updatePath() {
        pathComponentController.updatePath();
    }
    public void addDataToTable() {
        try {
            items.clear();
            Map<String, Target> map = graph.getTargetMap();
            for (String keys : map.keySet()) {
                TargetTable t = new TargetTable(map.get(keys));
                //t.setSerialSetTableCol();
                List<String > list= new ArrayList<>();

                graph.getEngine().whatIf(t.getName(),list, utility.Utility.Dependence.DEPENDS_ON);
                t.setTotalDependsOnTableCol(list.size()-1);
                list.clear();
                graph.getEngine().whatIf(t.getName(),list, utility.Utility.Dependence.REQUIRED_FOR);
                t.setTotalRequiredForTableCol(list.size()-1);

                items.add(t);
            }
        }
        catch (Exception e){}
    }
    public String getToggleColor(){
        String toggleColor = "-fx-background-color: linear-gradient(#2A5058, #61a2b1)";
        return toggleColor;}

    public ObservableList<TargetTable> getItems() {
        return items;
    }
    // graph - get and set
    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    public Graph getGraph() {
        return  this.graph;
    }


    /// tab fxml
    @FXML private AnchorPane settingAnchorPane;
    @FXML private AnchorPane graphAnchorPane;
    @FXML private Tab tableTab;
    @FXML private Tab treeViewTab;
    @FXML private Tab graphTab;
    @FXML private Tab pathTab;
    @FXML private Tab taskTab;
    @FXML private Tab fileTab;
    @FXML private Tab settingTab;
    @FXML private BorderPane mainBorderPane;

    @FXML private BorderPane pathComponent;
    @FXML private pathController pathComponentController;
    @FXML private BorderPane tableComponent;
    @FXML private tableController tableComponentController;
    @FXML private BorderPane missionAdminComponent;
    @FXML private missionAdminController missionAdminComponentController;
    @FXML private BorderPane generalComponent;
    @FXML private GeneralGraphController generalComponentController;
}

