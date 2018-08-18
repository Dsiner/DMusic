package com.d.music.module.events;

/**
 * MusicInfoEvent
 * Created by D on 2016/6/6.
 */
public class MusicInfoEvent {
    public String songName;
    public String artistName;
    public int status; // 当前播放状态 0:停止 1:播放 2:暂停
    public boolean isUpdateNotif = true; // 是否更新通知栏
}
