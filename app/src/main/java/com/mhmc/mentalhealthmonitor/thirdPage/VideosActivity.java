package com.mhmc.mentalhealthmonitor.thirdPage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.mhmc.mentalhealthmonitor.LoginActivity;
import com.mhmc.mentalhealthmonitor.R;
import com.mhmc.mentalhealthmonitor.homepage;

import butterknife.ButterKnife;

public class VideosActivity extends Activity {
    Button logoutbutton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videos_layout);
        ButterKnife.bind(this);

        logoutbutton = (Button) findViewById(R.id.logout);
        logoutbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
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
}