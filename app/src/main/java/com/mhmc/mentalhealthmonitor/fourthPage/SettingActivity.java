package com.mhmc.mentalhealthmonitor.fourthPage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.mhmc.mentalhealthmonitor.LoginActivity;
import com.mhmc.mentalhealthmonitor.MYSQL.buffer;
import com.mhmc.mentalhealthmonitor.R;
import com.mhmc.mentalhealthmonitor.homepage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.ButterKnife;



public class SettingActivity extends AppCompatActivity {
    Button logoutbutton;
    private Switch question_voice_switch;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        ButterKnife.bind(this);
        read();

        question_voice_switch = findViewById(R.id.question_voice_switch);
        if(buffer.getAlert_question_voice()!=null){
            if(buffer.getAlert_question_voice().equals("ON"))
                question_voice_switch.setChecked(true);
            else
                question_voice_switch.setChecked(false);
        }

        question_voice_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    save_alert_question_voice("ON");
                    buffer.setAlert_question_voice("ON");
                    Toast.makeText(SettingActivity.this, "聲音ON", Toast.LENGTH_SHORT).show();
                }else{
                    save_alert_question_voice("OFF");
                    buffer.setAlert_question_voice("OFF");
                    Toast.makeText(SettingActivity.this, "聲音OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutbutton = (Button) findViewById(R.id.logout);
        logoutbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        /**問卷**/
        logoutbutton = (Button) findViewById(R.id.scale1);//DASS21
        logoutbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri=Uri.parse("https://goo.gl/forms/pCvfYzdEsMA8iF0a2");
                        Intent i=new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(i);
                    }
                }
        );

        logoutbutton = (Button) findViewById(R.id.scale2);//Altman
        logoutbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri=Uri.parse("https://goo.gl/forms/VNMWPoKLgOjVTYND3");
                        Intent i=new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(i);
                    }
                }
        );
        /*******************************************************************************************/
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

    /***********************************儲存Account************************************/
    private void save(){
        String path = Environment.getExternalStorageDirectory().getPath() + "/RDataR/";
        try{
            FileWriter fw = new FileWriter(path+"user.txt", false);//false: 覆蓋
            BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
            bw.write("帳號:");
            bw.newLine();
            bw.write("密碼:");
            bw.newLine();
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /***********************************儲存資料檔*************************************/
    private void save_alert_question_voice(String offon){
        String path = Environment.getExternalStorageDirectory().getPath() + "/RDataR/";
        try{
            FileWriter fw = new FileWriter(path+"user.txt", true);//true: 要繼續寫
            BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
            bw.write("鬧鐘聲音:"+offon);
            bw.newLine();
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /***********************************讀取資料檔**************************************/
    //密碼、鬧鐘問題是否需要聲音一併讀取
    private void read() {
        String path = "/storage/emulated/0/RDataR/";
        String myData = "";
        try {
            FileInputStream fis = new FileInputStream(path + "user.txt");
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains("鬧鐘聲音:") && strLine.length() > 6) {
                    myData = strLine;
                    String offon = myData.replace("鬧鐘聲音:", "");
                    buffer.setAlert_question_voice(offon);
                }
            }
            in.close();
        } catch (Exception e) {
        }
    }
}