package com.d.music.component.media.controler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.List;

/**
 * IMediaControler
 * Created by D on 2017/9/11.
 */
public interface IMediaControl {
    void init(@NonNull List<MusicModel> datas, int position, boolean play);

    void overLoad(@NonNull List<MusicModel> list);

    @NonNull
    List<MusicModel> list();

    @Nullable
    MusicModel getModel();

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
