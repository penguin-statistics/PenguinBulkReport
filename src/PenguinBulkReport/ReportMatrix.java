package PenguinBulkReport;

import org.json.JSONArray;

public class ReportMatrix {
    private String stageId;
    private JSONArray drop_list;
    private int furniture_num;
    private static String source = "penguin bulk report";

    public ReportMatrix(){
        this.stageId = "";
        this.drop_list = new JSONArray();
        this.furniture_num =0;
    }

    public ReportMatrix(String stage, JSONArray list, int furniture_num){
        this.stageId = stage;
        this.drop_list = list;
        this.furniture_num =furniture_num;
    }

    public int getFurniture_num() {
        return furniture_num;
    }

    public void setFurniture_num(int furniture_num) {
        this.furniture_num = furniture_num;
    }

    public JSONArray getDrop_list() {
        return drop_list;
    }

    public void setDrop_list(JSONArray drop_list) {
        this.drop_list = drop_list;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }
}// class ends
