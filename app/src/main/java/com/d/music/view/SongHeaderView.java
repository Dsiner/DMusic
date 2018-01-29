package com.d.music.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.music.R;
import com.d.commen.module.repeatclick.ClickUtil;
import com.d.commen.utils.Util;

/**
 * SongHeaderView
 * Created by D on 2017/5/7.
 */
public class SongHeaderView extends LinearLayout implements View.OnClickListener {
    private FrameLayout flytPlayAll;
    private LinearLayout llytPlayAll;
    private TextView tvSongCount;
    private FrameLayout flytHandler;
    private OnHeaderListener listener;

    public SongHeaderView(Context context) {
        this(context, null);
    }

    public SongHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SongHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Util.dip2px(context, 50)));
        setOrientation(HORIZONTAL);
        View root = LayoutInflater.from(context).inflate(R.layout.layout_song, this);
        flytPlayAll = (FrameLayout) root.findViewById(R.id.flyt_header_song_play_all);
        llytPlayAll = (LinearLayout) root.findViewById(R.id.llyt_header_song_play_all);
        tvSongCount = (TextView) root.findViewById(R.id.tv_header_song_count);
        flytHandler = (FrameLayout) root.findViewById(R.id.flyt_header_song_handler);
        flytPlayAll.setOnClickListener(this);
        llytPlayAll.setOnClickListener(this);
        flytHandler.setOnClickListener(this);
    }

    public void setSongCount(int count) {
        tvSongCount.setText(count + "首");
    }

    public void setVisibility(int resId, int visibility) {
        View v = findViewById(resId);
        if (v != null) {
            v.setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.flyt_header_song_play_all:
            case R.id.llyt_header_song_play_all:
                if (listener != null) {
                    listener.onPlayAll();
                }
                break;
            case R.id.flyt_header_song_handler:
                if (listener != null) {
                    listener.onHandle();
                }
                break;
        }
    }

    public interface OnHeaderListener {
        void onPlayAll();

        void onHandle();
    }

    public void setOnHeaderListener(OnHeaderListener listener) {
        this.listener = listener;
    }
}
