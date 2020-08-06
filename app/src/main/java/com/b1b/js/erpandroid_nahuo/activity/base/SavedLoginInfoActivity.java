package com.b1b.js.erpandroid_nahuo.activity.base;

import android.os.Bundle;
import android.util.Log;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;

public class SavedLoginInfoActivity extends BaseMActivity {
    protected String loginID = MyApp.id;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("uid", loginID);
            outState.putString("ftpUrl", MyApp.ftpUrl);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            loginID = savedInstanceState.getString("uid");
            MyApp.id = loginID;
            MyApp.ftpUrl = savedInstanceState.getString("ftpUrl");
        }
        Log.d("zjy", "SavedLoginInfoActivity->onCreate(): nowID==" + loginID);
    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }
}
