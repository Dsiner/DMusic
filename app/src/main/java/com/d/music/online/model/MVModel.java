package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseModel;

import java.util.List;

/**
 * MVModel
 * Created by D on 2017/4/29.
 */
public class MVModel extends BaseModel {

    /**
     * id : 10752156
     * cover : http://p1.music.126.net/oXqI8JIjz1lwqPpEnsncXw==/109951163439522231.jpg
     * name : 心事
     * playCount : 721953
     * briefDesc : 徐秉龙新专辑首支单曲《零零》
     * desc : null
     * artistName : 徐秉龙
     * artistId : 1197168
     * duration : 0
     * mark : 0
     * lastRank : 10
     * score : 50281
     * subed : false
     * artists : [{"id":1197168,"name":"徐秉龙"}]
     */
    public int id;
    public String cover;
    public String name;
    public int playCount;
    public String briefDesc;
    public String desc;
    public String artistName;
    public int artistId;
    public int duration;
    public int mark;
    public int lastRank;
    public int score;
    public boolean subed;
    public List<ArtistsBean> artists;

    public static class ArtistsBean {

        /**
         * id : 1197168
         * name : 徐秉龙
         */
        public int id;
        public String name;
    }
}
