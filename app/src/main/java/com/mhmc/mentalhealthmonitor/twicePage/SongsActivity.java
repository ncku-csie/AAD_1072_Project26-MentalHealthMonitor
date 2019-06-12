package com.mhmc.mentalhealthmonitor.twicePage;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.mhmc.mentalhealthmonitor.MYSQL.DBConnector;
import com.mhmc.mentalhealthmonitor.MYSQL.buffer;
import com.mhmc.mentalhealthmonitor.R;
import com.mhmc.mentalhealthmonitor.homepage;
import com.mhmc.mentalhealthmonitor.twicePage.internal.Entry;
import com.mhmc.mentalhealthmonitor.twicePage.internal.EntryData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.ButterKnife;

//這是setting哦
public class SongsActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_layout);
        ButterKnife.bind(this);

        //設定隱藏標題
        getSupportActionBar().hide();

        try {
            get();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void get() throws JSONException {
        buffer.typezero();

        //取情緒資料
        ArrayList<String[]> Emotions = getInf(1);
        chart_emotoin(Emotions);
        buffer.typeadd();

        //取打電話秒數資料
        ArrayList<String[]> phoneSeconds = getInf(2);
        chart_phoneSeconds(phoneSeconds);
        buffer.typeadd();

        //取打電話次數資料
        ArrayList<String[]> phoneTimes = getInf(3);
        chart_phoneTimes(phoneTimes);
        buffer.typeadd();

        //取GPS資料
        ArrayList<String[]> GPS = getInf(4);
        /*
        if(GPS==null){
            for(int i = 0;i < GPS.size();i++){
                String[] setd = new String[2];
                setd[0] = " ";
                setd[1] = "0";
                GPS.add(setd);
            }
        }else if(GPS.size()<7){
            ArrayList<String[]> new_GPS = new ArrayList<String[]>();

            int max = 8-GPS.size();
            for(int i = 0; i < max;i++){
                String[] setd = new String[2];
                setd[0] = "a";
                setd[1] = "0";
                new_GPS.add(setd);
            }
            for(int i=0;i<GPS.size();i++){
                String[] setd = GPS.get(i);
                new_GPS.add(setd);
            }

            GPS = new_GPS;
            buffer.setGPSData(GPS);
        }*/
        chart_GPS(GPS);

        ArrayList<ArrayList<String[]>> arrayList = new ArrayList<>();
        arrayList.add(Emotions);
        arrayList.add(phoneSeconds);
        arrayList.add(phoneTimes);
        arrayList.add(GPS);
        buffer.setArraList(arrayList);
    }

    private void chart_emotoin(ArrayList<String[]> Emotions) {
        final EntryData data = new EntryData();

        int i = 0;
        //value = -194 + (x * 102)
        for (String[] dataarray : Emotions) {
            if (dataarray[1] == null || dataarray[1].equals("null")) {
            } else {
                float mult = -194 + (Float.valueOf(dataarray[1]) * 102);

                float val = (float) (1 * 40) + mult;

                float high = (float) (1 * 9) + 8f;
                float low = (float) (1 * 9) + 8f;

                float open = (float) (0 * 6) + 1f;
                float close = (float) (0 * 6) + 1f;

                boolean even = i % 2 == 0;

                data.addEntry(new Entry(
                        val + high,
                        val - low,
                        even ? val + open : val - open,
                        even ? val - close : val + close,
                        (int) (Math.random() * 111),
                        ""));
                i++;
            }
        }

        final KLineChart chart = (KLineChart) findViewById(R.id.chartEmotion);
        chart.setData(data, Emotions);
    }

    private void chart_phoneSeconds(ArrayList<String[]> phoneSeconds) {
        final EntryData data = new EntryData();

        int i = 0;
        //value = -194 + (x * 102)
        for (String[] dataarray : phoneSeconds) {
            float mult = -194 + (Float.valueOf(dataarray[1]) * 102);
            float val = (float) (1 * 40) + mult;

            float high = (float) (1 * 9) + 8f;
            float low = (float) (1 * 9) + 8f;

            float open = (float) (0 * 6) + 1f;
            float close = (float) (0 * 6) + 1f;

            boolean even = i % 2 == 0;

            data.addEntry(new Entry(
                    val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
                    (int) (Math.random() * 111),
                    ""));
            i++;
        }

        final KLineChart chart = (KLineChart) findViewById(R.id.chartPhoneSeconds);
        chart.setData(data, phoneSeconds);
    }

    private void chart_phoneTimes(ArrayList<String[]> phoneTimes) {
        final EntryData data = new EntryData();

        int i = 0;
        //value = -194 + (x * 102)
        for (String[] dataarray : phoneTimes) {
            float mult = -194 + (Float.valueOf(dataarray[1]) * 102);

            float val = (float) (1 * 40) + mult;

            float high = (float) (1 * 9) + 8f;
            float low = (float) (1 * 9) + 8f;

            float open = (float) (0 * 6) + 1f;
            float close = (float) (0 * 6) + 1f;

            boolean even = i % 2 == 0;

            data.addEntry(new Entry(
                    val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
                    (int) (Math.random() * 111),
                    ""));
            i++;
        }

        final KLineChart chart = (KLineChart) findViewById(R.id.chartPhoneTimes);
        chart.setData(data, phoneTimes);
    }

    private void chart_GPS(ArrayList<String[]> GPS) {
        final EntryData data = new EntryData();

        int i = 0;
        //value = -194 + (x * 102)
        for (String[] dataarray : GPS) {
            float mult = -194 + (Float.valueOf(dataarray[1]) * 102);

            float val = (float) (1 * 40) + mult;

            float high = (float) (1 * 9) + 8f;
            float low = (float) (1 * 9) + 8f;

            float open = (float) (0 * 6) + 1f;
            float close = (float) (0 * 6) + 1f;

            boolean even = i % 2 == 0;

            data.addEntry(new Entry(
                    val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
                    (int) (Math.random() * 111),
                    ""));
            i++;
        }

        final KLineChart chart = (KLineChart) findViewById(R.id.chartGPS);
        chart.setData(data, GPS);
    }

    //按下返回鍵回到homepage畫面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
            // Finish the registration screen and return to the Login activity
            Intent intent = new Intent(getApplicationContext(), homepage.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        return super.onKeyDown(keyCode, event);
    }

    private ArrayList<String[]> getE() throws JSONException {
        String result = DBConnector.executeQuery("http://140.116.82.102:8080/app/SelectInf.php?at=" + buffer.getAccount() + "");

        ArrayList<String[]> d = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(result);
        JSONObject jsonData;

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonData = jsonArray.getJSONObject(i);
            String[] dataarray = new String[2];
            dataarray[0] = jsonData.getString("Datetime");
            dataarray[1] = jsonData.getString("test_Happiness");
            d.add(dataarray);
            String[] test = d.get(i);
        }
        return d;
    }

    /*
        type
        1:表情符號
        2:打電話時間
        3:打電話次數
        4:GPS走路公尺
     */
    private ArrayList<String[]> getInf(int type) {
        String result = "";

        switch (type) {
            case 1:
                ArrayList<String[]> data = new ArrayList<String[]>();
                result = DBConnector.executeQuery("http://140.116.82.102:8080/app/SelectInf.php?at=" + buffer.getAccount() + "");
                if (result.contains("<b>Notice</b>:")) {
                    //直接添加七筆，以免沒資料出錯
                    for (int i = 0; i < 5; i++) {
                        String[] dataarray = new String[2];
                        dataarray[0] = "`-`-` 00:00:00";
                        dataarray[1] = "0";
                        data.add(dataarray);
                    }

                    buffer.Renderer_Max(Integer.valueOf(String.valueOf(0)) + 1);
                    System.out.println(data.size());
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonData;

                        //直接添加七筆，以免沒資料出錯
                        for (int i = 0; i < 5; i++) {
                            String[] dataarray = new String[2];
                            dataarray[0] = "`-`-` 00:00:00";
                            dataarray[1] = "0";
                            data.add(dataarray);
                        }

                        //取數據
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonData = jsonArray.getJSONObject(i);
                            String[] dataarray = new String[2];
                            dataarray[0] = jsonData.getString("Datetime");
                            dataarray[1] = jsonData.getString("subject_Happiness");
                            data.add(dataarray);
                        }

                        Collections.reverse(data);
                        buffer.Renderer_Max(4);
                        System.out.println(data.size());
                    } catch (Exception e) {
                        Log.e("log_tag", e.toString());
                    }
                }
                return data;
            case 2:
                /*
                取打電話秒數 second
                每日總和
                */
                ArrayList<String[]> data2 = new ArrayList<String[]>();
                ArrayList<String> date2 = new ArrayList<String>();
                String sdate;
                HashMap<String, Integer> sdateList = new HashMap<String, Integer>();
                result = DBConnector.executeQuery("http://140.116.82.102:8080/app/Selectphone.php?at=" + buffer.getAccount() + "");
                if (result.contains("<b>Notice</b>:")) {
                    //直接添加七筆，以免沒資料出錯
                    for (int i = 0; i < 5; i++) {
                        String[] dataarray2 = new String[2];
                        dataarray2[0] = "`-`-` 00:00:00";
                        dataarray2[1] = "0";
                        data2.add(dataarray2);
                    }

                    buffer.Renderer_Max(Integer.valueOf(String.valueOf(0)) + 1);
                    System.out.println(data2.size());
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonData;

                        //取數據
                        int max = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonData = jsonArray.getJSONObject(i);
                            sdate = jsonData.getString("date").toString().split(" ")[0];

                            if (sdateList.containsKey(sdate)) {
                                String svalue = jsonData.getString("second").toString();
                                int value = sdateList.get(sdate);
                                value += Integer.valueOf(svalue);
                                sdateList.put(sdate, value);

                                if (Integer.valueOf(svalue) > max)
                                    max = Integer.valueOf(svalue);
                            } else {
                                String svalue = jsonData.getString("second").toString();
                                sdateList.put(sdate, Integer.valueOf(svalue));

                                if (Integer.valueOf(svalue) > max)
                                    max = Integer.valueOf(svalue);
                            }
                            if (!date2.contains(sdate))
                                date2.add(sdate);
                        }

                        //list前後顛倒
                        for (int i = date2.size() - 1; i >= 0; i--) {
                            String key = date2.get(i);
                            float value = sdateList.get(key);
                            String[] dataarray2 = new String[2];
                            dataarray2[0] = key;
                            dataarray2[1] = String.valueOf(value);
                            data2.add(dataarray2);
                        }

                        //直接添加七筆，以免沒資料出錯
                        for (int i = 0; i < 5; i++) {
                            String[] dataarray2 = new String[2];
                            dataarray2[0] = "`-`-` 00:00:00";
                            dataarray2[1] = "0";
                            data2.add(dataarray2);
                        }

                        buffer.Renderer_Max(Integer.valueOf(String.valueOf(max)) + 1);
                        System.out.println(data2.size());
                    } catch (Exception e) {
                        Log.e("log_tag", e.toString());
                    }
                }
                return data2;

            case 3:
                /*
                計算每日打電話 次數
                */
                ArrayList<String[]> data3 = new ArrayList<String[]>();
                ArrayList<String> date3 = new ArrayList<String>();
                String ssdate;
                HashMap<String, Integer> ssdateList = new HashMap<String, Integer>();
                result = DBConnector.executeQuery("http://140.116.82.102:8080/app/Selectphone.php?at=" + buffer.getAccount() + "");
                if (result.contains("<b>Notice</b>:")) {
                    //直接添加七筆，以免沒資料出錯
                    for (int i = 0; i < 5; i++) {
                        String[] dataarray3 = new String[2];
                        dataarray3[0] = "`-`-` 00:00:00";
                        dataarray3[1] = "0";
                        data3.add(dataarray3);
                    }

                    buffer.Renderer_Max(Integer.valueOf(String.valueOf(0)) + 1);
                    System.out.println(data3.size());
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonData;

                        //取數據
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonData = jsonArray.getJSONObject(i);
                            ssdate = jsonData.getString("date").split(" ")[0];

                            if (ssdateList.containsKey(ssdate)) {
                                int value = ssdateList.get(ssdate);
                                value += 1;
                                ssdateList.put(ssdate, value);
                            } else {
                                ssdateList.put(ssdate, 1);
                            }
                            if (!date3.contains(ssdate))
                                date3.add(ssdate);
                        }

                        //list前後顛倒
                        Integer max = 0;
                        for (int i = date3.size() - 1; i >= 0; i--) {
                            String key = date3.get(i);
                            Integer value = ssdateList.get(key);
                            String[] dataarray3 = new String[2];
                            dataarray3[0] = key;
                            dataarray3[1] = String.valueOf(value);
                            data3.add(dataarray3);

                            if (value > max)
                                max = value;
                        }

                        //直接添加七筆，以免沒資料出錯
                        for (int i = 0; i < 5; i++) {
                            String[] dataarray3 = new String[2];
                            dataarray3[0] = "`-`-` 00:00:00";
                            dataarray3[1] = "0";
                            data3.add(dataarray3);
                        }

                        buffer.Renderer_Max(Integer.valueOf(String.valueOf(max)) + 1);
                        System.out.println(data3.size());
                    } catch (Exception e) {
                        Log.e("log_tag", e.toString());
                    }
                }
                return data3;

            case 4:
                /*
                記錄每日走路公尺
                */
                ArrayList<String[]> data4 = new ArrayList<String[]>();
                ArrayList<String> date4 = new ArrayList<String>();
                String sGPS;
                HashMap<String, Float> sGPSList = new HashMap<String, Float>();
                result = DBConnector.executeQuery("http://140.116.82.102:8080/app/Selectgps.php?at=" + buffer.getAccount() + "");
                if (result.contains("<b>Notice</b>:")) {
                    //直接添加七筆，以免沒資料出錯
                    for (int i = 0; i < 5; i++) {
                        String[] dataarray4 = new String[2];
                        dataarray4[0] = "`-`-` 00:00:00";
                        dataarray4[1] = "0";
                        data4.add(dataarray4);
                    }

                    buffer.Renderer_Max(Integer.valueOf(String.valueOf(0)) + 1);
                    System.out.println(data4.size());
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        JSONObject jsonData;

                        //取數據
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonData = jsonArray.getJSONObject(i);
                            sGPS = jsonData.getString("formatetime").split(" ")[0];

                            if (sGPSList.containsKey(sGPS)) {
                                String svalue = jsonData.getString("distance").toString();
                                float value = sGPSList.get(sGPS);
                                value = value + Float.valueOf(svalue);
                                sGPSList.put(sGPS, value);
                            } else {
                                String svalue = jsonData.getString("distance").toString();
                                sGPSList.put(sGPS, Float.valueOf(svalue));
                            }
                            if (!date4.contains(sGPS))
                                date4.add(sGPS);
                        }

                        //list前後顛倒
                        float max = 0;
                        for (int i = date4.size() - 1; i >= 0; i--) {
                            String key = date4.get(i);
                            float value = sGPSList.get(key);
                            String[] dataarray4 = new String[2];
                            dataarray4[0] = key;
                            dataarray4[1] = String.valueOf(value);
                            data4.add(dataarray4);

                            if (value > max)
                                max = value;
                        }

                        //直接添加七筆，以免沒資料出錯
                        for (int i = 0; i < 5; i++) {
                            String[] dataarray4 = new String[2];
                            dataarray4[0] = "`-`-` 00:00:00";
                            dataarray4[1] = "0";
                            data4.add(dataarray4);
                        }

                        buffer.Renderer_Max(Integer.valueOf(String.valueOf(max)) + 1);
                        System.out.println(data4.size());
                    } catch (Exception e) {
                        Log.e("log_tag4", e.toString());
                    }
                }
                return data4;

            default:
                return null;
        }
    }
}