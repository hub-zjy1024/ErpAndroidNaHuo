package com.b1b.js.erpandroid_nahuo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.android.dev.BarcodeAPI;
import com.b1b.js.erpandroid_nahuo.activity.base.ToolbarActivity;
import com.b1b.js.erpandroid_nahuo.handler.NoLeakHandler;

import zxing.activity.CaptureActivity;

public abstract class BaseScanActivity extends ToolbarActivity implements NoLeakHandler.NoLeakCallback {
    private Handler scanHandler = new NoLeakHandler(this);

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case BarcodeAPI.BARCODE_READ:
                if (msg.obj != null) {
                    Log.e("zjy", "BaseScanActivity->handleMessage(): code==" + msg.obj.toString());
                    resultBack(msg.obj.toString());
                }
                break;
        }
    }

    boolean hasScanBtn = false;
    boolean hasInit = false;
    protected BarcodeAPI scanTool = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void resultBack(String result) {

    }

    public void getCameraScanResult(String result) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_MUTE:
                hasScanBtn = true;
                if (!hasInit) {
                    scanTool = BarcodeAPI.getInstance();
                    scanTool.open();
                    scanTool.m_handler = scanHandler;
                    hasInit = true;
                }
                scanTool.scan();
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public static final int REQ_CODE = 400;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            getCameraScanResult(result, requestCode);
            if (requestCode == REQ_CODE) {
                getCameraScanResult(result);
            }
        }
    }

    public void startScanActivity() {
        startActivityForResult(new Intent(this, CaptureActivity.class), REQ_CODE);
    }

    public void startScanActivity(int code) {
        startActivityForResult(new Intent(this, CaptureActivity.class), code);
    }

    public void getCameraScanResult(String result, int code) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanTool != null) {
            scanTool.close();
        }
    }

    public final void disbleScanService(Context mContext) {
        if (mContext == null)
            return;
        //禁用扫描服务中的扫描键
        Intent intent = new Intent("com.android.scanservice.scanbtnenable");
        intent.putExtra("scan_para", false);
        mContext.sendBroadcast(intent);
        //输出到焦点
        intent = new Intent("com.android.scanservice.output2focus");
        intent.putExtra("scan_para", false);
        mContext.sendBroadcast(intent);
        //取消开机启动
        intent = new Intent("AUTO_SCAN_BT_ISCAN");
        intent.putExtra("scan_para", false);
        mContext.sendBroadcast(intent);
    }
}
