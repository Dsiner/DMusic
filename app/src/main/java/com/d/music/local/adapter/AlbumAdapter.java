package com.d.music.local.adapter;

import android.content.Context;
import android.view.View;

import com.d.lib.common.component.quickclick.OnAvailableClickListener;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.data.database.greendao.db.AppDatabase;
import com.d.music.local.fragment.AbstractLMFragment;
import com.d.music.local.fragment.SongFragment;
import com.d.music.local.model.AlbumModel;

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
        holder.setOnClickListener(R.id.llyt_album, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MainActivity.getManger().replace(SongFragment.getInstance(AppDatabase.LOCAL_ALL_MUSIC,
                        AbstractLMFragment.TYPE_ALBUM, item.album));
            }
        });
    }
}
