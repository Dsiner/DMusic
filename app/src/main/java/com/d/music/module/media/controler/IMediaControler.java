package com.d.music.module.media.controler;

import android.support.annotation.NonNull;

import com.d.music.module.greendao.bean.MusicModel;

import java.util.List;

/**
 * IMediaControler
 * Created by D on 2017/9/11.
 */
public interface IMediaControler {
    void init(@NonNull List<MusicModel> datas, int position, boolean play);

    @NonNull
    List<MusicModel> list();

    void play(int position);

    void start();

    void pause();

    boolean isPlaying();

    void seekTo(int msec);

    void prev();

    void next();

    void autoNext();

    void stop();

    void delete(int position);

    void deleteAll();
}
