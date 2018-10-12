package com.d.music.event.eventbus;

/**
 * Created by D on 2017/6/16.
 */
public class SortTypeEvent {
    public int type;
    public int orderType;

    public SortTypeEvent(int type, int orderType) {
        this.type = type;
        this.orderType = orderType;
    }
}
