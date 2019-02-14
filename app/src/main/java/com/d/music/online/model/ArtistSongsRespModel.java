package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * BillSongsRespModel - 百度排行榜 - 歌手歌曲列表
 * Created by D on 2018/8/11.
 */
public class ArtistSongsRespModel extends BaseRespModel {

    /**
     * songlist :
     * songnums : 252
     * havemore : 1
     * error_code : 22000
     */
    public String songnums;
    public int havemore;
    public int error_code;
    public List<SonglistBean> songlist;

    public static class SonglistBean {

        /**
         * artist_id : 313
         * all_artist_ting_uid : 83561,88032951
         * all_artist_id : 313,58556
         * language : 国语
         * publishtime : 2005-12-30
         * album_no : 9
         * versions :
         * pic_big : http://qukufile2.qianqian.com/data2/pic/ce44502999bd4bf3d275430e41f91ed3/2461663/2461663.jpg@s_1,w_150,h_150
         * pic_small : http://qukufile2.qianqian.com/data2/pic/ce44502999bd4bf3d275430e41f91ed3/2461663/2461663.jpg@s_1,w_90,h_90
         * country : 港台
         * area : 1
         * lrclink : http://qukufile2.qianqian.com/data2/lrc/65702222/65702222.lrc
         * hot : 2037
         * file_duration : 268
         * del_status : 0
         * resource_type : 0
         * resource_type_ext : 2
         * copy_type : 1
         * relate_status : 0
         * all_rate : 32,64,128,256,320,flac
         * has_mv_mobile : 1
         * toneid : 600902000009169843
         * bitrate_fee : {"0":"129|-1","1":"-1|-1"}
         * biaoshi : lossless,vip,perm-3
         * info :
         * has_filmtv : 0
         * si_proxycompany : 华宇世博音乐文化（北京）有限公司-天中
         * song_id : 2072778
         * title : 输了你赢了世界又如何
         * ting_uid : 83561
         * author : 林志炫,詹兆源
         * album_id : 2461663
         * album_title : 原声之旅
         * is_first_publish : 0
         * havehigh : 2
         * charge : 0
         * has_mv : 1
         * learn : 0
         * song_source : web
         * piao_id : 0
         * korean_bb_song : 0
         * mv_provider : 1100000000
         * listen_total : 398
         * pic_radio : http://qukufile2.qianqian.com/data2/pic/ce44502999bd4bf3d275430e41f91ed3/2461663/2461663.jpg@s_1,w_300,h_300
         * pic_s500 : http://qukufile2.qianqian.com/data2/pic/ce44502999bd4bf3d275430e41f91ed3/2461663/2461663.jpg
         * pic_premium : http://qukufile2.qianqian.com/data2/pic/ce44502999bd4bf3d275430e41f91ed3/2461663/2461663.jpg
         * pic_huge :
         * album_500_500 : http://qukufile2.qianqian.com/data2/pic/ce44502999bd4bf3d275430e41f91ed3/2461663/2461663.jpg
         * album_800_800 :
         * album_1000_1000 :
         */
        public String artist_id;
        public String all_artist_ting_uid;
        public String all_artist_id;
        public String language;
        public String publishtime;
        public String album_no;
        public String versions;
        public String pic_big;
        public String pic_small;
        public String country;
        public String area;
        public String lrclink;
        public String hot;
        public String file_duration;
        public String del_status;
        public String resource_type;
        public String resource_type_ext;
        public String copy_type;
        public String relate_status;
        public String all_rate;
        public int has_mv_mobile;
        public String toneid;
        public String bitrate_fee;
        public String biaoshi;
        public String info;
        public String has_filmtv;
        public String si_proxycompany;
        public String song_id;
        public String title;
        public String ting_uid;
        public String author;
        public String album_id;
        public String album_title;
        public int is_first_publish;
        public int havehigh;
        public int charge;
        public int has_mv;
        public int learn;
        public String song_source;
        public String piao_id;
        public String korean_bb_song;
        public String mv_provider;
        public String listen_total;
        public String pic_radio;
        public String pic_s500;
        public String pic_premium;
        public String pic_huge;
        public String album_500_500;
        public String album_800_800;
        public String album_1000_1000;
    }
}
