package com.d.dmusic.api;

import com.d.dmusic.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * Created by D on 2017/5/5.
 */
public interface IQueueListener {
    void onPlayModeChange();

    void onCountChange(int count);

    List<MusicModel> getQueue();
}