package PenguinBulkReport;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Arrays;
import java.util.HashMap;
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
    public String[] activityItem = {"randomMaterial_1", "ap_supply_lt_010"};

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

    public int report(String stage_id, JSONObject params,String userID) {
        return sendDropRequest("https://penguin-stats.io/PenguinStats/api/report",params,userID);
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
        for (String s : activityItem) {
            if (itemID.equals(s)) {
                bounds[0] = 0;
                bounds[1] = 1;
                return bounds;
            }
        }
        for (int i =0;i<limitations.getKey().length();i++){
            if (itemID.equals(limitations.getKey().getJSONObject(i).getString("itemId"))){
                bounds[0] = limitations.getKey().getJSONObject(i).getJSONObject("bounds").getInt("lower");
                bounds[1] = limitations.getKey().getJSONObject(i).getJSONObject("bounds").getInt("upper");
            }
        }
        return bounds;
    }

    private boolean check_int_array_all_zeros(int[] array){
        boolean allNonZero = true;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                allNonZero = false;
                break;
            }
        }
        return allNonZero;
    }

    public ArrayList<JSONObject> stage_multiple_reports(String userID, HashMap<String,HashMap<String,Object>> results){
        ArrayList<JSONObject> infos = new ArrayList<>();
        for (String stageID : results.keySet()) {
            JSONObject info = stage_info(stageID);
            JSONArray normal_drop = info.getJSONArray("normalDrop");
            JSONArray special_drop = info.getJSONArray("specialDrop");
            JSONArray extra_drop = info.getJSONArray("extraDrop");

            JSONArray summary_drop = null;
            ArrayList<JSONObject> temp_objects = new ArrayList<>();

            boolean drop_extra_once = special_drop.length() >0;
            int[] drop_list = (int[])results.get(stageID).get("drop_list");
            int sum_of_amounts = Arrays.stream(drop_list).sum();
            int accumulator = 0;
            int furniture_total = (int)results.get(stageID).get("furniture_total");
            int furniture_num = 0;
            int num_times = Integer.parseInt(results.get(stageID).get("times").toString());
            int[] item_type_bounds = {get_limitations(stageID).getValue().getInt("lower"),get_limitations(stageID).getValue().getInt("upper")};
            ArrayList<HashMap<String, Object>> pre_definded_list = new ArrayList<>();
            int temp_item_count = 0;
            
            // 1 item for each time -> guarantee non null
            int item_in_pre_defined = 0;
            while(item_in_pre_defined < num_times){
                if (drop_list[temp_item_count]<=0){
                    temp_item_count ++;
                    continue;
                }
                HashMap<String, Object> temp_object = new HashMap<>();
                if (temp_item_count<normal_drop.length()) {
                    temp_object.put("itemId", normal_drop.get(temp_item_count));
                    temp_object.put("quantity", 1);
                    drop_list[temp_item_count]--;
                } else if (temp_item_count - normal_drop.length()<special_drop.length()) {
                    temp_object.put("itemId", special_drop.get(temp_item_count- normal_drop.length()));
                    temp_object.put("quantity", 1);
                    drop_list[temp_item_count]--;
                } else if (temp_item_count - normal_drop.length()-special_drop.length()< extra_drop.length()) {
                    temp_object.put("itemId", extra_drop.get(temp_item_count- normal_drop.length()-special_drop.length()));
                    temp_object.put("quantity", 1);
                    drop_list[temp_item_count]--;
                }
                if (!temp_object.isEmpty()) {
                    pre_definded_list.add(temp_object);
                    item_in_pre_defined++;
                    accumulator++;
                }
            }

            for(int t = 0; t < num_times; t++) {


                if (furniture_total >0) {
                    furniture_num = 1;
                    furniture_total --;
                } else{
                    furniture_num =0;
                }
                int item_counter =1;
                int[] item_quantity_bound=null;

                HashMap<String, Object> item = pre_definded_list.get(t);
                String current_item_id = item.get("itemId").toString();
                summary_drop = new JSONArray();
                for(int item_index = 0; item_index < drop_list.length; item_index++) {

                    if (check_int_array_all_zeros(drop_list)) break;
                    if (item_index < normal_drop.length() && drop_list[item_index] > 0) {
                        if (normal_drop.getString(item_index).equals(current_item_id)) {
                            item_quantity_bound = get_item_limitation_in_stage(stageID, current_item_id);
                            if (drop_list[item_index] >= item_quantity_bound[1]) {
                                item.put("quantity", item_quantity_bound[1]);
                                drop_list[item_index] -= item_quantity_bound[1] - 1;
                            } else if (drop_list[item_index] >= item_quantity_bound[0] && drop_list[item_index] > 0) {
                                item.put("quantity", drop_list[item_index] + 1);
                                drop_list[item_index] -= drop_list[item_index];
                            }
                        } else if (item_counter < item_type_bounds[1]) {
                            String id_temp = normal_drop.getString(item_index);
                            item_quantity_bound = get_item_limitation_in_stage(stageID, id_temp);
                            HashMap<String, Object> temp = new HashMap<>();
                            temp.put("itemId", id_temp);
                            if (drop_list[item_index] >= item_quantity_bound[1]) {
                                temp.put("quantity", item_quantity_bound[1]);
                                drop_list[item_index] -= item_quantity_bound[1];
                            } else if (drop_list[item_index] >= item_quantity_bound[0] && drop_list[item_index] > 0) {
                                temp.put("quantity", drop_list[item_index]);
                                drop_list[item_index] -= drop_list[item_index];
                            }
                            item_counter++;
                            summary_drop.put(new JSONObject(temp));
                            accumulator = accumulator+Integer.parseInt(temp.get("quantity").toString());
                        }// else ends
                        System.out.println("Normal drop"+ item_index);
                    }// normal drop ends
                    else if (item_index - normal_drop.length() < special_drop.length() && drop_list[item_index] > 0
                            && item_counter < item_type_bounds[1]
                    ) {
                        if (special_drop.getString(item_index - normal_drop.length()).equals(current_item_id)) {

                            item_quantity_bound = get_item_limitation_in_stage(stageID, current_item_id);
                            if (drop_list[item_index] >= item_quantity_bound[1]) {
                                item.put("quantity", item_quantity_bound[1]);
                                drop_list[item_index] -= item_quantity_bound[1] - 1;
                            } else if (drop_list[item_index] >= item_quantity_bound[0] && drop_list[item_index] > 0) {
                                item.put("quantity", drop_list[item_index] + 1);
                                drop_list[item_index] -= drop_list[item_index];
                            }
                        } else if (item_counter < item_type_bounds[1]) {
                            String id_temp = special_drop.getString(item_index - normal_drop.length());
                            item_quantity_bound = get_item_limitation_in_stage(stageID, id_temp);
                            for (int k = 0; k< item_quantity_bound.length; k++){
                                System.out.println(stageID);
                                System.out.println(special_drop.toString());
                                System.out.println("bounds: "+item_quantity_bound[k]);
                            }

                            HashMap<String, Object> temp = new HashMap<>();
                            temp.put("itemId", id_temp);
                            if (drop_list[item_index] >= item_quantity_bound[1]) {
                                temp.put("quantity", item_quantity_bound[1]);
                                drop_list[item_index] -= item_quantity_bound[1];
                            } else if (drop_list[item_index] >= item_quantity_bound[0] && drop_list[item_index] > 0) {
                                temp.put("quantity", drop_list[item_index]);
                                drop_list[item_index] -= drop_list[item_index];
                            }
                            summary_drop.put(new JSONObject(temp));
                            accumulator = accumulator + Integer.parseInt(temp.get("quantity").toString());
                            item_counter++;
                        }// else ends
                        System.out.println("Special drop "+ item_index);
                    } // special drop ends
                    else if (item_index - normal_drop.length() - special_drop.length() < extra_drop.length()
                            && drop_list[item_index] > 0
                            && item_counter < item_type_bounds[1]
                    ) {
                        System.out.println("Extra drop "+ item_index);
                        if (extra_drop.getString(item_index - normal_drop.length() - special_drop.length()).equals(current_item_id)) {
                            item_quantity_bound = get_item_limitation_in_stage(stageID, current_item_id);
                            if (drop_list[item_index] >= item_quantity_bound[1]) {
                                item.put("quantity", item_quantity_bound[1]);
                                drop_list[item_index] -= item_quantity_bound[1] - 1;
                            } else if (drop_list[item_index] >= item_quantity_bound[0] && drop_list[item_index] > 0) {
                                item.put("quantity", drop_list[item_index] + 1);
                                drop_list[item_index] -= drop_list[item_index];
                            }
                        } else if (item_counter < (drop_extra_once? item_type_bounds[1]-1 : item_type_bounds[1])) {
                            String id_temp = extra_drop.getString(item_index - normal_drop.length() - special_drop.length());
                            item_quantity_bound = get_item_limitation_in_stage(stageID, id_temp);
                            HashMap<String, Object> temp = new HashMap<>();
                            temp.put("itemId", id_temp);
                            if (drop_list[item_index] >= item_quantity_bound[1]) {
                                temp.put("quantity", item_quantity_bound[1]);
                                drop_list[item_index] -= item_quantity_bound[1];
                            } else if (drop_list[item_index] >= item_quantity_bound[0] && drop_list[item_index] > 0) {
                                temp.put("quantity", drop_list[item_index]);
                                drop_list[item_index] -= drop_list[item_index];
                            }
                            summary_drop.put(new JSONObject(temp));
                            accumulator = accumulator+Integer.parseInt(temp.get("quantity").toString());
                            item_counter++;
                        }// else ends
                    } // extra ends
                }// for loop ends
                summary_drop.put(item);
                accumulator = accumulator+Integer.parseInt(item.get("quantity").toString());
                JSONObject params = new JSONObject();
                params.put("stageId",stageID);//Stage id
                params.put("drops",summary_drop); // drop array with matrix
                params.put("furnitureNum",furniture_num);//either 1 | 0
                params.put("source","penguin bulk report");//either 1 | 0
                temp_objects.add(params);
            }// for ends
            System.out.println(sum_of_amounts);
            System.out.println(accumulator);
            System.out.println(sum_of_amounts == accumulator);
            System.out.println(check_int_array_all_zeros(drop_list));
            System.out.println(Arrays.toString(drop_list));
            //if (sum_of_amounts == accumulator &&
            if (check_int_array_all_zeros(drop_list)){
                for (JSONObject temp_object : temp_objects) {
                    infos.add(temp_object);
                    int status = report(stageID, temp_object, userID);
                }
            } else {
                ArrayList<JSONObject> message = new ArrayList<>();
                JSONObject error = new JSONObject();
                error.put("Error","The number is not correct");
                message.add(error);
                return message;
            }
        }// iterate stage ends
        return infos;
    }//method ends

