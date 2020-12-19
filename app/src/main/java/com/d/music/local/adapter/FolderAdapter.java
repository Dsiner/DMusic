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
import com.d.music.local.model.FolderModel;

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
        holder.setOnClickListener(R.id.llyt_folder, new OnAvailableClickListener() {
            @Override
            public void onAvailableClick(View v) {
                MainActivity.getManger().replace(SongFragment.getInstance(AppDatabase.LOCAL_ALL_MUSIC,
                        AbstractLMFragment.TYPE_FOLDER, item.folder));
            }
        });
    }
}