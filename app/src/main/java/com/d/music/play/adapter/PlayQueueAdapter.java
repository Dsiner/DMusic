package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.common.component.quickclick.OnAvailableClickListener;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControl;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.List;

public class PlayQueueAdapter extends CommonAdapter<MusicModel> {
    private IQueueListener mIQueueListener;

    public PlayQueueAdapter(Context context, List<MusicModel> datas, int layoutId, IQueueListener listener) {
        super(context, datas, layoutId);
        this.mIQueueListener = listener;
    }

    @Override
    public void convert(final int position, CommonHolder holder, MusicModel item) {
        holder.setText(R.id.tv_song_name, item.songName);
        holder.setText(R.id.tv_singer, "-- " + item.artistName);
        holder.setOnClickListener(R.id.llyt_queue, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MediaControl.getInstance(mContext).play(position);
            }
        });
        holder.setOnClickListener(R.id.iv_delete, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MediaControl.getInstance(mContext).delete(position);
                notifyDataSetChanged();
                if (mIQueueListener != null) {
                    mIQueueListener.onCountChange(mDatas.size());
                }
            }
        });
    }

    public interface IQueueListener {
        void onPlayModeChange(int playMode);

        void onCountChange(int count);
    }
}
