package com.b1b.js.erpandroid_nahuo.contract;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.b1b.js.erpandroid_nahuo.activity.ObtainPicFromPhone2;
import com.b1b.js.erpandroid_nahuo.activity.YhTakepic2Activity;
import com.b1b.js.erpandroid_nahuo.activity.YhTakepicActivity;
import com.b1b.js.erpandroid_nahuo.application.MyApp;

/**
 * Created by 张建宇 on 2020/8/6.
 */

public class YanhuoPicMgr {
    private Context mContext;
    public static String insertType = "YH";

    public YanhuoPicMgr(Context mContext) {
        this.mContext = mContext;
    }

    public void takePic(String pid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("上传图片：" + pid);
        builder.setItems(new String[]{"拍照", "从手机选择", "连拍"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                switch (which) {
                    case 0:
                        intent = new Intent(mContext, YhTakepicActivity
                                .class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        MyApp.myLogger.writeInfo(getClass() + "checkpage-take");
                        break;
                    case 1:
                        intent = new Intent(mContext, ObtainPicFromPhone2.class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("checkpage-obtain");
                        break;
                    case 2:
                        intent = new Intent(mContext, YhTakepic2Activity.class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("checkpage-take2");
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }


}
