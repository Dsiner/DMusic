package com.d.dmusic.api;

/**
 * IQueueListener
 * Created by D on 2017/5/5.
 */
public interface IQueueListener {
    void onPlayModeChange();

    void onCountChange(int count);
}