//    public ArrayList<String> validate(ArrayList<JSONObject> jsonStrings, int[] original_drop_list){
//        int[] res = new int[original_drop_list.length];
//        for (JSONObject item : jsonStrings){
//            JSONArray drop_list = item.getJSONArray("drops");
//            for (int i = 0; i<drop_list.length();i++){
//                JSONObject temp_item = drop_list.getJSONObject(i);
//
//
//            }
//        }
//
//    }

    public ArrayList<String> stage_multiple_reports_old(String userID, HashMap<String,HashMap<String,Object>> results){
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
//                boolean all_zero = true;
//                for (int num : drop_list){
//                    if (num != 0){
//                        all_zero = false;
//                        break;
//                    }
//                }
//                if (all_zero){
//                    break;
//                }
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
                boolean extra_once = false;
                System.out.println("item_counter:");
                System.out.println(item_counter);
                for (int i = 0; i < extra_drop.length(); i++) {
                    if (drop_list[item_index] == 0 || extra_once) {
                        item_index++;
                        continue;
                    }else if(special_drop.length()>0 && item_counter == item_type_bounds[1]-1 && item_counter<item_index){
                        break;
                    } else if(item_counter == item_type_bounds[1]){
                        break;
                    } else if (special_drop.length()>0 && item_counter == item_type_bounds[1]-1 && item_counter<item_index){
                        break;
                    } else if (special_drop.length()>0 && item_counter == 0) {
                        extra_once = true;
                    }
                    HashMap<String, Object> item = new HashMap<>();
                    String id = extra_drop.getString(i);
                    int previous_item_index = -1;
                    int previous_item_amount = -1;
                    for (int j = 0; j < summary_drop.length(); j++) {
                        JSONObject previous_item = summary_drop.getJSONObject(j);
                        String itemId = previous_item.optString("itemId");
                        int quantity = previous_item.optInt("quantity");
                        if (itemId.equals(id)){
                            previous_item_index = j;
                            previous_item_amount = quantity;
                            break;
                        }
                    }
                    if (previous_item_index <0) {
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
                    } else {
                        item_quantity_bound = get_item_limitation_in_stage(stageID, id);
                        JSONObject previous = summary_drop.getJSONObject(previous_item_index);
                        int new_amount = previous_item_amount+ (item_quantity_bound[1] - drop_list[item_index]);
                        if (new_amount <= item_quantity_bound[1]) {
                            previous.put(id, new_amount);
                        }
                    }

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