package com.mhmc.mentalhealthmonitor.GPS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.mhmc.mentalhealthmonitor.MYSQL.DBConnector;
import com.mhmc.mentalhealthmonitor.Phone.Phone_listener;
import com.mhmc.mentalhealthmonitor.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static android.app.Notification.DEFAULT_VIBRATE;


public class GPS extends Service {
    private LocationManager locMgr;
    private MyLocationListener locMgrListener;
    private long starttime, endtime;
    private float speed, distance;
    private double startx, starty, endx, endy;
    private Boolean firstGPStime = true, AllBegin = true;
    private int zerotime = 0;

    //螢幕休眠，service不休眠
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock = null;
    /****/
    private static final String CHANNEL_ID = "1250024";
    private static final String TAG = Phone_listener.class.getSimpleName();
    /****/

    @Override
    public void onCreate() {
        super.onCreate();
        AllRoot = Environment.getExternalStorageDirectory().getPath() + "/RDataR";
        read();

        locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        locMgrListener = new MyLocationListener();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**t創建通知細節**/
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        } else {
            notificationBuilder =  new NotificationCompat.Builder(this);
        }
        notificationBuilder
//                .setContentTitle(notification.getTitle())
                .setContentText(String.format("GPS系統啟動"))
                // .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setLargeIcon(icon)
                .setColor(Color.RED)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationBuilder.setDefaults(DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.YELLOW, 1000, 300);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
        /**創建通知視窗**/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,TAG,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(getApplicationContext(),CHANNEL_ID).build();
            startForeground(2, notification);
        }
        else {
            startForeground(startId, new Notification());
        }
        /****/
        if (null == intent) {
            return 0;
        } else {
            boolean mode = intent.getBooleanExtra("mode", true);
            if (mode) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return 0;
                }
                //取得GPS服務，並設置每秒取得資料及最小距離0米
                locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locMgrListener);
            } else {
                locMgr.removeUpdates(locMgrListener);
            }
        }
        //创建PowerManager对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();

        return super.onStartCommand(intent, flags, startId);
        //return Service.START_STICKY;
    }

    public void onDestroy() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        Toast.makeText(this, "GPS掛掉，即將重啟", Toast.LENGTH_LONG).show();
        stopForeground(true);
        Intent localIntent = new Intent();
        localIntent.setClass(this, GPS.class); //銷毀時重新啟動Service
        this.startService(localIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void show(){
        Toast.makeText(this, ""+startx+"\n"+starty, Toast.LENGTH_LONG).show();
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (!location.getProvider().equals("network")) {
                speed = location.getSpeed();

                //第一次進入，設置初始座標、時間
                if(AllBegin){
                    startx = location.getLatitude();
                    starty = location.getLongitude();
                    starttime = Long.valueOf(location.getTime());
                    AllBegin = false;
                }

                //速度為0，且為本輪第一次偵測，設置每輪初始座標、時間
                if (speed == 0 && firstGPStime) {
                    startx = location.getLatitude();
                    starty = location.getLongitude();
                    starttime = Long.valueOf(location.getTime());
                //速度不為0，紀錄每次座標及時間
                } else if (speed > 0) {
                    firstGPStime = false;
                    endx = location.getLatitude();
                    endy = location.getLongitude();
                    endtime = Long.valueOf(location.getTime());
                    distance += speed;
                    zerotime = 0;
                //本輪速度一旦為0，zerotime++
                } else if (speed==0){
                    zerotime++;
                }

                //假設偵測時間達一分鐘，輸出資料並進入新的一輪偵測
                if (endtime - starttime >= 60000) {
                    firstGPStime = true;
                    AllBegin = true;
                    zerotime = 0;
                    recorde();
                    startx = endx;
                    starty = endy;
                    starttime = endtime;
                    endx = 0.0;
                    endy = 0.0;
                    endtime = 0;
                    distance = 0;
                //假設本輪速度連續十秒為0，則輸出資料並進入新的一輪偵測
                } else if (zerotime > 10) {
                    firstGPStime = true;
                    AllBegin = true;
                    zerotime = 0;
                    recorde();
                    startx = endx;
                    starty = endy;
                    starttime = endtime;
                    endx = 0.0;
                    endy = 0.0;
                    endtime = 0;
                    distance = 0;
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }

    private String AllRoot = Environment.getExternalStorageDirectory().getPath() + "/RDataR";
    private int count = 0;
    private String query;
    private void recorde() {count++;
        Long costtime = endtime - starttime;
        if(saccount==null || saccount.equals("null"))
            read();
        final String fsquery = "http://140.116.82.102:8080/app/InsertNewGPSData.php?Account="+saccount+"&speed=" + speed + "&startlat=" + startx + "&startlng=" + starty + "&endlat=" + endx + "&endlng=" + endy + "&starttime=" + starttime + "&endtime=" + endtime + "&distance=" + distance + "&costtime=" + costtime;
        query = fsquery;


        new Thread(new Runnable() {
            public void run() {
                String result = DBConnector.executeQuery(query);
            }
        }).start();
    }

    private String saccount;
    private void read() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/RDataR/";
        String myData = "";
        try {
            FileInputStream fis = new FileInputStream(path + "user.txt");
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if(strLine.contains("帳號"))
                    myData = strLine;
            }
            saccount = myData.replace("帳號:","");
            in.close();
        } catch (Exception e) {
        }
    }

    private long ComputeCosttime(long starttime, long endtime) {
        long costtime = (endtime - starttime) / 1000;

        return costtime;
    }

    public double GetDistance(double Lat1, double Long1, double Lat2, double Long2) {
        double Lat1r = ConvertDegreeToRadians(Lat1);
        double Lat2r = ConvertDegreeToRadians(Lat2);
        double Long1r = ConvertDegreeToRadians(Long1);
        double Long2r = ConvertDegreeToRadians(Long2);

        double R = 6371; // Earth's radius (km)
        double d = Math.acos(
                Math.sin(Lat1r) * Math.sin(Lat2r) + Math.cos(Lat1r) * Math.cos(Lat2r) * Math.cos(Long2r - Long1r)) * R;
        return d;
    }

    private double ConvertDegreeToRadians(double degrees) {
        return (Math.PI / 180) * degrees;
    }
}