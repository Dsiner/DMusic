package com.d.music.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d.lib.common.component.quickclick.QuickClick;
import com.d.lib.common.util.DimenUtils;
import com.d.music.R;

/**
 * SongHeaderView
 * Created by D on 2017/5/7.
 */
public class SongHeaderView extends LinearLayout implements View.OnClickListener {
    private FrameLayout flyt_header_song_play_all;
    private LinearLayout llyt_header_song_play_all;
    private TextView tv_header_song_count;
    private FrameLayout flyt_header_song_handler;
    private OnHeaderListener mOnHeaderListener;

    public SongHeaderView(Context context) {
        super(context);
        init(context);
    }

    public SongHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SongHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DimenUtils.dp2px(context, 50)));
        setOrientation(HORIZONTAL);
        View root = LayoutInflater.from(context).inflate(R.layout.module_local_layout_song, this);
        flyt_header_song_play_all = (FrameLayout) root.findViewById(R.id.flyt_header_song_play_all);
        llyt_header_song_play_all = (LinearLayout) root.findViewById(R.id.llyt_header_song_play_all);
        tv_header_song_count = (TextView) root.findViewById(R.id.tv_header_song_count);
        flyt_header_song_handler = (FrameLayout) root.findViewById(R.id.flyt_header_song_handler);
        flyt_header_song_play_all.setOnClickListener(this);
        llyt_header_song_play_all.setOnClickListener(this);
        flyt_header_song_handler.setOnClickListener(this);
    }

    public void setSongCount(int count) {
        tv_header_song_count.setText(String.format(getResources().getString(R.string.module_common_song_unit_format), count));
    }

    public void setVisibility(int resId, int visibility) {
        View v = findViewById(resId);
        if (v != null) {
            v.setVisibility(visibility);
        }
    }

    @Override
    public void onClick(View v) {
        if (QuickClick.isQuickClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.flyt_header_song_play_all:
            case R.id.llyt_header_song_play_all:
                if (mOnHeaderListener != null) {
                    mOnHeaderListener.onPlayAll();
                }
                break;

            case R.id.flyt_header_song_handler:
                if (mOnHeaderListener != null) {
                    mOnHeaderListener.onHandle();
                }
                break;
        }
    }

    public void setOnHeaderListener(OnHeaderListener listener) {
        this.mOnHeaderListener = listener;
    }

    public interface OnHeaderListener {
        void onPlayAll();

        void onHandle();
    }
}
