package com.d.music.listener;

/**
 * IQueueListener
 * Created by D on 2017/5/5.
 */
public interface IQueueListener {
    void onPlayModeChange(int playMode);

    void onCountChange(int count);
}