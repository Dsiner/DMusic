package com.d.music.component.aster;

/**
 * API
 * Created by D on 2017/7/14.
 */
public interface API {
    // Base API
    String API_BASE = "http://musicapi.qianqian.com/";

    String BASE_URL_NETEASE = "https://netease.api.zzsun.cc/";

    String ACCESS_TOKEN = "token";

    interface CommonHeader {
        String authorization = "Authorization";
        String userAgent = "User-Agent";
        String rtp = "rtp";
        String time = "time";
        String sign = "sign";
        String appName = "app_name";
        String platform = "platform";
        String appVersion = "app_version";
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

        String FROM_QIANQIAN = "qianqian";
        String VERSION = "2.1.0";
        String FORMAT_JSON = "json";

        String METHOD_CATEGORY = "baidu.ting.billboard.billCategory";
        String METHOD_GET_CATEGORY_LIST = "baidu.ting.radio.getCategoryList";
        String METHOD_GET_BILL_LIST = "baidu.ting.billboard.billList";
        String METHOD_SEARCH_SUGGESTION = "baidu.ting.search.suggestion";
        String METHOD_MUSIC_INFO = "baidu.ting.song.getInfos";
        String METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo";
        String METHOD_SEARCH_MUSIC = "baidu.ting.search.common";
        String METHOD_SEARCH_MERGE = "baidu.ting.search.merge";
        String METHOD_72_HOT_ARTIST = "baidu.ting.artist.get72HotArtist";
        String METHOD_ARTIST_SONGS = "baidu.ting.artist.getSongList";

        /**
         * GET/Baidu-热门歌手
         */
        interface HotArtists {
            String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
            String method = "method"; // baidu.ting.artist.get72HotArtist
            String from = "from"; // qianqian
            String version = "version"; // 2.1.0
            String format = "format"; // json
            String order = "order"; // 1
            String offset = "offset"; // 0
            String limit = "limit"; // 50
        }

        /**
         * GET/Baidu-歌手-歌曲列表
         */
        interface ArtistSongs {
            String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
            String method = "method";
            String from = "from";
            String version = "version";
            String format = "format";
            String order = "order";
            String tinguid = "tinguid";
            String offset = "offset";
            String limits = "limits";
        }
    }

    /**
     * GET/Baidu-歌手
     */
    interface TopArtists {
        String rtpType = BASE_URL_NETEASE + "toplist/artist";
        String offset = "offset";
        String limit = "limit";
    }

    /**
     * GET/Baidu-排行榜
     */
    interface BaiduBill {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
        String method = "method"; // baidu.ting.billboard.billCategory
        String operator = "operator"; // 1
        String kflag = "kflag"; // 2
        String format = "format"; // json
    }

    /**
     * GET/Baidu-排行榜-歌单详情
     */
    interface BaiduBillSongs {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
        String method = "method"; // baidu.ting.billboard.billList
        String type = "type"; // 1
        String offset = "offset"; // 0
        String size = "size"; // 10
    }

    /**
     * GET/Baidu-电台
     */
    interface RadioChannels {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
        String from = "from"; // qianqian
        String version = "version"; // 2.1.0
        String method = "method"; // baidu.ting.radio.getCategoryList
        String format = "format"; // json
    }

    /**
     * GET/Baidu-电台-歌单详情
     */
    interface RadioChannelSongs {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.radio.getChannelSong&format=json";
        String pn = "pn";
        String rn = "rn";
        String channelname = "channelname"; // pid
    }

    /**
     * GET/网易-MV-排行榜
     */
    interface MVTop {
        String rtpType = BASE_URL_NETEASE + "top/mv";
        String offset = "offset";
        String limit = "limit";
    }

    /**
     * GET/网易-MV-详情
     */
    interface MvDetailInfo {
        String rtpType = BASE_URL_NETEASE + "mv";
        String mvid = "mvid";
    }

    /**
     * GET/网易-MV-相似
     */
    interface SimilarMV {
        String rtpType = BASE_URL_NETEASE + "simi/mv";
        String mvid = "mvid";
    }

    /**
     * GET/网易-MV-评论
     */
    interface MVComment {
        String rtpType = BASE_URL_NETEASE + "comment/mv";
        String id = "id";
    }

    /**
     * GET/Baidu-歌曲详情
     */
    interface SongInfo {
        String rtpType = "http://music.baidu.com/data/music/links";
        String songIds = "songIds";
    }

    /**
     * GET/网易 - 热门搜索
     */
    interface HotSearch {
        String rtpType = BASE_URL_NETEASE + "search/hot";
    }

    /**
     * GET/百度 - 搜索
     */
    interface Search {
        String rtpType = Baidu.BASE_URL_BAIDU + "v1/restserver/ting";
        String method = "method"; // baidu.ting.search.merge
        String query = "query"; // 1
        String page_no = "page_no"; // 0
        String page_size = "page_size"; // 10
    }
}
