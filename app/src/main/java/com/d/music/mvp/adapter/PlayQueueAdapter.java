package com.d.music.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.d.music.R;
import com.d.music.api.IQueueListener;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.lib.common.module.repeatclick.OnClickFastListener;
import com.d.music.module.service.MusicService;
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
