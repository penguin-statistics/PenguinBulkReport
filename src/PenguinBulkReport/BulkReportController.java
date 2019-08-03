package PenguinBulkReport;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.border.TitledBorder;


/**
 *  BulkReportController
 *      - Controller for program GUI part
 */
public class BulkReportController {
    private HashMap<String,String> stages = new HashMap<>();
    private HashMap<String, String> items = new HashMap<>();
    private HashMap<String, JSONArray> limitations = new HashMap<>();
    private ArrayList<String> choices_of_stage = new ArrayList<>();
    private HBox[] hboxes = new HBox[12];
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
    void select_stage(MouseEvent event){
        clear_boxes();

        String stage_selected = stage_list.getSelectionModel().getSelectedItem();
        JSONObject info = PenguinBulkReport.stage_info(stages.get(stage_selected));
        JSONArray bounds = limitations.get(stages.get(stage_selected));

        JSONArray normal_drop = info.getJSONArray("normalDrop");
        JSONArray special_drop = info.getJSONArray("specialDrop");
        JSONArray extra_drop = info.getJSONArray("extraDrop");

        int hbox_index = 0;

        for (int i = 0;i<normal_drop.length(); i++){
            String item_name = items.get(normal_drop.getString(i));
            URL urlToImage = this.getClass().getResource("icons/"+item_name+"icon.png");
            Image icon_image = new Image(String.valueOf(urlToImage),50,50,false,false);
            hboxes[0].setStyle("-fx-border-width: 2;" +"-fx-border-color: DeepSkyBlue");
            ((ImageView)hboxes[0].getChildren().get(0)).setImage(icon_image);
            ((Label)((VBox)hboxes[0].getChildren().get(1)).getChildren().get(0)).setText("常规掉落");
            ((Label)((VBox)hboxes[0].getChildren().get(1)).getChildren().get(0)).setTextFill(Color.web("#00bfff"));
            ((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(1).setVisible(true);
            hbox_index++;
        }
        for (int i = 0;i<special_drop.length(); i++){
            String item_name = items.get(special_drop.getString(i));
            URL urlToImage = this.getClass().getResource("icons/"+item_name+"icon.png");
            Image icon_image = new Image(String.valueOf(urlToImage),50,50,false,false);
            hboxes[hbox_index].setStyle("-fx-border-width: 2;" +"-fx-border-color: coral");
            ((ImageView)hboxes[hbox_index].getChildren().get(0)).setImage(icon_image);
            ((Label)((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(0)).setText("特殊掉落");
            ((Label)((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(0)).setTextFill(Color.web("#ff7f50"));
            ((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(1).setVisible(true);
            hbox_index++;
        }
        for (int i = 0;i<extra_drop.length(); i++){
            String item_name = items.get(extra_drop.getString(i));
            URL urlToImage = this.getClass().getResource("icons/"+item_name+"icon.png");
            Image icon_image = new Image(String.valueOf(urlToImage),50,50,false,false);
            hboxes[hbox_index].setStyle("-fx-border-width: 2;" +"-fx-border-color: dimgrey");
            ((ImageView)hboxes[hbox_index].getChildren().get(0)).setImage(icon_image);
            ((Label)((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(0)).setText("额外掉落");
            ((Label)((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(0)).setTextFill(Color.web("#696969"));
            ((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(1).setVisible(true);
            hbox_index++;
        }
        hboxes[11].setVisible(true);
    }
//    private HBox create_Item_panel(String item, String type){
//        HBox item_base = new HBox(2);
//        URL urlToImage = this.getClass().getResource("icons/"+item+"icon.png");
//        Image icon_image = new Image(String.valueOf(urlToImage),50,50,false,false);
//        ImageView icon = new ImageView(icon_image);
//        VBox box_for_quantity = new VBox();
//        TextField quantity = new TextField();
//        Label type_label = new Label();
//        box_for_quantity.getChildren().addAll(type_label,quantity);
//        switch (type) {
//            case "normal":
//                item_base.setStyle("-fx-border-width: 2;" +"-fx-border-color: DeepSkyBlue");
//                type_label.setText("常规掉落");
//                type_label.setTextFill(Color.web("#00bfff"));
//                break;
//            case "special":
//                item_base.setStyle("-fx-border-width: 2;" +"-fx-border-color: coral");
//                type_label.setText("特殊掉落");
//                type_label.setTextFill(Color.web("#ff7f50"));
//                break;
//            case "extra":
//                item_base.setStyle("-fx-border-width: 2;" +"-fx-border-color: dimgrey");
//                type_label.setText("额外掉落");
//                type_label.setTextFill(Color.web("#696969"));
//                break;
//        }
//        item_base.getChildren().addAll(icon,box_for_quantity);
//        return item_base;
//    }

    private void clear_boxes(){
        for (int i=0;i<11;i++){
            ((ImageView)hboxes[i].getChildren().get(0)).setImage(null);
            ((Label)((VBox)hboxes[i].getChildren().get(1)).getChildren().get(0)).setText("");
            ((VBox)hboxes[i].getChildren().get(1)).getChildren().get(1).setVisible(false);
            hboxes[i].setStyle(null);
        }
    }

    private HBox create_Furniture_panel(){
        HBox item_base = new HBox();
        Label furniture_label = new Label("家具掉落");
        Button furniture_yes_button = new Button("是");
        Button furniture_no_button = new Button("否");
        item_base.getChildren().addAll(furniture_label,furniture_yes_button,furniture_no_button);
        return item_base;
    }

    @FXML
    void initialize() {
        if (items.isEmpty()) {
            JSONArray allItems = PenguinBulkReport.all_items();
            for (int i = 0; i < allItems.length(); i++) {
                JSONObject item = allItems.getJSONObject(i);
                String itemId = item.optString("itemId");
                String itemName = item.optString("name");
                items.put(itemId,itemName);
            }
        }
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
        if (limitations.isEmpty()) {
            JSONArray allLimitaions = PenguinBulkReport.all_stages();
            for (int i = 0; i < allLimitaions.length(); i++) {
                JSONObject item = allLimitaions.getJSONObject(i);
                String stage = item.optString("name");
                JSONArray itemQuatityBounds = (JSONArray) item.opt("itemQuantityBounds");
                limitations.put(stage,itemQuatityBounds);
            }
        }
        for (int i=0;i<11;i++){
            hboxes[i] = new HBox(2);
            ImageView icon = new ImageView();
            VBox box_for_quantity = new VBox();
            TextField quantity = new TextField();
            quantity.setVisible(false);
            Label type_label = new Label();
            box_for_quantity.getChildren().addAll(type_label,quantity);
            hboxes[i].getChildren().addAll(icon,box_for_quantity);
            result_grid.add(hboxes[i], i%3,i/3);
            hboxes[i].setPrefWidth(100);
        }
        hboxes[11] = create_Furniture_panel();
        hboxes[11].setVisible(false);
        result_grid.add(hboxes[11],2,3);

        assert add_stage_button != null : "fx:id=\"add_stage_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert login_button != null : "fx:id=\"login_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert result_grid != null : "fx:id=\"result_grid\" was not injected: check your FXML file 'scene.fxml'.";
        assert stage_list != null : "fx:id=\"stage_list\" was not injected: check your FXML file 'scene.fxml'.";
        assert user_id_field != null : "fx:id=\"user_id_field\" was not injected: check your FXML file 'scene.fxml'.";


    }

}