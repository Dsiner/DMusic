package com.d.music.api;

/**
 * API
 * Created by D on 2017/7/14.
 */
public interface API {
    // Base API
    String API_BASE = "https://www.baidu.com/";

    String BASE_URL_NETEASE = "https://netease.api.zzsun.cc/";

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
     * 百度音乐
     */
    interface Baidu {
        String BASE_URL_BAIDU = "http://musicapi.qianqian.com/";
        String METHOD_CATEGORY = "baidu.ting.billboard.billCategory";
        String METHOD_GET_CATEGORY_LIST = "baidu.ting.radio.getCategoryList";
        String METHOD_GET_BILL_LIST = "baidu.ting.billboard.billList";
        String METHOD_SEARCH_SUGGESTION = "baidu.ting.search.suggestion";
        String METHOD_MUSIC_INFO = "baidu.ting.song.getInfos";
        String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
        String METHOD_SEARCH_MUSIC = "baidu.ting.search.common";
    }

    /**
     * GET/歌手
     */
    interface TopArtists {
        String rtpType = BASE_URL_NETEASE + "toplist/artist";
        String offset = "offset";
        String limit = "limit";
    }

    /**
     * GET/Baidu排行榜
     */
    interface BaiduBill {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
        String method = "method"; // baidu.ting.billboard.billCategory
        String operator = "operator"; // 1
        String kflag = "kflag"; // 2
        String format = "format"; // json
    }

    /**
     * GET/Baidu排行榜歌单详情
     */
    interface BaiduBillSongs {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting?";
        String method = "method"; // baidu.ting.billboard.billList
        String type = "type"; // 1
        String offset = "offset"; // 0
        String size = "size"; // 10
    }

    /**
     * GET/Baidu电台
     */
    interface RadioChannels {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
        String from = "from"; // qianqian
        String version = "version"; // 2.1.0
        String method = "method"; // baidu.ting.radio.getCategoryList
        String format = "format"; // json
    }

    /**
     * GET/Baidu电台歌单详情
     */
    interface RadioChannelSongs {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.radio.getChannelSong&format=json";
        String pn = "pn";
        String rn = "rn";
        String channelname = "channelname"; // pid
    }

    /**
     * GET/网易MV排行榜
     */
    interface MVTop {
        String rtpType = BASE_URL_NETEASE + "top/mv";
        String offset = "offset";
        String limit = "limit";
    }

    /**
     * GET/Baidu歌曲详情
     */
    interface SongInfo {
        String rtpType = "http://music.baidu.com/data/music/links";
        String songIds = "songIds";
    }
}
