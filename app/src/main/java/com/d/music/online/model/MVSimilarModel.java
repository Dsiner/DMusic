package com.d.music.online.model;

import java.util.List;

/**
 * MVSimilarModel
 * Created by D on 2018/8/13.
 */
public class MVSimilarModel extends MVDetailModel {

    /**
     * id : 10732128
     * cover : http://p3.music.126.net/G-nWoC063QDgg5699Bjanw==/109951163398869994.jpg
     * name : 隔壁泰山
     * playCount : 3532482
     * briefDesc :
     * desc : null
     * artistName : 阿里郎
     * artistId : 11015
     * duration : 270000
     * mark : 0
     * artists : [{"id":11015,"name":"阿里郎","alias":[],"transNames":null}]
     * alg : itembased
     */
    public int id;
    public String cover;
    public String name;
    public int playCount;
    public String briefDesc;
    public Object desc;
    public String artistName;
    public int artistId;
    public int duration;
    public int mark;
    public String alg;
    public List<ArtistsBean> artists;

    public MVSimilarModel() {
        this.view_type = TYPE_SIMILAR;
    }

    public static class ArtistsBean {

        /**
         * id : 11015
         * name : 阿里郎
         * alias : []
         * transNames : null
         */
        public int id;
        public String name;
        public Object transNames;
        public List<?> alias;
    }
}
