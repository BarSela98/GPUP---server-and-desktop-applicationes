package component.graph.general;
import component.graph.main.mainGraphController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class GeneralGraphController {

    private mainGraphController mainController;



    public void setMainController(mainGraphController mainController) {
        this.mainController = mainController;
    }




    @FXML private ChoiceBox<?> chooseGraphBox;
    @FXML private Text nameOfGraphText;
    @FXML private Text creatorText;
    @FXML private Text priceSimulationText;
    @FXML private Text priceCompilationText;
    public TableView generalGraphTableView;

    @FXML private TableView<?> genaralGraphTabelView;
    @FXML private TableColumn<?, ?> targetNameCol;
    @FXML private TableColumn<?, ?> rootCol;
    @FXML private TableColumn<?, ?> middleCol;
    @FXML private TableColumn<?, ?> leafCol;
    @FXML private TableColumn<?, ?> IndependentsCol;


}
