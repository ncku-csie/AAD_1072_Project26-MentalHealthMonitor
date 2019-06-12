package com.mhmc.mentalhealthmonitor.MYSQL;

import java.util.ArrayList;


public class buffer {
    private static String name;
    private static String account;
    private static String password;
    private static String alert_question_voice;
    private static ArrayList<String[]> Emotions, phoneSeconds, phoneTimes, GPS;
    private static int type = 1,Max = 0,count=1;

    public static void setname(String Sname) {
        name = Sname;
    }

    public static void setaccount(String Saccount) {
        account = Saccount;
    }

    public static void setpassword(String Spassword) {
        password = Spassword;
    }

    public static void setgetcount(){ count++;}

    public static void setArraList(ArrayList<ArrayList<String[]>> arraList) {
        Emotions = arraList.get(0);
        phoneSeconds = arraList.get(1);
        phoneTimes = arraList.get(2);
        GPS = arraList.get(3);
    }

    public static void setGPSData(ArrayList<String[]> gps){
        GPS = gps;
    }

    public static void setAlert_question_voice(String offon){ alert_question_voice = offon;}

    public static String getName() {
        return name;
    }

    public static String getAccount() {
        return account;
    }

    public static String getPassword() {
        return password;
    }

    public static int getcount(){ return count; }

    public static ArrayList<String[]> getArrayList() {
        switch (type) {
            case 1:

                return Emotions;
            case 2:
                return phoneSeconds;
            case 3:
                return phoneTimes;
            case 4:
                return GPS;
            default:
                return null;
        }
    }

    public static void typeadd(){
        type++;
    }

    public static void typezero(){
        type = 1;
    }

    public static int getType(){
        return type;
    }

    public static void Renderer_Max(int value){
        Max = value;
    }

    public static int get_Renderer_Max(){
        return Max;
    }

    public static String getAlert_question_voice(){ return alert_question_voice;}
}
