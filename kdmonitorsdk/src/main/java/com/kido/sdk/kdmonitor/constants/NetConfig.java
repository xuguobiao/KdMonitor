package com.kido.sdk.kdmonitor.constants;

public class NetConfig {

    private NetConfig() {

    }

    /**
     * You Url
     */
    public static String ONLINE_URL = "http://www.baidu.com";

    /**
     * 数据上报Debug Url
     */
    public static final String URL = "http://www.baidu.com";

    /**
     * 请求超时时间
     */
    public static final int TIME_OUT_MS = 50 * 1000;

    /**
     * 重新请求时间
     */
    public static final int RETRY_TIMES = 3;

    /**
     * HEADERS_KEY
     */
    public static final String HEADERS_KEY = "data_head";

    /**
     * key
     */
    public static final String PARAMS_KEY = "data_body";


}
