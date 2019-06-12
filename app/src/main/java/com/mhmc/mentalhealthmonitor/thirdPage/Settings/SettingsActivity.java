package com.mhmc.mentalhealthmonitor.thirdPage.Settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mhmc.mentalhealthmonitor.R;
import com.mhmc.mentalhealthmonitor.thirdPage.Alert.AlertActivity;
import com.mhmc.mentalhealthmonitor.thirdPage.SQLite.SQLite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SettingsActivity extends AppCompatActivity {
    private TextView tvDay, tvWeek;
    private TimePicker tvTime;
    private EditText etName;
    private Boolean[] Day = {false,false,false,false,false,false,false};
    private Boolean[] Week = {false,false};

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        refreshCurrentTimeViews();
    }

    private void refreshCurrentTimeViews() {
        tvDay = findViewById(R.id.alarmstn_day);
        tvWeek = findViewById(R.id.alarmact_week);
        tvTime = findViewById(R.id.TimePicker);
        etName = findViewById(R.id.alarmstn_name);

        Calendar cal = Calendar.getInstance();
        int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
        tvWeek.setText(getString(R.string.stn_now) + " " + weekOfMonth);

        long time=System.currentTimeMillis();
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒 EEEE");
        format=new SimpleDateFormat("EEEE");
        tvDay.setText(getString(R.string.stn_today) + " " + format.format(date));

        format=new SimpleDateFormat("HH");
        tvTime.setCurrentHour(Integer.valueOf(format.format(date)));
        format=new SimpleDateFormat("mm");
        tvTime.setCurrentMinute(Integer.valueOf(format.format(date)));
    }

    /**
         * 啟動鬧鐘
         *
         * 1.判斷本次鬧鐘選擇奇偶週
         * 2.判斷本次鬧鐘選擇哪幾日
         * 3.先判斷今天星期幾(這裡獨立function)
         * 再設置鬧鐘
         *
         * 最後確定鬧鐘名稱，預設空值
         * 並儲存至SQLite
         *
         */
    public void startAlarmClick(View view)
    {
        //取得下次響鈴時間，毫秒
        int timeup = calculate_time();
        Log.i("calculate_time", timeup+"");
        //獲取系統的鬧鐘服務
        AlarmManager am= (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //觸發鬧鐘的時間（毫秒）
        long triggerTime= System.currentTimeMillis() + timeup;
        Intent intent=new Intent(this,AlertActivity.class);
        PendingIntent op=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //只會執行一次鬧鐘
        //am.set(AlarmManager.RTC,triggerTime,op);

        //指定時間重複執行鬧鐘
        am.setRepeating(AlarmManager.RTC,triggerTime,86400000,op);


        SQLite sql = new SQLite();
        sql.Initial(etName.getText().toString(), tvTime.getCurrentHour()+":"+tvTime.getCurrentMinute(), Day, Week);
        finish();//關閉窗口
    }

    /**
         * 一週秒數604800秒
         * 一天86400秒
         *
         * calculate_time 計算單位秒，回傳單位毫秒
         */
    public int calculate_time(){
        int daysecond = countDaySecond();
        int weeksecond = countWeekSecond();
        int timeup = (daysecond + weeksecond)*1000;
        return timeup;
    }
    private int countDaySecond(){
        /***************Step1***************/
        int daysecond = 0;
        //取得今天星期幾
        int now_day = getDay();
        //取得該星期幾選項的Boolean
        int countFalse = 0;//判斷False有幾個
        ArrayList<Integer> Daytrue = new ArrayList<>();
        for(int i=Day.length;i>0;i--){
            if(Day[i-1]){
                Daytrue.add(i+1);
            }else{
                countFalse++;
            }
        }

        /***************Step2***************/
        int time;
        //if count False==7 ,then alert everyday
        if(countFalse==7 || Daytrue.size()==7){
            /**
                         * 把今天時間先加成明天同一時間，再以下次響鈴時間-明天同一時間
                         *      86400 + (settingtime - nowtime)
                         */

            /**取得目前時間*/
            long nowtime = System.currentTimeMillis();
            Date date=new Date(nowtime);
            SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒 EEEE");
            format=new SimpleDateFormat("HH:mm:ss");
            String[] split = format.format(date).split(":");
            int now_second = (Integer.valueOf(split[0])*3600) + (Integer.valueOf(split[1])*60) + Integer.valueOf(split[2]);
            /**取得下次響鈴時間*/
            int next_second = (tvTime.getCurrentHour()*3600) + (tvTime.getCurrentMinute()*60);

            //照公式計算
            daysecond = 86400 + ( next_second - now_second);
        }else{
            /**
                         * 先計算下次響鈴是星期幾
                         **/
            int nextday = -1;
            for(int i : Daytrue){
                if(now_day<i)
                    nextday = i;
            }

            /**
                         * 判斷下次響鈴星期有沒有超過今天星期
                         * 如果有超過今天
                         * example:
                         *     今天:星期3
                         *     下次響鈴:星期5
                         *
                         *     先轉成當天同一時間
                         *          天數差 = 5-3 = 2
                         *          秒數差 = 天數差 * 86400 = 2*86400
                         *     再以下次響鈴時間 - 當天同一時間
                         *          settingtime - nowtime
                         *
                         * 把今天時間先加成當天同一時間，再以下次響鈴時間-明天同一時間
                         * 公式如同上面公式
                         *     天數差*86400+(下次響鈴時間 - 明天同一時間) =
                         *        秒數差   + (settingtime - nowtime)
                         *
                         *
                         * 實際範例
                         *     今天星期3 13點30分20秒
                         *     下次響鈴星期5 8點40分15秒
                         *
                         *     天數差(轉秒數) + 下次響鈴時間-當天同一時間
                         *        2 * 86400   + (8*3600+40*60+15) - (13*3600+30*60+20) = 172800 + (-13765) = 159035 (秒)
                         *
                         **/

            /**得目前時間**/
            long nowtime = System.currentTimeMillis();
            Date date=new Date(nowtime);
            SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒 EEEE");
            format=new SimpleDateFormat("HH:mm:ss");
            String[] split = format.format(date).split(":");
            int now_second = (Integer.valueOf(split[0])*3600) + (Integer.valueOf(split[1])*60) + Integer.valueOf(split[2]);
            //取得下次響鈴時間
            int next_second = (tvTime.getCurrentHour()*3600) + (tvTime.getCurrentMinute()*60);


            /**判斷星期**/
            int daysub;
            if(nextday!=-1){
                /**下次響鈴星期大於今天，nextday不用重新設置*/
                daysub = nextday - now_day;//天數差 = 下次響鈴星期 - 今天
            } else if(nextday==now_day) {
                /**下次響鈴星期等於今天*/
                if(next_second <= now_second)//下次響鈴時間 < 目前時間
                    daysub = 7;
                else
                    daysub = 0;
            } else{
                /**下次響鈴星期少於今天*/
                nextday = Daytrue.get(Daytrue.size()-1);//下次響鈴星期
                daysub = 7 + nextday - now_day;//天數差 = 7 - 今天 + 下次響鈴星期
            }


            //轉成秒數
            int secondsub = daysub * 86400;
            //照公式計算
            daysecond = secondsub + ( next_second - now_second);
        }

        return daysecond;
    }
    private int countWeekSecond(){
        int weeksecond;
        Calendar cal = Calendar.getInstance();
        int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);

        //偶數(0) 奇數(1)
        if(Week[0] && !Week[1]){
            if(weekOfMonth%2==0)
                weeksecond = 0;
            else
                weeksecond = 604800;
        }else if(!Week[0] && Week[1]){
            if(weekOfMonth%2==0)
                weeksecond = 604800;
            else
                weeksecond = 0;
        }else{
            weeksecond = 0;
        }

        return weeksecond;
    }

    private int getDay(){
        int day = 0;

        long time=System.currentTimeMillis();
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒 EEEE");
        format=new SimpleDateFormat("EEEE");
        switch (format.format(date)+""){
            case "星期一":
                day = 1;
                break;
            case "星期二":
                day = 2;
                break;
            case "星期三":
                day = 3;
                break;
            case "星期四":
                day = 4;
                break;
            case "星期五":
                day = 5;
                break;
            case "星期六":
                day = 6;
                break;
            case "星期日":
                day = 7;
                break;
        }

        return day;
    }

    public void backAlarmClick(View view){
        finish();//關閉窗口
        //Intent intent = new Intent(SettingsActivity.this, AlarmsActivity.class);
        //startActivity(intent);
    }

    public void onDayButtonClick(View v){
        int day = Integer.parseInt(v.getTag().toString()) - 1;
        if(Day[day]){
            Day[day] = false;
        }else{
            Day[day] = true;
        }


        activateDateView(v, Day[day]);
    }

    //奇(1) 偶(0) 數週
    public void onWeekButtonClick(View v){
        boolean isWeekEven = v.getTag().toString().equals("even");
        int index;
        if(isWeekEven){
            index = 0;
        }else{
            index = 1;
        }


        if(Week[index]){
            Week[index] = false;
        }else{
            Week[index] = true;
        }

        activateDateView(v, Week[index]);
    }

    private void activateDateView(View v, boolean normal) {
        Log.i("activity  normal", normal+"");
        TransitionDrawable transition = (TransitionDrawable) v.getBackground();
        int animSpeed = getResources().getInteger(R.integer.animspeed_short);

        if (normal) transition.reverseTransition(animSpeed);
        else transition.startTransition(animSpeed);

        ((TextView) v).setTextColor(
                ContextCompat.getColor(
                        this,
                        ( normal ? android.R.color.tertiary_text_dark : android.R.color.white)));
    }
}
