package com.kido.sdk.kdmonitor.core;

import android.content.Context;
import android.os.Looper;

import com.kido.sdk.kdmonitor.constants.NetConfig;
import com.kido.sdk.kdmonitor.constants.StaticsConfig;
import com.kido.sdk.kdmonitor.http.KdHttpClient;
import com.kido.sdk.kdmonitor.util.JsonUtil;
import com.kido.sdk.kdmonitor.util.StatLog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.ParseException;

public class KdNetEngine {


    private Context context;

    private KdHttpClient mHttpClient;

    private String mKey;

    /**
     * 重试次数
     */
    protected int mRetrytimes = NetConfig.RETRY_TIMES;

    public static final String TAG = "KdMonitor::NetEngine";

    /**
     * 是否支持断点
     */
    protected boolean mCanContinue;

    private String mHostUrl = NetConfig.ONLINE_URL;

    private PaJsonHttpResponseHandler mTaskHandler;

    private IUpLoadlistener mUpLoadlistener;

    private HashMap<String, String> headers;

    private RequestParams requestParams;

    Header[] reqHeaders;

    Header header;

    public KdNetEngine(Context context, IUpLoadlistener upLoadlistener) {

        this(context, null, upLoadlistener);

    }

    public KdNetEngine(Context context, KdHttpClient httpClient, IUpLoadlistener upLoadlistener) {
        this.context = context;
        mHttpClient = httpClient;
        mCanContinue = true;
        mTaskHandler = new PaJsonHttpResponseHandler(true);
        mUpLoadlistener = upLoadlistener;
        init();


    }

    private void init() {

        if (StaticsConfig.DEBUG) {
            mHostUrl = NetConfig.URL;
        }
        headers = new HashMap<String, String>();
        requestParams = new RequestParams();
    }

    public KdHttpClient getHttpClient() {
        return mHttpClient;
    }

    public void setHttpClient(KdHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }

    public String start(final String... strings) {


        String str = JsonUtil.toJSONString(KdHeadrHandle.getHeader(context));

        StatLog.d(TAG, "head:" + str);
        if (headers.size() >= 0) {
            headers.clear();
        }
        headers.put(NetConfig.HEADERS_KEY, URLEncoder.encode(str));
        //headers.put("Accept", "application/json");

        requestParams.remove(NetConfig.PARAMS_KEY);

        requestParams.put(NetConfig.PARAMS_KEY, strings[0]);

        StatLog.d(TAG, "body:" + strings[0]);

        if (headers != null && headers.size() > 0) {
            reqHeaders = new Header[headers.size()];
            Set<String> keys = headers.keySet();
            int index = 0;
            for (final String mykey : keys) {
                header = new Header() {
                    @Override
                    public String getName() {
                        return mykey;
                    }

                    @Override
                    public String getValue() {
                        return headers.get(mykey);
                    }

                    @Override
                    public HeaderElement[] getElements() throws ParseException {
                        return new HeaderElement[0];
                    }
                };
                reqHeaders[index++] = header;
            }

        }


        KdHttpClient.post(context, mHostUrl, reqHeaders, requestParams, "application/json", mTaskHandler);
        return null;
    }

    void cancel() {

        KdHttpClient.cancel(mKey, true);
    }

    private class PaJsonHttpResponseHandler extends AsyncHttpResponseHandler {

        public PaJsonHttpResponseHandler() {
        }

        public PaJsonHttpResponseHandler(Looper looper) {
            super(looper);
        }

        public PaJsonHttpResponseHandler(boolean usePoolThread) {
            super(usePoolThread);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            if (mUpLoadlistener != null) {
                mUpLoadlistener.onSucess();
            }

            for (Header tmp : headers) {
                StatLog.d(TAG, tmp.getName() + ":" + tmp.getValue());
            }

            StatLog.d(TAG, "response code: " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                StatLog.d(TAG, "onSuccess");
                mCanContinue = false;
            } else if (statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                mCanContinue = true;
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (mUpLoadlistener != null) {
                mUpLoadlistener.onFailure();
            }
            cancel();
        }

    }

}
