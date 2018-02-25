package com.d.music.api;

/**
 * API
 * Created by D on 2017/7/14.
 */
public interface API {
    // Base API
    String API_BASE = "https://www.baidu.com/";

    String ACCESS_TOKEN = "token";

    interface CommonHeader {
        String authorization = "Authorization";
        String userAgent = "User-Agent";
        String rtp = "rtp";
        String time = "time";
        String sign = "sign";
        String app_name = "app_name";
        String platform = "platform";
        String app_version = "app_version";
        String uid = "uid";
        String imei = "imei";
        String rom = "rom";
        String device = "device";
        String epid = "epid";
        String location = "location";
        String channel = "channel";
    }

    /**
     * GET/Test
     */
    interface MovieTop {
        String rtpType = "https://api.douban.com/v2/movie/top250";
        String start = "start";
        String count = "count";
    }
}
