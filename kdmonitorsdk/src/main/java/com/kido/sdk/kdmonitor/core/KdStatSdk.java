package com.kido.sdk.kdmonitor.core;

import android.content.Context;

import java.util.HashMap;

public class KdStatSdk {

    private static final String TAG = "KdMonitor::StatSdk";

    private static KdStatSdk sInstance;

    private Context mContext;

    private KdStaticsManager mStaticsManager;

    /**
     * getInstance
     *
     * @param aContext context
     * @return 返回 TcStaticsManager
     */
    protected static synchronized KdStatSdk getInstance(Context aContext) {
        if (sInstance == null) {
            sInstance = new KdStatSdk(aContext, new KdStaticsManagerImpl(aContext));
        }
        return sInstance;
    }

    /**
     * constructor
     *
     * @param context context
     */
    private KdStatSdk(Context context, KdStaticsManager staticsManager) {
        this.mContext = context;
        this.mStaticsManager = staticsManager;

    }

    protected void init(String appId, String channel, String fileName) {

        mStaticsManager.onInit(appId, channel, fileName);

    }

    protected void send() {

        mStaticsManager.onSend();
    }

    protected void store() {

        mStaticsManager.onStore();

    }

    protected void upLoad() {

        mStaticsManager.onSend();
    }

    /**
     * release
     */
    protected void release() {

        mStaticsManager.onRelease();

    }

    protected void recordPageEnd() {

        mStaticsManager.onRecordPageEnd();

    }

    protected void recordAppStart() {

        mStaticsManager.onRecordAppStart();

    }

    protected void recordAppEnd() {

        mStaticsManager.onRecordAppEnd();

    }

    protected void recordPageStart(Context context) {

        mStaticsManager.onRecordPageStart(context);

    }

    protected void setPageParameter(String k, String v) {

        mStaticsManager.onPageParameter(k, v);

    }

    protected void initEvent(String envntName) {

        mStaticsManager.onInitEvent(envntName);

    }

    protected void setEventParameter(String k, String v) {

        mStaticsManager.onEventParameter(k, v);

    }

    protected void onEvent(String eventName, HashMap<String, String> parameters) {
        this.mStaticsManager.onEvent(eventName, parameters);
    }

    protected void initPage(String pageId, String referPageId) {

        mStaticsManager.onInitPage(pageId, referPageId);

    }

}
