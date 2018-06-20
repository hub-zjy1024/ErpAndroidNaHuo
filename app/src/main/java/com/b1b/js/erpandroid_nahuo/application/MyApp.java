package com.b1b.js.erpandroid_nahuo.application;

import android.app.Application;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import utils.LogRecoder;

/**
 * Created by 张建宇 on 2017/10/13.
 */

public class MyApp extends Application implements Thread.UncaughtExceptionHandler {
    public static List<Thread> totoalTask = new ArrayList<>();
    public static String id;
    public static String ftpUrl;
    public static LogRecoder myLogger;

    @Override
    public void onCreate() {
        super.onCreate();
        final String logFileName = "dyj_nahuo_log.txt";
        myLogger = new LogRecoder(logFileName);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ByteArrayOutputStream bao=new ByteArrayOutputStream();
        PrintWriter writer=new PrintWriter(bao);
        ex.printStackTrace(writer);
        writer.flush();
        String error="";
        try {
            error = new String(bao.toByteArray(), "utf-8");
            myLogger.writeError("Error uncatch exception:" +error);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.close();
        Log.e("zjy", "MyApp->uncaughtException(): detail==" + error);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
