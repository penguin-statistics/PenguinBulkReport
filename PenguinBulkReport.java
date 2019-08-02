import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.charset.Charset;

import org.json.JSONObject;

/*
    Penguin-stats bulk report
         - report on specific stages in multiple times
 */

public class PenguinBulkReport {

    public static int login(String userID){
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

    public int report(String stage_id, JSONObject drop_info, int furniture_num,String userID) {

        String requestUrl = "https://penguin-stats.io/PenguinStats/api/report";
        // Info send to penguin-stats.io
        HashMap params = new HashMap();
        params.put("stage_id",stage_id);//Stage id
        params.put("drops",drop_info); // drop array with matrix
        params.put("furnitureNum",furniture_num);//either 1 | 0

        int respond_code = sendDropRequest(requestUrl,params,userID);

        return respond_code;
    }

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

    private int sendDropRequest(String requestUrl, HashMap params, String userID) {
        int respond_code = 0;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setRequestMethod("POST");
            httpUrlConn.setRequestProperty("Cookie","userID="+userID);
            httpUrlConn.setRequestProperty("Content-Type","application/json; utf-8");
            httpUrlConn.setRequestProperty("user-agent", "114514");
            JSONObject json_string = new JSONObject(params);
            httpUrlConn.connect();

            OutputStream os = httpUrlConn.getOutputStream();
            os.write(json_string.toString().getBytes(),0,json_string.toString().getBytes().length);
            os.flush();
            os.close();
            respond_code = httpUrlConn.getResponseCode();

            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return respond_code;
    }

    public static void main(String[] args){
//        System.out.println("Enter your user ID");
//        Scanner sc = new Scanner(System.in);
//        String userId = sc.nextLine();
//        int login_status = login(userId);
//        System.out.println("Return code is");
//        System.out.println(login_status);
        JSONObject get_stage_status = stage_info("main_04-07");
        System.out.println(get_stage_status.toString());



    }

}//BulkReport ends