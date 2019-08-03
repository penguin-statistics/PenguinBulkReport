package PenguinBulkReport;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


/**
 *
 */
public class BulkReportController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button add_stage_button;

    @FXML
    private Button login_button;

    @FXML
    private GridPane result_grid;

    @FXML
    private ListView<?> stage_list;

    @FXML
    private TextField user_id_field;


    @FXML
    void user_login(ActionEvent event) {
    }

    @FXML
    void initialize() {
        assert add_stage_button != null : "fx:id=\"add_stage_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert login_button != null : "fx:id=\"login_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert result_grid != null : "fx:id=\"result_grid\" was not injected: check your FXML file 'scene.fxml'.";
        assert stage_list != null : "fx:id=\"stage_list\" was not injected: check your FXML file 'scene.fxml'.";
        assert user_id_field != null : "fx:id=\"user_id_field\" was not injected: check your FXML file 'scene.fxml'.";


    }



}