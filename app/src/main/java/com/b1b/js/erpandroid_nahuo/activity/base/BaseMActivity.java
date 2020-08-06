package com.b1b.js.erpandroid_nahuo.activity.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.utils.callback.IBoolCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import utils.DialogUtils;
import utils.MyToast;


/**
 * Created by 张建宇 on 2019/3/20.
 */
public abstract class BaseMActivity extends AppCompatActivity {
    protected Context mContext;
    private ProgressDialog proDialog;
    private DialogUtils mdialog;
    public static final int reqPermissions = 321;
    public boolean isStoped=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mdialog = new DialogUtils(mContext);
//        ActivityRecoderDB recoderDB = ActivityRecoderDB.newInstance(this);
//        recoderDB.addRecord(getClass());
        isStoped=false;
//        int id= R.style.AppTheme_Dark;
//        setTheme(id);
    }

    private View[] mChildViews;
    private void updateColor(int color) {
//        mLeftNavigationView.setBackgroundColor(color);
//        mRightNavigationView.setBackgroundColor(color);
        if (mChildViews == null) {
            return;
        }
        for (View view : mChildViews) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else {
                view.setBackgroundColor(color);
            }
        }
    }
    //沉浸式状态栏
    private void initStatusBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        init();
        setListeners();
    }

    IBoolCallback mCallback;

    public void usePermission(String[] permissions, IBoolCallback tempCallback) {
        List<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String pm = permissions[i];
            int isGranted = ContextCompat.checkSelfPermission(this, pm);
            if (isGranted == PackageManager.PERMISSION_DENIED) {
                notGranted.add(pm);
            }
        }
        if (notGranted.size() > 0) {
            mCallback = tempCallback;
            String[] objects = new String[notGranted.size()];
            notGranted.toArray(objects);
            ActivityCompat.requestPermissions(this, objects, reqPermissions);
        } else {
            mCallback = tempCallback;
            mCallback.callback(true);
        }
    }

    public int getResColor(int id) {
        return getResources().getColor(id);
    }

    public int getResDimen(int id) {
        return (int) getResources().getDimension(id);
    }

    public String getResString(int id) {
        return getResources().getString(id);
    }

    public String[] getResStringArray(int id) {
        return getResources().getStringArray(id);
    }

    public String readRawToString(int rawId) {
        String content = "";
        InputStream is = null;
        try {
            is = getResources().openRawResource(rawId);
            StringBuilder stringBuilder = new StringBuilder();
            String temp = null;
            BufferedReader breader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            while ((temp = breader.readLine()) != null) {
                stringBuilder.append(temp);
            }
            content = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == reqPermissions) {
            List<String> notGranted = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                Log.d("zjy", getClass() + "->onRequestPermissionsResult():state=" +
                        "" + grantResult +
                        ",name=" + permissions[i]);
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    notGranted.add(permissions[i]);
                }
            }
            if (notGranted.size() > 0) {
                showMsgDialog("还有权限未被授予，请重启程序并授权");
            } else {
                if (mCallback != null) {
                    mCallback.callback(true);
                }
            }
        }
    }

    public <T extends View> T getViewInContent(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    public void useFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public abstract void init();

    public abstract void setListeners();

    public void setOnClickListener(View.OnClickListener listener, @IdRes int id) {
        View v = getViewInContent(id);
        v.setOnClickListener(listener);
    }

    public void showMsgDialogWithCallback(final String msg, final Dialog.OnClickListener listener) {
        if(isStoped){
            Log.w(getClass().getName()+" ", "isStop when showMsgDialog ,msg= "+msg );
            return;
        }
        if (mdialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mdialog.showMsgDialogWithCallback(msg, listener);
                }
            });
        }
    }

    public void showMsgDialog(final String msg) {
        if(isStoped){
            Log.w(getClass().getName()+" ", "isStop when showMsgDialog ,msg= "+msg );
            return;
        }
        if (mdialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mdialog.showAlertWithId(msg);
                }
            });
        }
    }

    public void showMsgToast(String msg) {
        final String fMSg = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.showToast(mContext, fMSg);
            }
        });
    }

    public void showMsgDialog(final String msg, final String title) {
        final String fMSg = msg;
        //执行在主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.getSpAlert(mContext, fMSg, title).show();
            }
        });
    }

    public Dialog getDialogById(int id) {
        return mdialog.getDialogById(id);
    }

    public int showProgressWithID(String msg) {
        if (isStoped) {
            return -1;
        }
        if (mdialog == null) {
            return -1;
        }
        return mdialog.showProgressWithID(msg);
    }

    public int showProgressWithID(String title, String msg) {
        if (mdialog == null) {
            return -1;
        }
        return mdialog.showProgressWithID(title, msg);
    }

    public void cancelAllDialog() {
        if (mdialog == null) {
            return ;
        }
        mdialog.cancelAll();
    }

    public void cancelDialogById(int pdId) {
        if (mdialog == null) {
            return ;
        }
        mdialog.cancelDialogById(pdId);
    }

    public void showProgress(String msg) {
        if (isStoped) {
            return;
        }
        if (mdialog == null) {
            return;
        }
        mdialog.showProgressWithID(msg);
    }

    public void showProgress(String msg, int progress) {
        proDialog = new ProgressDialog(this);
        proDialog.setTitle("请稍后");
        proDialog.setProgress(progress);
        proDialog.setMessage(msg);
        proDialog.show();
    }

    public void cancelProgress() {
        if (proDialog != null) {
            proDialog.cancel();
        }
        if (mdialog != null) {
            mdialog.cancelAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStoped = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStoped=false;
    }

    public TypedValue getThemeAttr(int resId) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(resId , typedValue, true);
        return typedValue;
    }

}
