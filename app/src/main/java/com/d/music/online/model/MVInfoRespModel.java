package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

/**
 * MVInfoRespModel
 * Created by D on 2018/8/13.
 */
public class MVInfoRespModel extends BaseRespModel {

    /**
     * loadingPic :
     * bufferPic :
     * loadingPicFS :
     * bufferPicFS :
     * subed : false
     * data : data
     * code : 200
     */
    public String loadingPic;
    public String bufferPic;
    public String loadingPicFS;
    public String bufferPicFS;
    public boolean subed;
    public MVInfoModel data;
    public int code;
}
