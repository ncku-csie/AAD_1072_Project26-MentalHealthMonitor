package com.mhmc.mentalhealthmonitor.Phone;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.mhmc.mentalhealthmonitor.MYSQL.DBConnector;
import com.mhmc.mentalhealthmonitor.R;

import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * @author fanchangfa
 * Android電話監聽器
 */
public class Phone_listener extends Service {
    //螢幕休眠，service不休眠
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    public IBinder onBind(Intent arg) {
        // TODO Auto-generated method stub

        return null;
    }
    /****/
    private static final String CHANNEL_ID = "1250012";
    private static final String TAG = Phone_listener.class.getSimpleName();
    /****/
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        //取得電話管理服務
        TelephonyManager tele = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        //對呼叫狀態進行監聽
        tele.listen(new phone_state_listener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

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
                .setContentText(String.format("電話接收系統啟動"))//訊息
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
            startForeground(1, notification);
        }
        else {
            startForeground(startId, new Notification());
        }
        /****/
        //创建PowerManager对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        Toast.makeText(this, "電話掛掉，即將重啟", Toast.LENGTH_LONG).show();
        stopForeground(true);
        Intent localIntent = new Intent();
        localIntent.setClass(this, Phone_listener.class); //銷毀時重新啟動Service
        this.startService(localIntent);
    }

    private final class phone_state_listener extends PhoneStateListener {

        /*電話狀態有三種
         * . 來電
         * . 接通(通話中)
         * . 掛斷
         * */

        /* (non-Javadoc)
         * @see android.telephony.PhoneStateListener#onCallStateChanged(int, java.lang.String)
         * 狀態改變時執行
         */

        private String number, saccount;    //記錄來電號碼
        private MediaRecorder media;    //錄音對象

        private File recorder_file;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //來電狀態
                    //this.number = incomingNumber;
                    Log.i("start", "來電");
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //接通狀態

                    this.number = "12345";
                    /*
                     * 注意順序：先實例化存儲文件的目錄及格式，再對各項参數進行設置
                     * */
                    //實例化輸出目錄及文件名
                    recorder_file = new File(Environment.getExternalStorageDirectory(),
                            number+".3gp");

                    media = new MediaRecorder();    //實例化MediaRecorder對象

                    //設置錄音來源：MIC
                    media.setAudioSource(MediaRecorder.AudioSource.MIC);

                    //設置錄音格式为gp格式
                    media.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                    //設置MediaRecorder的編碼格式
                    media.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    //設置輸出目錄
                    media.setOutputFile(recorder_file.getAbsolutePath());

                    try {
                        media.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    media.start();
                    Log.i("start", "接通");
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    //掛斷電話
                    if (media != null) {    //停止錄音
                        media.stop();
                        media.release();
                        media = null;
                        sql();
                    }Log.i("start", "停止錄音");
                    break;
            }
        }

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

        private void sql() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                    Map<String, String> map = getDataList().get(0);
                    String mname = map.get("name");
                    String mnumber = map.get("number");
                    String mdate = map.get("date");
                    String mduration = map.get("duration");
                    String mtype = map.get("type");

                    String codename = "", codedate = "";
                    try {
                        codename = "" + URLEncoder.encode(mname, "UTF-8");
                        codedate = "" + URLEncoder.encode(mdate, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    read();

                    String query = "http://140.116.82.102:8080/app/InsertNewPhoneData.php?Account=" + saccount + "&name=" + codename + "&number=" + mnumber + "&date=" + codedate + "&second=" + mduration + "&type=" + mtype;
                    String result = DBConnector.executeQuery(query);
                    Log.i("接通",query);
                }

            }).start();
        }

        public List<Map<String, String>> getDataList() {
            // 1.获得ContentResolver
            ContentResolver resolver = getContentResolver();
            // 2.利用ContentResolver的query方法查询通话记录数据库
            /**
             * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
             * @param projection 需要查询的字段
             * @param selection sql语句where之后的语句
             * @param selectionArgs ?占位符代表的数据
             * @param sortOrder 排序方式
             *
             */
            if (ActivityCompat.checkSelfPermission(Phone_listener.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                    new String[]{CallLog.Calls.CACHED_NAME// 通话记录的联系人
                            , CallLog.Calls.NUMBER// 通话记录的电话号码
                            , CallLog.Calls.DATE// 通话记录的日期
                            , CallLog.Calls.DURATION// 通话时长
                            , CallLog.Calls.TYPE}// 通话类型
                    , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
            );
            // 3.通过Cursor获得数据
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                String date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(dateLong));
                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String typeString = "";
                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE:
                        typeString = "打入";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        typeString = "打出";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        typeString = "未接";
                        break;
                    default:
                        break;
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", (name == null) ? "無名稱" : name);
                map.put("number", number);
                map.put("date", date);
                map.put("duration", duration+ "");//秒
                map.put("type", typeString);
                list.add(map);
            }
            return list;
        }
    }
}