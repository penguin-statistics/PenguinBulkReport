package PenguinBulkReport;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.nio.charset.Charset;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

/*
    Penguin-stats bulk report
         - report on specific stages in multiple times
         - backend methods
 */

public class PenguinBulkReport {


    public int login(String userID){
        int respond_code =0;
        try {
            URL url = new URL("https://penguin-stats.io/PenguinStats/api/users");
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setRequestMethod("POST");
            httpUrlConn.setRequestProperty("accept", "application/json, text/plain");
            httpUrlConn.setRequestProperty("content-type", "text/plain");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            httpUrlConn.connect();
            OutputStream os = httpUrlConn.getOutputStream();
            os.write(userID.getBytes(), 0 ,userID.getBytes().length);
            os.flush();
            os.close();
            respond_code = httpUrlConn.getResponseCode();
            httpUrlConn.disconnect();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return respond_code;
    }

    public int report(String stage_id, JSONArray drop_info, int furniture_num,String userID) {

        String requestUrl = "https://penguin-stats.io/PenguinStats/api/report";
        // Info send to penguin-stats.io
        JSONObject params = new JSONObject();
        params.put("stageId",stage_id);//Stage id
        params.put("drops",drop_info); // drop array with matrix
        params.put("furnitureNum",furniture_num);//either 1 | 0
        params.put("source","penguin bulk report");//either 1 | 0
        System.out.println(params.toString());
        int respond_code = sendDropRequest(requestUrl,params,userID);

        return respond_code;
    }

    public static JSONArray all_stages(){
        JSONArray stages = null;
        try {
            URL url = new URL("https://penguin-stats.io/PenguinStats/api/stages/");
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            httpUrlConn.connect();
            InputStream is = httpUrlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            String jsonText= sb.toString();
            stages = new JSONArray(jsonText);

            httpUrlConn.disconnect();
        } catch(Exception e){
            e.printStackTrace();
        }
        return stages;
    }

    public static JSONArray all_items(){
        JSONArray items = null;
        try {
            URL url = new URL("https://penguin-stats.io/PenguinStats/api/items/");
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            httpUrlConn.connect();
            InputStream is = httpUrlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            String jsonText= sb.toString();
            items = new JSONArray(jsonText);

            httpUrlConn.disconnect();
        } catch(Exception e){
            e.printStackTrace();
        }
        return items;
    }

    public static JSONArray all_Limitations(){
        JSONArray limitations = null;
        try {
            URL url = new URL("https://penguin-stats.io/PenguinStats/api/limitations/");
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            httpUrlConn.connect();
            InputStream is = httpUrlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            String jsonText= sb.toString();
            limitations = new JSONArray(jsonText);

            httpUrlConn.disconnect();
        } catch(Exception e){
            e.printStackTrace();
        }
        return limitations;
    }

    public Pair<JSONArray, JSONObject> get_limitations(String stageID){
        JSONArray allLimitations = all_Limitations();
        JSONArray itemQuantityBounds = null;
        JSONArray itemTypeBounds = null;
        for (int i = 0; i < allLimitations.length(); i++) {
            JSONObject item = allLimitations.getJSONObject(i);
            String stage = item.optString("name");
            if (stage.equals(stageID)){
                return new Pair<>(item.optJSONArray("itemQuantityBounds"), item.optJSONObject("itemTypeBounds"));
            }
        }
        return null;
    }

    public int[] get_item_limitation_in_stage(String stageID, String itemID){
        Pair<JSONArray, JSONObject> limitations = get_limitations(stageID);
        int[] bounds = new int[2];
        for (int i =0;i<limitations.getKey().length();i++){
            if (itemID.equals(limitations.getKey().getJSONObject(i).getString("itemId"))){
                bounds[0] = limitations.getKey().getJSONObject(i).getJSONObject("bounds").getInt("lower");
                bounds[1] = limitations.getKey().getJSONObject(i).getJSONObject("bounds").getInt("upper");
            }
        }
        return bounds;
    }

    public ArrayList<String> stage_multiple_reports(String userID, HashMap<String,HashMap<String,Object>> results){
        ArrayList<String> infos = new ArrayList<>();
        for (String stageID : results.keySet()) {
            JSONObject info = stage_info(stageID);
            JSONArray normal_drop = info.getJSONArray("normalDrop");
            JSONArray special_drop = info.getJSONArray("specialDrop");
            JSONArray extra_drop = info.getJSONArray("extraDrop");

            JSONArray summary_drop = null;
            int[] drop_list = (int[])results.get(stageID).get("drop_list");
            int furniture_total = (int)results.get(stageID).get("furniture_total");
            int furniture_num = 0;

            for (int t = 0; t < Integer.parseInt(results.get(stageID).get("times").toString()); t++) {
                summary_drop = new JSONArray();
                //JSONArray single_res = new JSONArray();
                if (furniture_total >0) {
                    furniture_num = 1;
                    furniture_total --;
                } else{
                    furniture_num =0;
                }
                int item_index = 0;
                int item_counter =0;
                int[] item_quantity_bound=null;
                int[] item_type_bounds = {get_limitations(stageID).getValue().getInt("lower"),get_limitations(stageID).getValue().getInt("upper")};
                for (int i = 0; i<normal_drop.length();i++){
                    if (drop_list[item_index] == 0){
                        item_index++;
                        continue;
                    }
                    HashMap<String, Object> item = new HashMap<>();
                    String id = normal_drop.getString(i);
                    item.put("itemId", id);
                    item_quantity_bound = get_item_limitation_in_stage(stageID, id);
                    if (drop_list[item_index] >= item_quantity_bound[1]) {
                        item.put("quantity", item_quantity_bound[1]);
                        drop_list[item_index] -= item_quantity_bound[1];
                    } else if (drop_list[item_index]>= item_quantity_bound[0] && drop_list[item_index]>0) {
                        item.put("quantity", drop_list[item_index]);
                        drop_list[item_index] -= drop_list[item_index];
                    }
                    summary_drop.put(new JSONObject(item));
                    item_counter++;
                    item_index++;
                    if (item_counter==item_type_bounds[1]){
                        break;
                    }
                }// normal drop loop ends
               // if(!((item_type_bounds[1]==item_quantity_bound[2] && item_quantity_bound[2]==normal_drop.length()) ||(item_index==item_type_bounds[1]))) {
                for (int i = 0; i < special_drop.length(); i++) {
                    if (drop_list[item_index] == 0) {
                        item_index++;
                        continue;
                    }
                    HashMap<String, Object> item = new HashMap<>();
                    String id = special_drop.getString(i);
                    item.put("itemId", id);
                    item_quantity_bound = get_item_limitation_in_stage(stageID, id);
                    if (drop_list[item_index] >= item_quantity_bound[1]) {
                        item.put("quantity", item_quantity_bound[1]);
                        drop_list[item_index] -= item_quantity_bound[1];
                    } else if (drop_list[item_index] >= item_quantity_bound[0]) {
                        item.put("quantity", item_quantity_bound[0]);
                        drop_list[item_index] -= item_quantity_bound[0];
                    }
                    summary_drop.put(new JSONObject(item));
                    item_index++;
                    item_counter++;
                }// extra drop loop ends

                System.out.println("item_counter:");
                System.out.println(item_counter);
                for (int i = 0; i < extra_drop.length(); i++) {
                    if (drop_list[item_index] == 0) {
                        item_index++;
                        continue;
                    }else if(special_drop.length()>0 && item_counter == item_type_bounds[1]-1 && item_counter<item_index){
                        break;
                    } else if(item_counter == item_type_bounds[1]){
                        break;
                    }
                    HashMap<String, Object> item = new HashMap<>();
                    String id = extra_drop.getString(i);
                    item.put("itemId", id);
                    item_quantity_bound = get_item_limitation_in_stage(stageID, id);
                    if (drop_list[item_index] >= item_quantity_bound[1]) {
                        item.put("quantity", item_quantity_bound[1]);
                        drop_list[item_index] -= item_quantity_bound[1];
                    } else if (drop_list[item_index] >= item_quantity_bound[0]) {
                        item.put("quantity", item_quantity_bound[0]);
                        drop_list[item_index] -= item_quantity_bound[0];
                    }
                    summary_drop.put(new JSONObject(item));
                    item_index++;
                    item_counter++;
                }
                System.out.println(Arrays.toString(drop_list));
                JSONObject params = new JSONObject();
                params.put("stageId",stageID);//Stage id
                params.put("drops",summary_drop); // drop array with matrix
                params.put("furnitureNum",furniture_num);//either 1 | 0
                params.put("source","penguin bulk report");//either 1 | 0
                System.out.println(params.toString());
                infos.add(params.toString());
//                int report_status = report(stageID,summary_drop,furniture_num,userID);
//                if (report_status>201){
//                    System.out.println("Error! Error code is:");
//                    System.out.println(report_status);
//                } else{
//                    System.out.println(report_status);
//                    System.out.println("Upload Successful");
//                }
            }// for ends
        }// iterate stage ends
        return infos;
    }//method ends

    public static JSONObject stage_info(String stage_id) {
        JSONObject json_result = null;
        int respond_code =0;
        try{
            URL url = new URL("https://penguin-stats.io/PenguinStats/api/stages/"+stage_id);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            httpUrlConn.connect();

            InputStream is = httpUrlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            is.close();

            String jsonText= sb.toString();
            json_result = new JSONObject(jsonText);

            respond_code = httpUrlConn.getResponseCode();

            httpUrlConn.disconnect();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return json_result;
    }

    private int sendDropRequest(String requestUrl, JSONObject params, String userID) {
        int respond_code = 0;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setRequestMethod("POST");
            httpUrlConn.setRequestProperty("Cookie","userID="+userID);
            httpUrlConn.setRequestProperty("Content-Type","application/json; utf-8");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            httpUrlConn.connect();

            OutputStreamWriter os = new OutputStreamWriter(httpUrlConn.getOutputStream());
            os.write(params.toString());
            os.flush();
            os.close();
            respond_code = httpUrlConn.getResponseCode();

            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return respond_code;
    }


}//BulkReport ends