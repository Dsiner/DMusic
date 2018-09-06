package com.d.music.component.events;

import com.d.music.component.greendao.bean.MusicModel;

import java.util.List;

/**
 * Scan list
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
