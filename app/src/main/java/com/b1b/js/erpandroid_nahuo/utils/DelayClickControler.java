package com.b1b.js.erpandroid_nahuo.utils;

import android.view.View;

/**
 Created by 张建宇 on 2018/11/12. */
public abstract class DelayClickControler implements View.OnClickListener {
    private long time = System.currentTimeMillis();

    private int delaytime = 500;

    public DelayClickControler(int delaytime) {
        this.delaytime = delaytime;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - time < delaytime) {
            finalClick(v, true);
        } else {
            finalClick(v, false);
            time = System.currentTimeMillis();
        }
    }

    public abstract void finalClick(View v, boolean isFast);
}
