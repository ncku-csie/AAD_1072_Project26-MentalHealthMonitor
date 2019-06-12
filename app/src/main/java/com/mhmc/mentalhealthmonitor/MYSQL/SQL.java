package com.mhmc.mentalhealthmonitor.MYSQL;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SQL {
    //判斷帳號密碼是否存在
    List<String> Content;
    List<String> time;
    List<String> icon_type;
    List<String> emotion;
    List<String> Name;
    List<String> emotionSystem;

    public HashMap<String, List<String>> SelectInfHistory(String account) {
        HashMap<String, List<String>> data = new HashMap();
        Content = new ArrayList<>();
        time = new ArrayList<>();
        icon_type = new ArrayList<>();
        emotion = new ArrayList<>();
        Name = new ArrayList<>();
        emotionSystem = new ArrayList<>();

        try {
            String result = DBConnector.executeQuery("http://140.116.82.102:8080/app/SelectInfHistory.php?at=" + account + "");


            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonData;
            String type;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonData = jsonArray.getJSONObject(i);
                type = jsonData.getString("type");
                icon_type.add(type);
                switch (type) {
                    case "0":
                        Content.add(jsonData.getString("write"));
                        break;
                    case "1":
                        Content.add(jsonData.getString("write"));
                        break;
                    case "2":
                        Content.add(jsonData.getString("icon"));//是select icon，但是因為icon就是圖表就好，故僅需add ""空值
                        break;
                    case "3":
                        Content.add(jsonData.getString("write"));
                        break;
                }

                time.add(jsonData.getString("Datetime"));/*
                emotion.add(
                        jsonData.getString("object_Anger") + "," +
                                jsonData.getString("object_Boredom") + "," +
                                jsonData.getString("object_Disgust") + "," +
                                jsonData.getString("object_Anxiety") + "," +
                                jsonData.getString("object_Happiness") + "," +
                                jsonData.getString("object_Sadness") + "," +
                                jsonData.getString("object_Surprised"));

                emotionSystem.add(
                        jsonData.getString("subject_Anger") + "," +
                                jsonData.getString("subject_Boredom") + "," +
                                jsonData.getString("subject_Disgust") + "," +
                                jsonData.getString("subject_Anxiety") + "," +
                                jsonData.getString("subject_Happiness") + "," +
                                jsonData.getString("subject_Sadness") + "," +
                                jsonData.getString("subject_Surprised"));*/

                if(jsonData.getString("object_Happiness").toString().equals("null") || jsonData.getString("object_Happiness").toString().equals(null)) {
                    emotion.add("0,0,0");
                }else{
                    emotion.add(
                            jsonData.getString("object_Happiness") + "," +
                                    jsonData.getString("object_Anger") + "," +
                                    jsonData.getString("object_Sadness"));
                }

                if(jsonData.getString("subject_Happiness").toString().equals("null") || jsonData.getString("subject_Happiness").toString().equals(null)){
                    emotionSystem.add("null,null,null");
                }else {
                    emotionSystem.add(
                            jsonData.getString("subject_Happiness") + "," +
                                    jsonData.getString("subject_Anger") + "," +
                                    jsonData.getString("subject_Sadness"));
                }
            }

            data.put("emotion", emotion);
            data.put("time", time);
            data.put("content", Content);
            data.put("icon_type", icon_type);
            data.put("emotionSystem", emotionSystem);

        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

        return data;
    }

    public static HashMap<String, List<String>> SelectSubject(String account, String filename) {
        HashMap<String, List<String>> data = new HashMap();
        List<String> emotion;
        emotion = new ArrayList<>();

        try {
            String result = DBConnector.executeQuery("http://140.116.82.102:8080/app/SelectInfsubject.php?at=" + account + "&fn="+filename+"");
            //System.out.println("http://140.116.82.102:8080/app/SelectInfsubject.php?at=" + account + "&fn="+filename+"");
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonData;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonData = jsonArray.getJSONObject(i);/*
                emotion.add(
                        jsonData.getString("subject_Anger") + "," +
                                jsonData.getString("subject_Boredom") + "," +
                                jsonData.getString("subject_Disgust") + "," +
                                jsonData.getString("subject_Anxiety") + "," +
                                jsonData.getString("subject_Happiness") + "," +
                                jsonData.getString("subject_Sadness") + "," +
                                jsonData.getString("subject_Surprised"));
*/
                if(jsonData.getString("subject_Happiness").toString().equals("null") || jsonData.getString("subject_Happiness").toString().equals(null)) {
                    emotion.add("null,null,null");
                }else{
                    emotion.add(
                            jsonData.getString("subject_Happiness") + "," +
                                    jsonData.getString("subject_Anger") + "," +
                                    jsonData.getString("subject_Sadness"));
                }
            }

            data.put("emotion", emotion);
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

        return data;
    }

    public void UpdateData(String account, String time, String content, String
            emotion, String type) {
        try {
            String[] t = emotion.split(",");
            String query = "http://140.116.82.102:8080/app/upload_data.php?Account=" + account + "&time=" + time.replace(" ","+") + "&content=" + content + "&type="
                    + type + "&object_Anger=" + t[0] + "&object_Boredom=" + t[1] + "&object_Disgust=" + t[2] + "&object_Anxiety=" + t[3] + "&object_Happiness=" + t[4] + "&object_Sadness=" + t[5] + "&object_Surprised=" + t[6] + "";
            String result = DBConnector.executeQuery(query);
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
    }

    public void InsertNewData_new(String account, String time, String content, String
            emotion, String type) {
        try {
            String[] t = emotion.split(",");

            String query = "http://140.116.82.102:8080/app/InsertNewData.php?Account=" + account + "&time=" + time.replace(" ","+") + "&content=" + content + "&type="
                    + type + "&object_Anger=" + t[0] + "&object_Boredom=" + t[1] + "&object_Disgust=" + t[2] + "&object_Anxiety=" + t[3] + "&object_Happiness=" + t[4] + "&object_Sadness=" + t[5] + "&object_Surprised=" + t[6] + "";
            String result = DBConnector.executeQuery(query);
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
    }

    public void InsertNewData_new(String account, String time, String content, String type) {
        try {
            String query = "http://140.116.82.102:8080/app/InsertNewData.php?Account=" + account + "&time=" + time + "&content=" + content + "&type=" +type+"";
            String result = DBConnector.executeQuery(query);
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }
    }
}
