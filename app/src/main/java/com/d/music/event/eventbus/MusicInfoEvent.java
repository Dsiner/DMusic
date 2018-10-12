package com.d.music.event.eventbus;

/**
 * MusicInfoEvent
 * Created by D on 2016/6/6.
 */
public class MusicInfoEvent {
    public int type;
    public String songName;
    public String artistName;

    /**
     * 当前播放状态, 0: 停止; 1: 播放; 2: 暂停
     */
    public int status;

    /**
     * 是否更新通知栏
     */
    public boolean isUpdateNotif = true;
}