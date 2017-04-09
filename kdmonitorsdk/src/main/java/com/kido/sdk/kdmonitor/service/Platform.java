package com.kido.sdk.kdmonitor.service;

import android.os.Build;

import java.util.concurrent.Executor;

public class Platform {
    private static final Platform PLATFORM = findPlatform();

    public static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return new Platform();
    }

    public Executor defaultCallbackExecutor() {
        return new KdHandleThreadPool().getExecutor();
    }

    public Object execute(Runnable runnable) {
        defaultCallbackExecutor().execute(runnable);
        return null;
    }

    static class Android extends Platform {
        @Override
        public Executor defaultCallbackExecutor() {
            return new KdHandleThreadPool().getExecutor();
        }

    }

}
