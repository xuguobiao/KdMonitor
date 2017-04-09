package com.kido.sdk.kdmonitor.core;

import android.content.Context;

import java.util.HashMap;

public interface KdStaticsManager {

    boolean onInit(String appId, String channel, String fileName);

    void onSend();

    void onStore();

    void onRelease();

    void onRecordAppStart();

    void onRecordPageEnd();

    void onRecordPageStart(Context context);

    void onRecordAppEnd();

    void onInitPage(String... strings);

    void onPageParameter(String... strings);

    void onInitEvent(String eventName);

    void onEventParameter(String... strings);

    void onEvent(String var1, HashMap<String, String> var2);

}
