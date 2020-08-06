package com.b1b.js.erpandroid_nahuo.activity.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.b1b.js.erpandroid_nahuo.R;

/**
 * Created by 张建宇 on 2020/8/6.
 */

public abstract class ToolbarActivity extends SavedLoginInfoActivity {
    protected Toolbar mToolbar;

    boolean enableToolbar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup mContent = findViewById(android.R.id.content);
        super.setContentView(layoutResID);
        if(enableToolbar){
            mToolbar = (Toolbar) LayoutInflater.from(mContext).inflate(R.layout.title_normal_toobar, null,
                    false);
            ViewGroup mContentView = (ViewGroup) mContent.getChildAt(0);
            mContentView.addView(mToolbar, 0);
            mToolbar.setTitle(getToobarTittle());
        }
    }

    public void setToolbarEnabled(boolean isEnabled) {
        enableToolbar = isEnabled;
    }
    public abstract  String getToobarTittle();
}
