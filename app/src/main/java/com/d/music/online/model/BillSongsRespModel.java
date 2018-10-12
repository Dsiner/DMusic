package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.List;

/**
 * BillSongsRespModel - 百度排行榜 - 歌曲列表
 * Created by D on 2018/8/11.
 */
public class BillSongsRespModel extends BaseRespModel {

    /**
     * song_list : song_list
     * billboard : billboard
     * error_code : 22000
     */
    public BillboardBean billboard;
    public int error_code;
    public List<BillSongsModel> song_list;

    /**
     * Extra
     */
    public List<MusicModel> datas;

    public static class BillboardBean {

        /**
         * billboard_type : 1
         * billboard_no : 2648
         * update_date : 2018-08-11
         * billboard_songnum : 113
         * havemore : 1
         * name : 新歌榜
         * comment : 该榜单是根据千千音乐平台歌曲每日播放量自动生成的数据榜单，统计范围为近期发行的歌曲，每日更新一次
         * pic_s192 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_9a4fbbbfa50203aaa9e69bf189c6a45b.jpg
         * pic_s640 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_a4aa99cf8bf218304de9786b6ba38982.jpg
         * pic_s444 : http://hiphotos.qianqian.com/ting/pic/item/78310a55b319ebc4845c84eb8026cffc1e17169f.jpg
         * pic_s260 : http://hiphotos.qianqian.com/ting/pic/item/e850352ac65c1038cb0f3cb0b0119313b07e894b.jpg
         * pic_s210 : http://business.cdn.qianqian.com/qianqian/pic/bos_client_dea655f4be544132fb0b5899f063d82e.jpg
         * web_url : http://music.baidu.com/top/new
         */
        public String billboard_type;
        public String billboard_no;
        public String update_date;
        public String billboard_songnum;
        public int havemore;
        public String name;
        public String comment;
        public String pic_s192;
        public String pic_s640;
        public String pic_s444;
        public String pic_s260;
        public String pic_s210;
        public String web_url;
    }
}
