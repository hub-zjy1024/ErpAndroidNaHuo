package com.b1b.js.erpandroid_nahuo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.b1b.js.erpandroid_nahuo.R;

public class SettingActivity extends AppCompatActivity {
    public static final String PREF_CAMERA_INFO = "cameraInfo";
    public static final String PREF_USERINFO = "UserInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }
}
