package com.mhmc.mentalhealthmonitor.MYSQL;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DBConnector {
    public static String executeQuery(String query_string) {
        String result = "";
        HttpURLConnection urlConnection = null;
        InputStream is = null;

        try {
            URL url = new URL(query_string);//php的位置
            urlConnection = (HttpURLConnection) url.openConnection();//對資料庫打開連結
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();//接通資料庫
            is = urlConnection.getInputStream();//從database 開啟 stream

            InputStream inputStream;

            int status = urlConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();
            System.out.println(inputStream);

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            is.close();
            result = builder.toString();
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
            result = e.toString();
        }

        return result;
    }
}