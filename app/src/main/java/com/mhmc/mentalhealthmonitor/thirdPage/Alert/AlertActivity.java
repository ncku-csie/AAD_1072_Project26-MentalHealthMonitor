package com.mhmc.mentalhealthmonitor.thirdPage.Alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mhmc.mentalhealthmonitor.R;
import com.mhmc.mentalhealthmonitor.thirdPage.Question.question;

public class AlertActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
    }

    //關閉鬧鐘_進入問題回答
    public void onAlarmTurnOnClick() {
        finish();//關閉窗口
        Intent intent = new Intent(AlertActivity.this, question.class);
        startActivity(intent);
    }

    //關閉鬧鐘
    public void onAlarmTurnOffClick(View v) {
        finish();//關閉窗口
    }

    //五分鐘後提醒
    public void onAlarmSetAsideClick(View v) {
        alarm();
        finish();
    }

    private void alarm() {
        //獲取系統的鬧鐘服務
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //觸發鬧鐘的時間（毫秒）預設五分鐘
        long triggerTime = System.currentTimeMillis() + 300000;
        Intent intent = new Intent(this, AlertActivity.class);
        PendingIntent op = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //只會執行一次鬧鐘
        am.set(AlarmManager.RTC, triggerTime, op);
        //指定時間重複執行鬧鐘
        //am.setRepeating(AlarmManager.RTC,triggerTime,2000,op);
    }
}