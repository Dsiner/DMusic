package com.d.dmusic.mvp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.model.FolderModel;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.mvp.fragment.SongFragment;
import com.d.xrv.adapter.CommonAdapter;
import com.d.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * Created by D on 2016/6/2.
 */
public class FolderAdapter extends CommonAdapter<FolderModel> {

    public FolderAdapter(Context context, List<FolderModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setDatas(List<FolderModel> datas) {
        if (mDatas != null && datas != null) {
            mDatas.clear();
            mDatas.addAll(datas);
        }
    }

    @Override
    public void convert(int position, CommonHolder holder, final FolderModel item) {
        holder.setText(R.id.tv_folder_dir, item.folder);
        holder.setText(R.id.tv_folder_count, "" + item.count);
        holder.setViewOnClickListener(R.id.llyt_folder, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", item.folder);
                bundle.putInt("type", MusicDB.LOCAL_ALL_MUSIC);
                SongFragment songFragment = new SongFragment();
                songFragment.setArguments(bundle);

                MainActivity.fManger.beginTransaction().replace(R.id.framement, songFragment)
                        .addToBackStack(null).commit();
            }
        });
    }
}