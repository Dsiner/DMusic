package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * SingerRespModel
 * Created by D on 2018/8/11.
 */
public class SingerRespModel extends BaseRespModel {

    /**
     * artist :
     * nums : null
     * noFirstChar :
     * havemore : 1
     */
    public String nums;
    public String noFirstChar;
    public int havemore;
    public List<SingerModel> artist;
}
