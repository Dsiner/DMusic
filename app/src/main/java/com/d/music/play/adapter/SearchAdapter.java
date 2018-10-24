package com.d.music.play.adapter;

import android.content.Context;

import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.d.music.R;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.List;

/**
 * SearchAdapter
 * Created by D on 2017/4/29.
 */
public class SearchAdapter extends CommonAdapter<MusicModel> {

    public SearchAdapter(Context context, List<MusicModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, final CommonHolder holder, final MusicModel item) {
        holder.setText(R.id.tv_tag, item.songName);
    }
}
