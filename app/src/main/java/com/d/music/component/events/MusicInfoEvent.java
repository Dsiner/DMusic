package com.d.music.component.events;

/**
 * MusicInfoEvent
 * Created by D on 2016/6/6.
 */
public class MusicInfoEvent {
    public final static int TYPE_STATE = 0;
    public final static int TYPE_LRC = 1;

    public int type;
    public String songName;
    public String artistName;

    /**
     * 当前播放状态 0:停止 1:播放 2:暂停
     */
    public int status;

    /**
     * 是否更新通知栏
     */
    public boolean isUpdateNotif = true;
}