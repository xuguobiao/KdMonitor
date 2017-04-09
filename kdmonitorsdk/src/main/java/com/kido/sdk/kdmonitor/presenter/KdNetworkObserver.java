
package com.kido.sdk.kdmonitor.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.kido.sdk.kdmonitor.constants.StaticsConfig;


/**
 * NetworkObserver
 */
public class KdNetworkObserver extends BroadcastReceiver {

    private static final boolean DEBUG = StaticsConfig.DEBUG;
    private static final String LOG_TAG = KdNetworkObserver.class.getSimpleName();

    private Context mContext;

    private INetworkListener mListener;
    private boolean mIsNetworkAvailable;
    private boolean isRegistered;

    public KdNetworkObserver(Context aContext, INetworkListener aListener) {
        mContext = aContext;
        mListener = aListener;
        mIsNetworkAvailable = false;
        isRegistered = false;
    }

    /**
     * start,  onResume call
     */
    public void start() {

        if (isRegistered) {
            return;
        }
        try {
            mContext.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            isRegistered = true;
        } catch (Exception e) {
            isRegistered = false;
            if (DEBUG) {
                Log.e(LOG_TAG, "Start Exception", e);
            }
        }
    }

    /**
     * stop， onPause call
     */
    public void stop() {

        if (!isRegistered) {

            return;
        }
        try {
            mContext.unregisterReceiver(this);
            isRegistered = false;
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(LOG_TAG, "Stop Exception", e);
            }
            isRegistered = true;
        }
    }

    /**
     * Indicates whether network connectivity is possible.
     *
     * @param aContext Context
     * @return true if the network is available, false otherwise
     */
    public boolean isNetworkAvailable(Context aContext) {
        ConnectivityManager cm = (ConnectivityManager) aContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    @Override
    public void onReceive(Context aContext, Intent aIntent) {

        if (TextUtils.equals(aIntent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {

            boolean isAvailable = isNetworkAvailable(aContext);
            if (isAvailable) {
                if (!mIsNetworkAvailable && mListener != null) {
                    mListener.onNetworkConnected(aContext);
                }
            } else {
                if (mListener != null) {
                    mListener.onNetworkUnConnected(aContext);
                }
            }
            mIsNetworkAvailable = isAvailable;

        }
    }

    /**
     * INetworkListener
     */
    public interface INetworkListener {
        /**
         * Connected(
         *
         * @param aContext Context
         */
        void onNetworkConnected(Context aContext);


        /**
         * UnConnected
         *
         * @param aContext
         */
        void onNetworkUnConnected(Context aContext);
    }
}
