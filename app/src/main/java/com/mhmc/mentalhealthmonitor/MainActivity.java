package com.mhmc.mentalhealthmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.PHONE;


public class MainActivity extends AppCompatActivity {

    //判斷網路
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //防止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int permission = ActivityCompat.checkSelfPermission(this, RECORD_AUDIO);
        int permission1 = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(this, READ_CALL_LOG);
        int permission4 = ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE);
        int permission5 = ActivityCompat.checkSelfPermission(this, WRITE_CALL_LOG);
        int permission6 = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int permission8 = ActivityCompat.checkSelfPermission(this, CAMERA);
        int permission9 = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        int permission10 = ActivityCompat.checkSelfPermission(this, ACCESS_WIFI_STATE);
        int permission11 = ActivityCompat.checkSelfPermission(this, PHONE);
        int permission12 = ActivityCompat.checkSelfPermission(this, WAKE_LOCK);
        int permission13 = ActivityCompat.checkSelfPermission(this, POWER_SERVICE);
        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED
                || permission3 != PackageManager.PERMISSION_GRANTED || permission4 != PackageManager.PERMISSION_GRANTED || permission5 != PackageManager.PERMISSION_GRANTED ||
                permission6 != PackageManager.PERMISSION_GRANTED || permission8 != PackageManager.PERMISSION_GRANTED || permission9 != PackageManager.PERMISSION_GRANTED
                || permission10 != PackageManager.PERMISSION_GRANTED || permission11 != PackageManager.PERMISSION_GRANTED
                || permission12 != PackageManager.PERMISSION_GRANTED || permission13 != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            ACCESS_FINE_LOCATION,
                            CAMERA,
                            RECORD_AUDIO,
                            READ_CALL_LOG,
                            PHONE,
                            ACCESS_FINE_LOCATION},
                    0
            );
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        //判斷網路
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");    //廣播接收器想要監聽什幺廣播，就在這裏添加相應的action
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);              //調用resigerReceiver()方法進行註冊
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //判斷是否有網路
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    //返回登入畫面
    public void gohome(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //自動偵測網路
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //          Toast.makeText(context,"network changes",Toast.LENGTH_SHORT).show();
            ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);    //得到系統服務類
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                //Toast.makeText(context, "網路正常連接", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "           網路已斷線\n將'無法登入'或'自動登出'", Toast.LENGTH_SHORT).show();
                gohome();
            }
        }
    }
}
