package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * BillRespModel - 百度排行榜
 * Created by D on 2018/8/11.
 */
public class BillRespModel extends BaseRespModel {

    /**
     * content : content
     * error_code : 22000
     */
    public int error_code;
    public List<BillModel> content;
}
