package com.d.music.mvp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.model.FolderModel;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.repeatclick.OnClickFastListener;
import com.d.music.mvp.fragment.SongFragment;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;

import java.util.List;

/**
 * FolderAdapter
 * Created by D on 2016/6/2.
 */
public class FolderAdapter extends CommonAdapter<FolderModel> {

    public FolderAdapter(Context context, List<FolderModel> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(int position, CommonHolder holder, final FolderModel item) {
        holder.setText(R.id.tv_folder_dir, item.folder);
        holder.setText(R.id.tv_folder_count, "" + item.count);
        holder.setViewOnClickListener(R.id.llyt_folder, new OnClickFastListener() {
            @Override
            public void onFastClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", item.folder);
                bundle.putInt("type", MusicDB.LOCAL_ALL_MUSIC);
                bundle.putInt("tab", 3);
                SongFragment songFragment = new SongFragment();
                songFragment.setArguments(bundle);

                MainActivity.replace(songFragment);
            }
        });
    }
}