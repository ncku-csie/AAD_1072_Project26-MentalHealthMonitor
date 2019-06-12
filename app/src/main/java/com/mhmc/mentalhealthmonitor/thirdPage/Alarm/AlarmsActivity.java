package com.mhmc.mentalhealthmonitor.thirdPage.Alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.mhmc.mentalhealthmonitor.R;
import com.mhmc.mentalhealthmonitor.thirdPage.Settings.SettingsActivity;

public class AlarmsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startSettingsActivity());
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(AlarmsActivity.this, SettingsActivity.class);
        startActivityForResult(intent, 102);
    }

    /*
     * 寫RecyclerView
     * 統一從SQLite取資料
     */
    /*
     * SQLite還未創立
     */
}