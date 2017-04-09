package com.kido.sdk.kdmonitor.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;

import com.kido.sdk.kdmonitor.constants.StaticsConfig;
import com.kido.sdk.kdmonitor.core.KdIntentManager;


/**
 * ScreenObserver
 */
public class KdScreenObserver extends BroadcastReceiver {

    private static final boolean DEBUG = StaticsConfig.DEBUG;
    private static final String LOG_TAG = KdScreenObserver.class.getSimpleName();

    private Context mContext;
    private IScreenListener mListener;

    public KdScreenObserver(Context aContext, IScreenListener aListener) {
        mContext = aContext;
        mListener = aListener;
    }

    public void start() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mContext.registerReceiver(this, filter);

            if (isScreenOn(mContext)) {
                if (mListener != null) {
                    mListener.onScreenOn(mContext);
                }
            } else {
                if (mListener != null) {
                    mListener.onScreenOff(mContext);
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "start Exception", e);
            }
        }
    }


    public void stop() {
        try {
            mContext.unregisterReceiver(this);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(LOG_TAG, "stop Exception", e);
            }
        }
    }

    /**
     * isScreenOn
     *
     * @param aContext Context
     */
    public boolean isScreenOn(Context aContext) {
        PowerManager pm = (PowerManager) aContext.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    @Override
    public void onReceive(Context aContext, Intent aIntent) {
        if (KdIntentManager.getInstance().isScreenOnIntent(aIntent)) {
            if (mListener != null) {
                mListener.onScreenOn(aContext);
            }
        } else if (KdIntentManager.getInstance().isScreenOffIntent(aIntent)) {
            if (mListener != null) {
                mListener.onScreenOff(aContext);
            }
        }
    }

    /**
     * IScreenListener
     */
    public interface IScreenListener {


        void onScreenOn(Context aContext);


        void onScreenOff(Context aContext);
    }

}
