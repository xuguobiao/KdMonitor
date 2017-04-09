package com.kido.sdk.kdmonitor.core;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.kido.sdk.kdmonitor.constants.StaticsConfig;
import com.kido.sdk.kdmonitor.db.helper.DataConstruct;
import com.kido.sdk.kdmonitor.db.helper.StaticsAgent;
import com.kido.sdk.kdmonitor.model.DataBlock;
import com.kido.sdk.kdmonitor.service.Platform;
import com.kido.sdk.kdmonitor.util.JsonUtil;
import com.kido.sdk.kdmonitor.util.NetworkUtil;
import com.kido.sdk.kdmonitor.util.StatLog;

import java.io.InputStream;
import java.util.HashMap;

import cz.msebera.android.httpclient.util.EncodingUtils;


public class KdStaticsManagerImpl implements KdStaticsManager, KdObserverPresenter.ScheduleListener {

    private static final String TAG = "KdMonitor::StaticsM";

    private static KdStaticsManager sInstance;

    private static KdObserverPresenter paObserverPresenter;

    private Context mContext;

    private StaticsListener eventInterface;

    private KdStatiPollMgr statiPollMgr;

    HashMap<String, String> pageIdMaps = new HashMap<String, String>();

    public KdStaticsManagerImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean onInit(String appId, String channel, String fileName) {

        // init  ObserverPresenter
        paObserverPresenter = new KdObserverPresenter(this);

        // init StaticsAgent
        StaticsAgent.init(mContext);

        // init CrashHandler
        KdCrashHandler.getInstance().init(mContext);

        // load pageIdMaps
        pageIdMaps = getStatIdMaps(fileName);

        // init  StatiPoll
        statiPollMgr = new KdStatiPollMgr(this);
        // init Header
        return initHeader(appId, channel);
    }

    @Override
    public void onSend() {
        // report data to server
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                DataBlock dataBlock = StaticsAgent.getDataBlock();

                if (dataBlock.getApp_action().isEmpty() &&
                        dataBlock.getEvent().isEmpty() &&
                        dataBlock.getPage().isEmpty()) {
                    return;
                }
                StatLog.d(TAG, "TcStatfacr >> report is Start");
                KdUpLoadManager.getInstance(mContext).report(JsonUtil.toJSONString(dataBlock));
            }
        });

    }

    @Override
    public void onStore() {
        DataConstruct.storeEvents();
        DataConstruct.storePage();
    }

    @Override
    public void onRelease() {
        if (paObserverPresenter != null) {
            paObserverPresenter.destroy();
        }

        stopSchedule();

    }

    @Override
    public void onRecordAppStart() {
        //send
        onSend();
        // store appAction
        DataConstruct.storeAppAction("1");
    }

    @Override
    public void onRecordPageEnd() {
        DataConstruct.storeEvents();
        DataConstruct.storePage();
        if (paObserverPresenter != null) {
            paObserverPresenter.onStop(mContext);
        }
        stopSchedule();
    }

    @Override
    public void onRecordPageStart(Context context) {

        if (context == null) {
            return;
        }

        //startSchedule
        startSchedule();

        String pageId = checkValidId(context.getClass().getSimpleName());
        if (pageId == null || pageId.equals("")) {
            pageId = context.getClass().getSimpleName();
        }

        // init page
        onInitPage(pageId, null);

        if (paObserverPresenter != null) {
            paObserverPresenter.init(mContext);
        }

        if (paObserverPresenter != null) {
            paObserverPresenter.onStart(mContext);
        }
    }


    @Override
    public void onRecordAppEnd() {

        //recard APP exit
        DataConstruct.storeAppAction("2");

        onSend();

        onRelease();
    }

    @Override
    public void onInitPage(String... strings) {
        DataConstruct.initPage(mContext, eventInterface, strings[0], strings[1]);
    }

    @Override
    public void onPageParameter(String... strings) {
        DataConstruct.initPageParameter(strings[0], strings[1]);
    }


    @Override
    public void onInitEvent(String eventName) {
        DataConstruct.initEvent(eventInterface, eventName);
    }

    @Override
    public void onEventParameter(String... strings) {
        DataConstruct.onEvent(strings[0], strings[1]);
    }

    @Override
    public void onEvent(String eventName, HashMap<String, String> parameters) {
        DataConstruct.initEvent(this.eventInterface, eventName, parameters);
    }

    /**
     * init header
     */
    private boolean initHeader(String appId, String channel) {

        if (!KdHeadrHandle.isInit()) {
            return KdHeadrHandle.initHeader(mContext, appId, channel);
        }
        return false;

    }

    /**
     * onScheduleTimeOut
     */
    void onScheduleTimeOut() {

        StatLog.d(TAG, "onScheduleTimeOut  is sendData");
        onSend();
    }

    /**
     * startSchedule
     */
    public void startSchedule() {
        // if debug  time is 5 min
        if (StaticsConfig.DEBUG &&
                KdMonitor.sUploadPolicy == KdMonitor.UploadPolicy.UPLOAD_POLICY_DEVELOPMENT) {
            statiPollMgr.start(5 * 1000);
            StatLog.d(TAG, "Schedule is start");
        } else {
            if (NetworkUtil.isWifi(mContext)) {
                statiPollMgr.start(KdMonitor.getIntervalRealTime() * 60 * 1000);
            } else {
                statiPollMgr.start(KdMonitor.UPLOAD_TIME_THIRTY * 60 * 1000);
            }
        }
    }

    /**
     * checkValidId
     *
     * @param name activitiyname
     * @return pageId
     */
    private String checkValidId(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (name.length() <= 0) {
            return null;
        }

        return getPageId(name);
    }


    /**
     * getPageId
     *
     * @param clazz
     * @return
     */
    private String getPageId(String clazz) {
        if (pageIdMaps == null || pageIdMaps.isEmpty()) {
            return null;
        }
        return pageIdMaps.get(clazz);
    }

    /**
     * stop Schedule
     */
    public void stopSchedule() {

        StatLog.d(TAG, "stopSchedule()");

        statiPollMgr.stop();
    }

    @Override
    public void onStart() {
        StatLog.d(TAG, "startSchedule");

        startSchedule();

    }

    @Override
    public void onStop() {
        stopSchedule();
    }

    @Override
    public void onReStart() {
        // stopSchedule
        stopSchedule();
        // startSchedule
        startSchedule();
    }


    public HashMap<String, String> getStatIdMaps(String jsonName) {

        HashMap<String, String> map = null;
        String assetContent = getFromAsset(jsonName);
        if (assetContent != null) {
            try {
                map = (HashMap<String, String>) JSON.parseObject(assetContent, HashMap.class);
            } catch (Exception e) {
            }
        }
        return map;
    }

    public String getFromAsset(String fileName) {
        String result = "";
        try {
            InputStream in = mContext.getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
