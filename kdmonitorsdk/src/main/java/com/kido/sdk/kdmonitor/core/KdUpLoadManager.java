package com.kido.sdk.kdmonitor.core;

import android.content.Context;
import android.text.TextUtils;

import com.kido.sdk.kdmonitor.constants.NetConfig;
import com.kido.sdk.kdmonitor.db.helper.StaticsAgent;
import com.kido.sdk.kdmonitor.http.KdHttpClient;
import com.kido.sdk.kdmonitor.service.Platform;
import com.kido.sdk.kdmonitor.util.JsonUtil;
import com.kido.sdk.kdmonitor.util.NetworkUtil;
import com.kido.sdk.kdmonitor.util.StatLog;

import java.util.concurrent.atomic.AtomicReference;

public class KdUpLoadManager implements IUpLoadlistener {


    private static final String TAG = KdNetEngine.class.getSimpleName();

    private static KdUpLoadManager sInstance;

    private Context mContext;

    private KdHttpClient mHttpClient;

    private Boolean isRunning = false;

    private AtomicReference<KdNetEngine> atomic;
    private KdNetEngine netEngine;

    /**
     * getInstance
     *
     * @param aContext context
     * @return UpLoadManager
     */
    public static synchronized KdUpLoadManager getInstance(Context aContext) {
        if (sInstance == null) {
            sInstance = new KdUpLoadManager(aContext);
        }
        return sInstance;
    }

    /**
     * constructor
     *
     * @param aContext context
     */
    private KdUpLoadManager(Context aContext) {
        mContext = aContext;
        init();
    }

    /**
     * init
     */
    private void init() {
        mHttpClient = getHttpclient();
        atomic = new AtomicReference<>();
        netEngine = new KdNetEngine(mContext, this);
    }


    /**
     * report
     */
    public void report(String jsonString) {

        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            return;
        }

        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        //netEngine.setHttpClient(getHttpclient());
        atomic.set(netEngine);
        atomic.getAndSet(netEngine).start(jsonString);
    }

    /**
     * cancel
     */
    public void cancle() {

        if (atomic.get() != null) {
            atomic.get().cancel();

        }

    }


    /**
     * get http client
     *
     * @return http client
     */
    public KdHttpClient getHttpclient() {
        if (mHttpClient == null) {
            // HttpClient
            mHttpClient = new KdHttpClient();
            mHttpClient.setTimeOut(NetConfig.TIME_OUT_MS);
        }
        return mHttpClient;

    }


    @Override
    public void onStart() {

        isRunning = true;
    }

    @Override
    public void onUpLoad() {

        isRunning = true;
    }

    @Override
    public void onSucess() {

        isRunning = false;
        // delete data
        StatLog.d(TAG, "DELETE  ：StaticsAgent.deleteTable()");
        // delete data
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                StaticsAgent.deleteData();
                StatLog.d(TAG, "delete after :>>>>>>" + JsonUtil.toJSONString(StaticsAgent.getDataBlock()));
            }
        });

    }

    @Override
    public void onFailure() {

        isRunning = false;

    }

    @Override
    public void onCancell() {

        isRunning = false;
    }
}
