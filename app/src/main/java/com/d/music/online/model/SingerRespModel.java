package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * SingerRespModel
 * Created by D on 2018/8/11.
 */
public class SingerRespModel extends BaseRespModel {

    /**
     * list : list
     * code : 200
     */
    public ListBean list;
    public int code;

    public static class ListBean {

        /**
         * artists : artists
         * updateTime : 1533890745678
         * type : 1
         */
        public long updateTime;
        public int type;
        public List<SingerModel> artists;
    }
}
