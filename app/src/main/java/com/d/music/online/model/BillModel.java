package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseModel;

import java.util.List;

/**
 * BillModel - 百度排行榜
 * Created by D on 2017/4/29.
 */
public class BillModel extends BaseModel {

    /**
     * name : 新歌榜
     * type : 1
     * count : 4
     * comment : 该榜单是根据千千音乐平台歌曲每日播放量自动生成的数据榜单，统计范围为近期发行的歌曲，每日更新一次
     * web_url :
     * pic_s192 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_9a4fbbbfa50203aaa9e69bf189c6a45b.jpg
     * pic_s444 : http://hiphotos.qianqian.com/ting/pic/item/78310a55b319ebc4845c84eb8026cffc1e17169f.jpg
     * pic_s260 : http://hiphotos.qianqian.com/ting/pic/item/e850352ac65c1038cb0f3cb0b0119313b07e894b.jpg
     * pic_s210 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_dea655f4be544132fb0b5899f063d82e.jpg
     * content : [{"title":"卡路里（电影《西虹市首富》插曲）","author":"火箭少女101","song_id":"601427388","album_id":"601427384","album_title":"卡路里（电影《西虹市首富》插曲）","rank_change":"0","all_rate":"96,128,224,320,flac","biaoshi":"lossless"},{"title":"最好","author":"薛之谦","song_id":"601422013","album_id":"601422007","album_title":"最好","rank_change":"0","all_rate":"96,128,224,320,flac","biaoshi":"lossless"},{"title":"彩虹下面（电影《西虹市首富》推广曲）","author":"赵雷","song_id":"601414610","album_id":"601414607","album_title":"彩虹下面（电影《西虹市首富》推广曲）","rank_change":"2","all_rate":"96,128,224,320,flac","biaoshi":"lossless"},{"title":"风誓","author":"刘珂矣","song_id":"601510899","album_id":"601510896","album_title":"风誓","rank_change":"2","all_rate":"96,128,224,320,flac","biaoshi":"lossless,vip"}]
     */
    public String name;
    public int type;
    public int count;
    public String comment;
    public String web_url;
    public String pic_s192;
    public String pic_s444;
    public String pic_s260;
    public String pic_s210;
    public List<ContentBean> content;

    public static class ContentBean {

        /**
         * title : 卡路里（电影《西虹市首富》插曲）
         * author : 火箭少女101
         * song_id : 601427388
         * album_id : 601427384
         * album_title : 卡路里（电影《西虹市首富》插曲）
         * rank_change : 0
         * all_rate : 96,128,224,320,flac
         * biaoshi : lossless
         */
        public String title;
        public String author;
        public String song_id;
        public String album_id;
        public String album_title;
        public String rank_change;
        public String all_rate;
        public String biaoshi;
    }
}
