package com.mhmc.mentalhealthmonitor.thirdPage.SQLite;


public class SQLite {
    private Boolean[] Day, Week;
    private String alarm_name, alert_time;
    public void Initial(String alarm_name,String alert_time, Boolean[] Day, Boolean[] Week){
        this.alarm_name = alarm_name;
        this.alert_time = alert_time;
        this.Day = Day;
        this.Week = Week;
    }
}
