package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.dmusic.R;
import com.d.dmusic.api.IQueueListener;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.dmusic.module.service.MusicService;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import java.util.List;

public class PlayQueueAdapter extends CommonAdapter<MusicModel> {
    private IQueueListener listener;

    public PlayQueueAdapter(Context context, List<MusicModel> datas, int layoutId, IQueueListener listener) {
        super(context, datas, layoutId);
        this.listener = listener;
    }

    @Override
    public void convert(final int position, CommonHolder holder, MusicModel item) {
        holder.setText(R.id.tv_song_name, item.songName);
        holder.setText(R.id.tv_singer, "-- " + item.singer);
        holder.setViewOnClickListener(R.id.llyt_queue, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MusicService.getControl(mContext).playPosition(position);
            }
        });
        holder.setViewOnClickListener(R.id.iv_delete, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MusicService.getControl(mContext).delelteByPosition(mContext, position);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onCountChange(mDatas.size());
                }
            }
        });
    }
}
