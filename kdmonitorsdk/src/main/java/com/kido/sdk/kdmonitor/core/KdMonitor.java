package com.kido.sdk.kdmonitor.core;

import android.content.Context;

import com.kido.sdk.kdmonitor.constants.NetConfig;

import java.util.HashMap;

/**
 * 统计类，满足基础需求。部分高级功能待完善。
 */
public final class KdMonitor {

    /**
     * 实时发送
     */
    protected static final int UPLOAD_POLICY_REALTIME = 0;
    /**
     * 只在wifi下
     */
    protected static final int UPLOAD_POLICY_WIFI_ONLY = 2;
    /**
     * 批量上报 达到一定次数
     */
    protected static final int UPLOAD_POLICY_BATCH = 3;
    /**
     * 时间间隔
     */
    protected static final int UPLOAD_POLICY_INTERVAL = 4;
    /**
     * 开发者debug模式 调用就可以发送
     */
    protected static final int UPLOAD_POLICY_DEVELOPMENT = 5;
    /**
     * 每次启动 发送上次产生的数据
     */
    protected static final int UPLOAD_POLICY_WHILE_INITIALIZE = 6;

    /**
     * 上报策略模式
     */
    public enum UploadPolicy {
        /**
         * 实时发送
         */
        UPLOAD_POLICY_REALTIME,
        /**
         * 只在wifi下
         */
        UPLOAD_POLICY_WIFI_ONLY,
        /**
         * 批量上报 达到一定次数
         */
        UPLOAD_POLICY_BATCH,
        /**
         * 时间间隔
         */
        UPLOAD_POLICY_INTERVAL,
        /**
         * 开发者debug模式 调用就可以发送
         */
        UPLOAD_POLICY_DEVELOPMENT,
        /**
         * 每次启动 发送上次产生的数据
         */
        UPLOAD_POLICY_WHILE_INITIALIZE
    }

    /**
     * 实时发送
     */
    public static final int UPLOAD_INTERVAL_REALTIME = 0;
    /**
     * 1分钟
     */
    public static final int UPLOAD_TIME_ONE = 1;
    /**
     * 5分钟
     */
    public static final int UPLOAD_TIME_FIVE = 5;
    /**
     * 10分钟
     */
    public static final int UPLOAD_TIME_TEN = 10;
    /**
     * 20分钟
     */
    public static final int UPLOAD_TIME_TWENTY = 20;
    /**
     * 30分钟发送
     */
    public static final int UPLOAD_TIME_THIRTY = 30;
    /**
     * 上报策略
     */
    protected static UploadPolicy sUploadPolicy;

    private static int sIntervalRealTime = UPLOAD_TIME_FIVE;

    private static Context sContext;

    private KdMonitor() {

    }

    /**
     * 初始化。appId & channel & fileName 默认为空
     *
     * @param context
     */
    public static void initialize(Context context) {
        initialize(context, "", "", "");
    }

    /**
     * @param context  传ApplicationContext
     * @param appId    应用id，用于表示该应用（由于该统计sdk可能用于多个app）
     * @param channel  渠道号
     * @param fileName json格式文本内容的文件，用于表示页面类名对应的页面id
     */
    public static void initialize(Context context, String appId, String channel, String fileName) {

        sContext = context;

        KdStatSdk.getInstance(context).init(appId, channel, fileName);

    }

    /**
     * 设置策略模式
     *
     * @param policy 策略模式（实时模式下间隔时间无效）
     *               目前默认为UPLOAD_POLICY_INTERVAL模式
     * @param time   时间间隔（1 5 10 20 30分钟）
     */
    public static void setUploadPolicy(UploadPolicy policy, int time) {

        if (policy == null) {
            sUploadPolicy = UploadPolicy.UPLOAD_POLICY_INTERVAL;
            return;
        }

        if (time > 0 || time <= 60) {
            sIntervalRealTime = time;
        }
        sUploadPolicy = policy;

    }

    /**
     * getIntervalRealTime
     *
     * @return sIntervalRealTime
     */
    public static int getIntervalRealTime() {
        return sIntervalRealTime;
    }

    public static void setIntervalRealtime(int intervalRealTime) {
        KdMonitor.sIntervalRealTime = intervalRealTime;
    }

    /**
     * setUrl
     *
     * @param url
     */
    public static void setUrl(String url) {
        NetConfig.ONLINE_URL = url;
    }

    /**
     * record App Start
     */
    public static void onAppStart() {

        KdStatSdk.getInstance(sContext).recordAppStart();


    }

    /**
     * 关闭APP
     */
    public static void onAppEnd() {

        KdStatSdk.getInstance(sContext).recordAppEnd();

        KdStatSdk.getInstance(sContext).release();
    }

    /**
     * record Page Start
     */
    public static void onPageStart(Context context) {

        KdStatSdk.getInstance(context).recordPageStart(context);

    }

    /**
     * record Page End
     */
    public static void onPageEnd() {

        KdStatSdk.getInstance(sContext).recordPageEnd();

    }


    /**
     * 上报数据
     * 非Debug模式无法直接调用，请先设置为UPLOAD_POLICY_DEVELOPMENT
     */
    protected static void report() {

        KdStatSdk.getInstance(sContext).send();

    }

    /**
     * 上报数据
     * 非Debug模式无法直接调用，请先设置为UPLOAD_POLICY_DEVELOPMENT
     */
    public static void reportData() {


        if (sUploadPolicy != UploadPolicy.UPLOAD_POLICY_DEVELOPMENT) {

            throw new RuntimeException("call reportData(), you must will UploadPolicy set : UPLOAD_POLICY_DEVELOPMENT!");
        }

        report();

    }


//    /**
//     * 加入page参数
//     *
//     * @param k 业务名字
//     * @param v 对应值
//     */
//    public static void onPageParameter(String k, String v) {
//
//        KdStatSdk.getInstance(sContext).setPageParameter(k, v);
//
//    }

//    /**
//     * 初始化Event
//     */
//    public static void initEvent(String eventName) {
//
//        KdStatSdk.getInstance(sContext).initEvent(eventName);
//
//    }
//
//
//    /**
//     * 加入自定义envent
//     *
//     * @param k 业务名字
//     * @param v 对应值
//     */
//    public static void onEventParameter(String k, String v) {
//
//        KdStatSdk.getInstance(sContext).setEventParameter(k, v);
//
//    }
//
//    /**
//     * onEvent
//     */
//    public static void onEvent(String eventName, String k, String v) {
//        initEvent(eventName);
//        onEventParameter(k, v);
//
//    }

    public static void onEvent(String eventName, HashMap<String, String> parameters) {
        KdStatSdk.getInstance(sContext).onEvent(eventName, parameters);
    }

}
