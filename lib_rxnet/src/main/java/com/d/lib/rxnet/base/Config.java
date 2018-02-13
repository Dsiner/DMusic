package com.d.lib.rxnet.base;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Config
 * Created by D on 2017/10/24.
 */
class Config {
    static String TAG_LOG = "RetrofitLog Back = ";
    static HttpLoggingInterceptor.Level LOG_LEVEL = HttpLoggingInterceptor.Level.BODY;

    /****************** Default ******************/
    static final String BASE_URL = "https://www.google.com/";
    static final long CONNECT_TIMEOUT = 10 * 1000;
    static final long READ_TIMEOUT = 10 * 1000;
    static final long WRITE_TIMEOUT = 10 * 1000;
    static final int RETRY_COUNT = 3;//默认重试次数
    static final long RETRY_DELAY_MILLIS = 3 * 1000;//默认重试间隔时间（毫秒）
}
