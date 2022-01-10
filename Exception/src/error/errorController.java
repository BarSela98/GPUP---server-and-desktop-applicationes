package error;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.io.IOException;

public class errorController {

    @FXML
    private Text errorText;

    public void error(Exception e) throws IOException {
        errorText.setText(e.toString());
    }
}
