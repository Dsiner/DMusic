package com.d.music.event.eventbus;

/**
 * RefreshEvent
 * Created by D on 2017/5/7.
 */
public class RefreshEvent {
    public static final int TYPE_INVALID = -1; // 无效的类型
    public static final int SYNC_CUSTOM_LIST = -100; // 刷新首页自定义歌曲列表
    public static final int SYNC_COLLECTIONG = -101; // 刷新收藏状态

    public int type;
    public int event;

    public RefreshEvent(int type, int event) {
        this.type = type;
        this.event = event;
    }
}
