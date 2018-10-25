package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * SearchRespModel
 * Created by D on 2018/8/13.
 */
public class SearchRespModel extends BaseRespModel {

    /**
     * query : 偏爱
     * is_artist : 0
     * is_album : 1
     * rs_words :
     * pages : {"total":"34","rn_num":"34"}
     */
    public String query;
    public int is_artist;
    public int is_album;
    public String rs_words;
    public PagesBean pages;
    public AlbumBean album;
    public List<SongListBean> song_list;

    public static class PagesBean {

        /**
         * total : 34
         * rn_num : 34
         */
        public String total;
        public String rn_num;
    }

    public static class AlbumBean {

        /**
         * album_id : 22209394
         * title : 偏爱
         * pic_small : http://qukufile2.qianqian.com/data2/pic/1a037d20bc3b666c4354e70816fdd8cc/601821147/601821147.jpg@s_1,w_90,h_90
         * pic_big : http://qukufile2.qianqian.com/data2/pic/1a037d20bc3b666c4354e70816fdd8cc/601821147/601821147.jpg@s_1,w_150,h_150
         * publishtime : 2001-01-08
         * publishcompany : 龙乐唱片
         */
        public String album_id;
        public String title;
        public String pic_small;
        public String pic_big;
        public String publishtime;
        public String publishcompany;
    }

    public static class SongListBean {

        /**
         * title : 偏爱
         * song_id : 22209410
         * author : 环环
         * artist_id : 1985111
         * all_artist_id : 1985111
         * album_title : 偏爱
         * appendix :
         * album_id : 22209394
         * lrclink : /data2/lrc/8ccf52740efab2c7231a86d668e5bdc3/601823766/601823766.lrc
         * resource_type : 0
         * content :
         * relate_status : 0
         * havehigh : 0
         * copy_type : 1
         * del_status : 0
         * all_rate : 96,128
         * has_mv : 0
         * has_mv_mobile : 0
         * mv_provider : 0000000000
         * charge : 0
         * toneid : 0
         * info :
         * data_source : 0
         * learn : 0
         */
        public String title;
        public String song_id;
        public String author;
        public String artist_id;
        public String all_artist_id;
        public String album_title;
        public String appendix;
        public String album_id;
        public String lrclink;
        public int resource_type;
        public String content;
        public int relate_status;
        public int havehigh;
        public String copy_type;
        public String del_status;
        public String all_rate;
        public int has_mv;
        public int has_mv_mobile;
        public String mv_provider;
        public int charge;
        public String toneid;
        public String info;
        public int data_source;
        public int learn;
    }
}
