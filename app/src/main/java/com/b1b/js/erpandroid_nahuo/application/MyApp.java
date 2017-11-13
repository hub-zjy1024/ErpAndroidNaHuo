package com.b1b.js.erpandroid_nahuo.application;

import android.app.Application;

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
        myLogger = new LogRecoder(logFileName, null);
        myLogger.init();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        StringBuilder sb = new StringBuilder();
        String exMsg = ex.getMessage();
        Throwable cause = ex.getCause();
        StackTraceElement[] stacks = ex.getStackTrace();
        sb.append(exMsg + "\n");
        if (cause != null) {
            sb.append("caused by:" + cause.getMessage() + "\n");
            StackTraceElement[] cStackTraces = cause.getStackTrace();
            for (StackTraceElement e : cStackTraces) {
                String className = e.getClassName();
                if (className.contains("b1b") || className.contains("utils") ||
                        className.contains("printer")
                        || className.contains("zhy")) {
                    sb.append(className + "." + e.getMethodName() + "(" + e.getFileName
                            () + ":" + e.getLineNumber() + ")\n");
                }
            }
        }
        for (StackTraceElement s : stacks) {
            String className = s.getClassName();
            if (className.contains("b1b") || className.contains("utils") || className
                    .contains("printer")
                    || className.contains("zhy")) {
                sb.append(className + "." + s.getMethodName() + "(" + s.getFileName() +
                        ":" + s.getLineNumber() + ")\n");
            }
        }
        myLogger.writeError("uncatch exception:" + sb.toString());
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
