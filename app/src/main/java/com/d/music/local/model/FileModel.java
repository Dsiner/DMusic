package com.d.music.local.model;

/**
 * 文件
 * Created by D on 2017/4/30.
 */
public class FileModel {
    public static final int TYPE_DIR = 1;// dir目录类型
    public static final int TYPE_FILTER = 2; // 目标过滤文件类型

    public String name; // 文件名
    public String postfix; // 文件后缀类型
    public int type; // 文件类型
    public int count; // 路径下目标过滤文件数
    public String absolutePath; // 文件绝对路径
    public boolean isEmptyDir; // 是否为空目录
    public boolean isChecked; // 是否选中
}