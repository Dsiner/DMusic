package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseModel;

/**
 * MVDetailModel
 * Created by D on 2018/8/13.
 */
public class MVDetailModel extends BaseModel {
    public static final int TYPE_INFO = 0;
    public static final int TYPE_SIMILAR_HEAD = 1;
    public static final int TYPE_SIMILAR = 2;
    public static final int TYPE_COMMENT_HEAD = 3;
    public static final int TYPE_COMMENT = 4;

    public int view_type;

    public MVDetailModel() {
    }

    public MVDetailModel(int view_type) {
        this.view_type = view_type;
    }
}
