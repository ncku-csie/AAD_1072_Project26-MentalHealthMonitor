package com.mhmc.mentalhealthmonitor.thirdPage.Question;

import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mhmc.mentalhealthmonitor.MYSQL.DBConnector;
import com.mhmc.mentalhealthmonitor.MYSQL.buffer;

import org.json.JSONArray;
import org.json.JSONObject;


public class SearchAccount extends ActivityCompat{
    //判斷帳號密碼是否存在
    public static String CheckAccount(String account, String password) {
        String DA = "";
        try {
            String result = DBConnector.executeQuery("http://140.116.82.102:8080/app/checkAccount.php?at=" + account + "&pw=" + password + "");
                /*
                SQL 結果有多筆資料時使用JSONArray
                只有一筆資料時直接建立JSONObject物件
                JSONObject jsonData = new JSONObject(result);
                */

            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            DA = jsonData.getString("Account");
            buffer.setaccount(DA);
            buffer.setpassword(password);
            buffer.setname(jsonData.getString("Name"));
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

        return DA;
    }
}
