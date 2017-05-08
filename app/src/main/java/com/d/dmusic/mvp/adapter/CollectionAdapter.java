package com.d.dmusic.mvp.adapter;

import android.content.Context;

import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.xrv.adapter.CommonAdapter;
import com.d.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * 我的收藏-列表Adapter
 *
 * @author D
 */
public class CollectionAdapter extends CommonAdapter<MusicModel> {

    public CollectionAdapter(Context context, List<MusicModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, MusicModel item) {

    }
}
