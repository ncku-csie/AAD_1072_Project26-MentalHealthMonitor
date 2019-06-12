package com.mhmc.mentalhealthmonitor.GPS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mhmc.mentalhealthmonitor.CheckService.checkservice;
import com.mhmc.mentalhealthmonitor.Phone.Phone_listener;


public class GPSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "手機已重啟完成", Toast.LENGTH_LONG).show();
            boolean isRunning;

            for(int i=1;i<=3;i++){
                Toast.makeText(context, "第"+i+"次嘗試啟動GPS服務", Toast.LENGTH_LONG).show();
                Intent serviceIntent = new Intent(context, GPS.class);
                context.startService(serviceIntent);

                isRunning = checkservice.isServiceRunning(context, "com.mhmc.mentalhealthmonitor.GPS.GPS");

                if (isRunning) {
                    Toast.makeText(context, "第"+i+"次GPS服務已啟動", Toast.LENGTH_LONG).show();
                    break;
                } else {
                    Toast.makeText(context, "第"+i+"次GPS服務啟動失敗", Toast.LENGTH_LONG).show();
                }
            }

            for(int i=1;i<=3;i++){
                Toast.makeText(context, "第"+i+"次嘗試啟動電話服務", Toast.LENGTH_LONG).show();
                Intent serviceIntent = new Intent(context, Phone_listener.class);
                context.startService(serviceIntent);

                isRunning = checkservice.isServiceRunning(context, "com.mhmc.mentalhealthmonitor.Phone.Phone_listener");

                if (isRunning) {
                    Toast.makeText(context, "第"+i+"次電話服務已啟動", Toast.LENGTH_LONG).show();
                    break;
                } else {
                    Toast.makeText(context, "第"+i+"次電話服務啟動失敗", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
