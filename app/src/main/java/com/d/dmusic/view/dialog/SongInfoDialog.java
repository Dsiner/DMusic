package com.d.dmusic.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.d.dmusic.R;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.utils.Util;

/**
 * SongInfoDialog
 * Created by D on 2017/4/29.
 */
public class SongInfoDialog extends AbstractDialog implements View.OnClickListener {
    private TextView tvSongName, tvSinger, tvAlbum, tvDuration, tvFilePostfix, tvSize, tvUrl, tvOk;
    private MusicModel model;

    public SongInfoDialog(Context context, MusicModel model) {
        super(context);
        this.model = model;
        initInfo();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_song_info;
    }

    @Override
    protected void init(View rootView) {
        tvSongName = (TextView) rootView.findViewById(R.id.tv_song_name);
        tvSinger = (TextView) rootView.findViewById(R.id.tv_singer);
        tvAlbum = (TextView) rootView.findViewById(R.id.tv_album);
        tvDuration = (TextView) rootView.findViewById(R.id.tv_duration);
        tvFilePostfix = (TextView) rootView.findViewById(R.id.tv_filepostfix);
        tvSize = (TextView) rootView.findViewById(R.id.tv_size);
        tvUrl = (TextView) rootView.findViewById(R.id.tv_url);
        tvOk = (TextView) rootView.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(this);
    }

    private void initInfo() {
        tvSongName.setText(model.getSongName());
        tvSinger.setText(model.getSinger());
        tvAlbum.setText(model.getAlbum());
        long duration = model.getDuration() / 1000;
        long minute = duration / 60;
        long second = duration % 60;

        tvDuration.setText(String.format("%02d:%02d", minute, second));// 格式待转
        tvFilePostfix.setText(model.getFilePostfix());
        tvSize.setText(Util.formatSize(model.getSize()));// 格式待转
        tvUrl.setText(model.getUrl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                dismiss();
                break;
        }
    }
}
