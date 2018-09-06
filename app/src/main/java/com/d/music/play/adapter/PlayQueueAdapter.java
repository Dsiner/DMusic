package com.d.music.play.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.common.component.repeatclick.OnClickFastListener;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.component.greendao.bean.MusicModel;
import com.d.music.component.media.controler.MediaControler;

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
        holder.setText(R.id.tv_singer, "-- " + item.artistName);
        holder.setViewOnClickListener(R.id.llyt_queue, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MediaControler.getIns(mContext).play(position);
            }
        });
        holder.setViewOnClickListener(R.id.iv_delete, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                MediaControler.getIns(mContext).delete(position);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onCountChange(mDatas.size());
                }
            }
        });
    }

    public interface IQueueListener {
        void onPlayModeChange(int playMode);

        void onCountChange(int count);
    }
}
