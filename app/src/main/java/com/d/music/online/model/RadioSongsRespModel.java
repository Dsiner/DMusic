package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.List;

/**
 * RadioSongsRespModel
 * Created by D on 2018/8/11.
 */
public class RadioSongsRespModel extends BaseRespModel {

    /**
     * error_code : 22000
     * result : result
     */
    public int error_code;
    public ResultBean result;

    /**
     * Extra
     */
    public List<MusicModel> datas;

    public static class ResultBean {

        /**
         * channel : 漫步春天
         * channelid : null
         * ch_name : public_tuijian_spring
         * artistid : null
         * avatar : null
         * count : null
         * songlist : songlist
         */
        public String channel;
        public String channelid;
        public String ch_name;
        public String artistid;
        public String avatar;
        public String count;
        public List<RadioSongsModel> songlist;
    }
}
