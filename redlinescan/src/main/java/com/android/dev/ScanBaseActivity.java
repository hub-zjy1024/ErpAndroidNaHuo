package com.android.dev;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public abstract class ScanBaseActivity extends AppCompatActivity {
    protected Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BarcodeAPI.BARCODE_READ:
                    Log.e("zjy", "ScanBaseActivity->handleMessage(): jiguangBack==");
                    if (msg.obj != null) {
                        resultBack(msg.obj.toString());
                    }
                    break;
            }
        }
    };
    boolean hasScanBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(getLayoutResId());
    }
    public abstract int getLayoutResId();

    public abstract void resultBack(String result);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("zjy", "ScanBaseActivity->onKeyDown(): clickID==" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_MUTE:
                BarcodeAPI.getInstance().open();
                BarcodeAPI.getInstance().m_handler = scanHandler;
                BarcodeAPI.getInstance().scan();
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}
