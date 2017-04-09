package com.kido.sdk.kdmonitor.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.kido.sdk.kdmonitor.model.header.AppInfo;
import com.kido.sdk.kdmonitor.model.header.DeviceInfo;
import com.kido.sdk.kdmonitor.model.header.HeaderInfo;
import com.kido.sdk.kdmonitor.model.header.NetworkInfo;
import com.kido.sdk.kdmonitor.util.DeviceUtil;
import com.kido.sdk.kdmonitor.util.NetworkUtil;

import java.util.List;
import java.util.Locale;

public class KdHeadrHandle {

    private static AppInfo sAppInfo;

    private static DeviceInfo sDeviceInfo;

    private static NetworkInfo sNetworkInfo;

    private static TelephonyManager sTelephonyMgr;

    private static HeaderInfo sHeaderInfo;

    private static boolean sIsInit;

    private static String sAppId;

    private static String sChannel;


    protected static boolean initHeader(Context context, String appId, String channel) {


        if (sHeaderInfo == null) {
            sAppId = appId;
            sChannel = channel;
            sNetworkInfo = new NetworkInfo();
            sHeaderInfo = new HeaderInfo(getAppInfo(context), getDeviceInfo(context), getNetWorkInfo(context));
            sIsInit = true;
        }

        return sIsInit;

    }

    public static boolean isInit() {
        return sIsInit;
    }


    protected static HeaderInfo getHeader(Context context) {

        if (sHeaderInfo == null) {
            return new HeaderInfo(getAppInfo(context), getDeviceInfo(context), getNetWorkInfo(context));
        }

        return sHeaderInfo;

    }

    /**
     * get AppInfo
     *
     * @param context
     */
    private static AppInfo getAppInfo(Context context) {

        if (sAppInfo != null) {
            return sAppInfo;
        }

        sAppInfo = new AppInfo();
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        String appLabel = "";

        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            sAppInfo.setApp_id(String.valueOf(sAppId));

            if (info != null) {
                sAppInfo.setApp_version(info.versionName);
            }
            sAppInfo.setApp_id(String.valueOf(sAppId));
            sAppInfo.setChannel(sChannel);
            sAppInfo.setSdk_version(DeviceUtil.getSdkCode());
            sAppInfo.setSdk_verson_name(DeviceUtil.getSdkName());

            return sAppInfo;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * get Device Info
     *
     * @param context
     */
    private static DeviceInfo getDeviceInfo(Context context) {

        if (sDeviceInfo != null) {
            return sDeviceInfo;
        }
        sDeviceInfo = new DeviceInfo();

        // 设备ID，

        sTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (sTelephonyMgr != null) {

            sDeviceInfo.setDevice_id(sTelephonyMgr.getDeviceId());
            // android Imei
            sDeviceInfo.setImei(sTelephonyMgr.getDeviceId());
        }

        // AndroidId
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            sDeviceInfo.setAndroid_id(androidId);
            if (TextUtils.isEmpty(sDeviceInfo.getImei())) {
                sDeviceInfo.setImei(androidId);
            }
        } catch (Exception e) {
            // do nothing. not use the data
        }

        sDeviceInfo.setMac(DeviceUtil.getMacAddress(context));

        sDeviceInfo.setModel(android.os.Build.MODEL);

        sDeviceInfo.setOs("Android");

        sDeviceInfo.setOs_version(android.os.Build.VERSION.RELEASE);

        // UniqueId
        String openId = sDeviceInfo.getDevice_id();
        if (openId == null || openId.trim().length() == 0) {
            openId = sDeviceInfo.getAndroid_id();
        }
        if (openId == null || openId.trim().length() == 0) {
            openId = sDeviceInfo.getMac();
        }

        sDeviceInfo.setOpenudid(openId);
        sDeviceInfo.setResolution(DeviceUtil.getScreenWidth(context) + "*" + DeviceUtil.getScreenHeight(context));
        sDeviceInfo.setDensity(String.valueOf(DeviceUtil.getScreenDensity(context)));
        sDeviceInfo.setLocale(Locale.getDefault().getLanguage());

        return sDeviceInfo;

    }

    /**
     * get NetWork Info
     *
     * @param context
     */
    protected static NetworkInfo getNetWorkInfo(Context context) {

        if (sNetworkInfo == null) {

            sNetworkInfo = new NetworkInfo();
        }
        sNetworkInfo.setIp_addr(NetworkUtil.getLocalIpAddress());

        sNetworkInfo.setWifi_ind(NetworkUtil.isWifi(context));

        if (sTelephonyMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
            sNetworkInfo.setCarrier(sTelephonyMgr.getSimOperatorName());
        }

        Location location = getLocation(context);
        if (location != null) {
            sNetworkInfo.setLatitude(String.valueOf(location.getLatitude()));
            sNetworkInfo.setLongitude(String.valueOf(location.getLongitude()));
        }

        return sNetworkInfo;
    }

    /**
     * 获取Location
     *
     * @param context
     * @return
     */
    private static Location getLocation(Context context) {
        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        String locationProvider;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {

            locationProvider = LocationManager.GPS_PROVIDER;
        }
        return locationManager.getLastKnownLocation(locationProvider);
    }

}
