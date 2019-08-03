package PenguinBulkReport;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *  BulkReportController
 *      - Controller for program GUI part
 */
public class BulkReportController {
    private HashMap<String,String> stages = new HashMap();
    private ArrayList<String> choices_of_stage = new ArrayList<>();

    @FXML
    private BorderPane frame;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button add_stage_button;

    @FXML
    private Button clear_stage_button;

    @FXML
    private Button login_button;

    @FXML
    private GridPane result_grid;

    @FXML
    private ListView<String> stage_list;

    @FXML
    private TextField user_id_field;


    @FXML
    void add_stage(ActionEvent event){
        if (stages.isEmpty()) {
            JSONArray allStages = PenguinBulkReport.all_stages();
            for (int i = 0; i < allStages.length(); i++) {
                JSONObject item = allStages.getJSONObject(i);
                String stageType = item.optString("stageType");
                if (stageType.equals("MAIN")) {
                    String stageID = item.optString("code");
                    String fullID = item.optString("stageId");
                    stages.put(stageID,fullID);
                }
            }
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, stages.keySet());
        dialog.setTitle("Please choose a stage");
        dialog.setHeaderText("添加一个您刷素材的地点");
        dialog.setContentText("地点");
        Optional<String> result = dialog.showAndWait();;
        result.ifPresent(s -> {
            choices_of_stage.add(s);
        });
        updateListView();
    }// add stage ends

    @FXML
    void clear_stage(ActionEvent event){
        choices_of_stage.clear();
        updateListView();
    }

    @FXML
    void user_login(ActionEvent event) {
        String userId = user_id_field.getText();
        int login_status = PenguinBulkReport.login(userId);
        if (login_status==200){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Login successfully");
            alert.showAndWait();
        }else if (login_status==201){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("User ID created");
            alert.showAndWait();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Login failed");
            alert.showAndWait();

        }
        System.out.println(login_status);
    }

    private void updateListView(){
        stage_list.setItems(FXCollections.observableArrayList(choices_of_stage));
    }

    @FXML
    void get_selected_stage(ActionEvent event){
        String stage_selected = stage_list.getSelectionModel().getSelectedItem();
        PenguinBulkReport.stage_info(stages.get(stage_selected));
    }

    private void create_Item_panel(String item){
        VBox item_base = new VBox();
        ImageView icon = new ImageView();
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