package PenguinBulkReport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;


/**
 *  BulkReportController
 *      - Controller for program GUI part
 */
public class BulkReportController {
    private TreeMap<String,String> stages = new TreeMap<>();
    private HashMap<String, String> items = new HashMap<>();
    //private HashMap<String, JSONArray> limitations = new HashMap<>();
    private ArrayList<String> choices_of_stage = new ArrayList<>();
    private HBox[] hboxes = new HBox[20];
    private HashMap<String,HashMap<String,Object>> all_results = new HashMap<>();
    private TextField[] amount_fields = new TextField[20];
    private int total_type = 0;
    private String userId;
    private PenguinBulkReport p = new PenguinBulkReport();
    private ArrayList<ImageView> imageViews = new ArrayList<>();

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
    private TextField times_field;

    @FXML
    private VBox times_box;

    @FXML
    private Button save_result_button;

    @FXML
    private Button upload_button;

    @FXML
    private void add_stage(ActionEvent event){
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, stages.keySet());
        dialog.setTitle("Please choose a stage");
        dialog.setHeaderText("添加一个您刷素材的地点");
        dialog.setContentText("地点");
        Optional<String> result = dialog.showAndWait();;
        result.ifPresent(s -> {
            if (!choices_of_stage.contains(s)){
                choices_of_stage.add(s);
                updateListView();
            }
        });
        updateListView();
        hboxes[11].setVisible(true);
        times_box.setVisible(true);
    }// add stage ends

    @FXML
    private void clear_stage(ActionEvent event){
        choices_of_stage.clear();
        clear_boxes();
        updateListView();
        all_results.clear();
    }

    @FXML
    private void user_login(ActionEvent event) {
        String userId = user_id_field.getText();
        int login_status = p.login(userId);
        if (login_status==200){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Login successfully");
            this.userId = userId;
            alert.showAndWait();
        }else if (login_status==201){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("User ID created");
            this.userId = userId;
            alert.showAndWait();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("Login failed. \nError code is "+login_status);
            alert.showAndWait();

        }
    }

    private void updateListView(){
        stage_list.setItems(FXCollections.observableArrayList(choices_of_stage));
    }

    @FXML
    private void select_stage(MouseEvent event){
        clear_boxes();

        String stage_selected = stage_list.getSelectionModel().getSelectedItem();
        JSONObject info = PenguinBulkReport.stage_info(stages.get(stage_selected));
        //JSONArray bounds = limitations.get(stages.get(stage_selected));

        JSONArray normal_drop = info.getJSONArray("normalDrop");
        JSONArray special_drop = info.getJSONArray("specialDrop");
        JSONArray extra_drop = info.getJSONArray("extraDrop");

        int hbox_index = 0;

        for (int i = 0;i<normal_drop.length(); i++){
            String item_name = items.get(normal_drop.getString(i));
            URL urlToImage = this.getClass().getResource("icons/"+item_name+"icon.png");
            Image icon_image = new Image(String.valueOf(urlToImage),50,50,false,false);
            hboxes[hbox_index].setStyle("-fx-border-width: 2;" +"-fx-border-color: DeepSkyBlue");
            ((ImageView)hboxes[hbox_index].getChildren().get(0)).setImage(icon_image);
            ((Label)((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(0)).setText("常规掉落");
            ((Label)((VBox)hboxes[hbox_index].getChildren().get(1)).getChildren().get(0)).setTextFill(Color.web("#00bfff"));
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
        total_type = hbox_index;
        //System.out.println(total_type);
        hboxes[19].setVisible(true);
        times_box.setVisible(true);
        String key = null;
        for (String possible_key : stages.keySet()){
            if (stage_selected.equals(possible_key)){
                key = possible_key;
                break;
            }
        }
        if (key != null) {
            HashMap<String, Object> storage = all_results.get(stages.get(key));
            if (storage != null) {
                System.out.println(new JSONObject(storage).toString());
                times_field.setText((String) storage.get("times"));
                int[] temp_read_list = (int[]) storage.get("drop_list");
                for (int i = 0; i < temp_read_list.length; i++) {
                    amount_fields[i].setText(Integer.toString(temp_read_list[i]));
                }
                amount_fields[19].setText(storage.get("furniture_total").toString());
            } else {
                clear_boxes_text();
                amount_fields[19].setText("0");
            }
        } else {
            clear_boxes_text();
            amount_fields[19].setText("0");
        }
        for (int i = 0;i<normal_drop.length()+extra_drop.length()+special_drop.length();i++){
            hboxes[i].setVisible(true);
        }
        for (int i = normal_drop.length()+extra_drop.length()+special_drop.length();i<14;i++){
            hboxes[i].setVisible(false);
        }
    }//select_stage ends

    private void clear_boxes_text(){
        for (int i=0;i<19;i++){
            ((TextField)((VBox)hboxes[i].getChildren().get(1)).getChildren().get(1)).setText("0");
        }
        times_field.setText("0");
    }

    private void clear_boxes(){
        for (int i=0;i<19;i++){
            ((ImageView)hboxes[i].getChildren().get(0)).setImage(null);
            ((Label)((VBox)hboxes[i].getChildren().get(1)).getChildren().get(0)).setText("");
            ((TextField)((VBox)hboxes[i].getChildren().get(1)).getChildren().get(1)).setText("0");
            ((VBox)hboxes[i].getChildren().get(1)).getChildren().get(1).setVisible(false);
            hboxes[i].setStyle(null);
        }
        hboxes[19].setVisible(false);
        times_field.setText("0");
        times_box.setVisible(false);

    }

    private HBox create_Furniture_panel(){
        HBox item_pane = new HBox();
        VBox item_base = new VBox();
        Label furniture_label = new Label("家具掉落");
        TextField quantity = new TextField();
        quantity.setText("0");
        amount_fields[19] = quantity;
        item_base.getChildren().addAll(furniture_label,quantity);
        item_pane.getChildren().addAll(item_base);
        return item_pane;
    }

    @FXML
    private void save_StageResult(){
        String stage_selected = stages.get(stage_list.getSelectionModel().getSelectedItem());
        HashMap<String,Object> drop_results = new HashMap<String, Object>();
        drop_results.put("times",times_field.getText());
        int[] num_for_each = new int[19];
        for (int i =0;i<total_type;i++){
            num_for_each[i] =Integer.parseInt(amount_fields[i].getText());
        }
        drop_results.put("drop_list",num_for_each);
        drop_results.put("furniture_total",Integer.parseInt(amount_fields[19].getText()));
        all_results.put(stage_selected,drop_results);
    }

    @FXML
    private void upload(){
        if (userId == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login");
            alert.setHeaderText(null);
            alert.setContentText("这位Doctor，您还没登录企鹅数据呢~");
            alert.showAndWait();
        }
        ArrayList<JSONObject> output = p.stage_multiple_reports(userId, all_results);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Output of this upload");
        alert.setHeaderText("本次掉落汇报");
        alert.setContentText("请Doctor务必确认数据无误");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        for(JSONObject a : output){
            textArea.appendText(a.toString() + "\n");
        }

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setContent(expContent);
        alert.showAndWait();
        all_results.clear();
    }

    @FXML
    private void initialize() {

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
                if (stageType.equals("MAIN")||stageType.equals("SUB")) {
                    String stageID = item.optString("code");
                    if (stageID.contains("GT")){
                        continue;
                    }
                    String fullID = item.optString("stageId");
                    stages.put(stageID,fullID);
                }
            }
        }


        for (int i=0;i<19;i++){
            hboxes[i] = new HBox(2);
            ImageView icon = new ImageView();
            imageViews.add(icon);

            VBox box_for_quantity = new VBox();
            TextField quantity = new TextField();
            quantity.setText("0");
            quantity.setVisible(false);
            quantity.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        quantity.setText(newValue.replaceAll("[^\\d]", ""));
                    } else {
                        save_StageResult();
                    }
                }
            });
            icon.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("kokodayo");
                    int source_id = imageViews.indexOf(event.getSource());
                    System.out.println(source_id);
                    // left
                    if (event.getButton()== MouseButton.PRIMARY){
                        int previous_amount = Integer.parseInt(amount_fields[source_id].getText());
                        System.out.println(previous_amount);
                        previous_amount++;
                        amount_fields[source_id].setText(Integer.toString(previous_amount));
                    }
                    //right
                    if (event.getButton()== MouseButton.SECONDARY){
                        int previous_amount = Integer.parseInt(amount_fields[source_id].getText());
                        if (previous_amount >0) {
                            previous_amount--;
                            amount_fields[source_id].setText(Integer.toString(previous_amount));
                        }

                    }
                }
            });
            Label type_label = new Label();
            box_for_quantity.getChildren().addAll(type_label,quantity);
            amount_fields[i] = quantity;
            hboxes[i].getChildren().addAll(icon,box_for_quantity);
            result_grid.add(hboxes[i], i%4,i/4);
            hboxes[i].setPrefWidth(100);
            times_field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")){
                    quantity.setText(newValue.replaceAll("[^\\d]", ""));
                } else {
                    save_StageResult();
                }
            });

        }
        hboxes[19] = create_Furniture_panel();
        hboxes[19].setVisible(false);
        times_box.setVisible(false);
        result_grid.add(hboxes[19],16%4,16/4);
        amount_fields[19].textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    amount_fields[19].setText(newValue.replaceAll("[^\\d]", ""));
                } else {
                    save_StageResult();
                }
            }
        });
        assert add_stage_button != null : "fx:id=\"add_stage_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert upload_button != null : "fx:id=\"upload_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert save_result_button != null : "fx:id=\"save_result_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert clear_stage_button != null : "fx:id=\"clear_stage_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert frame != null : "fx:id=\"frame\" was not injected: check your FXML file 'scene.fxml'.";
        assert login_button != null : "fx:id=\"login_button\" was not injected: check your FXML file 'scene.fxml'.";
        assert result_grid != null : "fx:id=\"result_grid\" was not injected: check your FXML file 'scene.fxml'.";
        assert stage_list != null : "fx:id=\"stage_list\" was not injected: check your FXML file 'scene.fxml'.";
        assert times_box != null : "fx:id=\"times_box\" was not injected: check your FXML file 'scene.fxml'.";
        assert times_field != null : "fx:id=\"times_field\" was not injected: check your FXML file 'scene.fxml'.";
        assert user_id_field != null : "fx:id=\"user_id_field\" was not injected: check your FXML file 'scene.fxml'.";



    }

}