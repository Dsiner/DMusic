package com.d.music.component.media.media;

/**
 * MusicInfo
 * Created by D on 2017/4/29.
 */
public class MusicInfo {
    public long id; // 歌曲ID
    public String title; // 音乐标题
    public String displayName; // 显示名称(即文件名，包含后缀)
    public long artistId; // 艺术家ID
    public String artist; // 艺术家
    public long albumId;// 专辑ID
    public String album; // 专辑
    public long duration; // 时长
    public long size; // 文件大小
    public String url; // 文件路径

    public String lrcName; // 歌词名称
    public String lrcUrl; // 歌词路径
}
