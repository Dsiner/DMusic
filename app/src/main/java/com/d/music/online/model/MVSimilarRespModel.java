package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseRespModel;

import java.util.List;

/**
 * MVSimilarRespModel
 * Created by D on 2018/8/13.
 */
public class MVSimilarRespModel extends BaseRespModel {
    /**
     * mvs : mvs
     * code : 200
     */
    public int code;
    public List<MVSimilarModel> mvs;
}
