package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * SearchHotRespModel
 * Created by D on 2018/8/13.
 */
public class SearchHotRespModel extends BaseRespModel {

    /**
     * code : 200
     */
    public int code;
    public ResultBean result;

    public static class ResultBean {
        public List<HotsBean> hots;
    }

    public static class HotsBean {

        /**
         * first : 张楚新歌
         * second : 1
         * third : null
         * iconType : 0
         */
        public String first;
        public String second;
        public String third;
        public int iconType;

        public HotsBean(String first) {
            this.first = first;
        }
    }
}
