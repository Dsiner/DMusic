package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.model.AlbumModel;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.repeatclick.OnClickFastListener;
import com.d.dmusic.mvp.fragment.SongFragment;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * 专辑适配器
 */
public class AlbumAdapter extends CommonAdapter<AlbumModel> {

    public AlbumAdapter(Context context, List<AlbumModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final AlbumModel item) {
        holder.setText(R.id.tv_album, item.album);
        holder.setText(R.id.tv_title, "" + item.count);
        holder.setViewOnClickListener(R.id.llyt_album, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", item.album);
                bundle.putInt("type", MusicDB.LOCAL_ALL_MUSIC);
                bundle.putInt("tab", 2);
                SongFragment songFragment = new SongFragment();
                songFragment.setArguments(bundle);

                MainActivity.replace(songFragment);
            }
        });
    }
}
