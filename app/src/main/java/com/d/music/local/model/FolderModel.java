package com.d.music.local.model;

import com.d.lib.common.component.mvp.model.BaseModel;

/**
 * 文件夹
 * Created by D on 2017/4/29.
 */
public class FolderModel extends BaseModel {
    public int id;
    public String folder; // 路径
    public int count; // 歌曲数
    public boolean isChecked; // 额外属性：是否选中
}
