package com.d.music.local.model;

import com.d.lib.common.component.mvp.model.BaseModel;

/**
 * 歌手
 * Created by D on 2017/4/29.
 */
public class SingerModel extends BaseModel {
    public int id;
    public String singer; // 歌手名
    public int count; // 歌曲数
    public boolean isChecked; // 额外属性：是否选中
}
