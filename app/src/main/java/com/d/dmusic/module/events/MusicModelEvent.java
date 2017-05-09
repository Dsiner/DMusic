package com.d.dmusic.module.events;

import com.d.dmusic.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * scan list
 * Created by D on 2017/5/9.
 */
public class MusicModelEvent {
    public int type;
    public List<MusicModel> list;

    public MusicModelEvent(int type, List<MusicModel> list) {
        this.type = type;
        this.list = list;
    }
}
