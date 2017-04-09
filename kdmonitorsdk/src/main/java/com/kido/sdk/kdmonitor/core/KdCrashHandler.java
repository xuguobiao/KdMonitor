package com.kido.sdk.kdmonitor.core;

import android.content.Context;
import android.util.Log;

import com.kido.sdk.kdmonitor.db.helper.StaticsAgent;
import com.kido.sdk.kdmonitor.model.ExceptionInfo;
import com.kido.sdk.kdmonitor.util.DeviceUtil;


public class KdCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "KdMonitor::CrashHandler";

    private Context context;
    public static KdCrashHandler INSTANCE;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private KdCrashHandler() {
    }

    public void init(Context context) {
        this.context = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    public static KdCrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KdCrashHandler();
        }
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            Log.i(TAG, stackTraceElements.length + "---");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ex.getMessage()).append("\n");
            for (int i = stackTraceElements.length - 1; i >= 0; i--) {
                stringBuffer.append(stackTraceElements[i].getFileName()).append(":").append(stackTraceElements[i].getClassName()).append(stackTraceElements[i].getMethodName()).append("(").append(stackTraceElements[i].getLineNumber()).append(")").append("\n");
            }
            Log.i(TAG, stringBuffer.toString());
            StaticsAgent.storeObject(new ExceptionInfo(DeviceUtil.getPhoneModel(), DeviceUtil.getSystemModel(), String.valueOf(DeviceUtil.getSystemVersion()), stringBuffer.toString()));
            ex.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }
}
