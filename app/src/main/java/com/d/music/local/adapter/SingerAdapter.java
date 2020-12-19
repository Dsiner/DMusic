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
import com.d.music.local.model.SingerModel;

import java.util.List;

public class SingerAdapter extends CommonAdapter<SingerModel> {

    public SingerAdapter(Context context, List<SingerModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final SingerModel item) {
        holder.setText(R.id.tv_singer, item.singer);
        holder.setText(R.id.tv_title, "" + item.count);
        holder.setOnClickListener(R.id.llyt_singer, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MainActivity.getManger().replace(SongFragment.getInstance(AppDatabase.LOCAL_ALL_MUSIC,
                        AbstractLMFragment.TYPE_SINGER, item.singer));
            }
        });
    }
}
